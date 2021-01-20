package io.kokuwa.fleet.registry.rest.gateway;

import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.slf4j.MDC;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.kokuwa.fleet.registry.ApplicationProperties;
import io.kokuwa.fleet.registry.auth.AuthTokenKeys;
import io.kokuwa.fleet.registry.auth.AuthTokenService;
import io.kokuwa.fleet.registry.auth.SignatureProvider;
import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link AuthApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthController implements AuthApi {

	public static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer";

	private final Clock clock;
	private final SignatureProvider signatureProvider;
	private final GatewayRepository gatewayRepository;
	private final AuthTokenService authService;
	private final AuthTokenKeys keys;
	private final ApplicationProperties applicationProperties;

	@Override
	public Single<HttpResponse<Object>> getKeys() {
		return Single.just(keys.getJwkSet()).map(JWKSet::toJSONObject).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<TokenRepsonseVO>> getToken(String grantType, String token) {
		if (!GRANT_TYPE.equals(grantType)) {
			throw error("grant_type is not " + GRANT_TYPE);
		}
		log.trace("Token: {}", token);
		return getGatewayFromToken(token)
				.map(this::getTokenResponse)
				.doOnTerminate(() -> MDC.remove("gateway"));
	}

	/**
	 * Parse token, validate claims, verify signature and return gateway.
	 *
	 * @param token JWT token.
	 * @return Maybe with gateway from token.
	 */
	private Single<Gateway> getGatewayFromToken(String token) {

		// parse token and get claim set

		SignedJWT jwt;
		JWTClaimsSet claims;
		try {
			jwt = SignedJWT.parse(token);
			claims = jwt.getJWTClaimsSet();
		} catch (ParseException e) {
			throw error("failed to parse token: " + e.getMessage());
		}

		// validate claims

		var gatewayId = validateClaims(claims);

		// get gateway and set mdc

		var gatewaySingle = gatewayRepository.findById(gatewayId)
				.doOnComplete(() -> {
					throw error("gateway " + gatewayId + " not found");
				})
				.doOnSuccess(gateway -> MDC.put("tenant", gateway.getTenant().getId().toString()))
				.toSingle().map(gateway -> {
					if (!gateway.getTenant().getEnabled()) {
						throw error("tenant " + gateway.getTenant().getId() + " disabled");
					}
					if (!gateway.getEnabled()) {
						throw error("gateway " + gatewayId + " disabled");
					}
					return gateway;
				});

		// filter if signature is invalid

		return gatewaySingle.flatMap(gateway -> signatureProvider.find(gateway)
				.any(signature -> {
					var verified = false;
					try {
						verified = signature.verify(jwt);
						log.debug("Token signature valid with {}: {}", signature, verified);
					} catch (JOSEException e) {
						log.debug("Failed to verify signature with {}: {}", signature, e.getMessage());
					}
					return verified;
				})
				.flatMap(verified -> {
					if (verified) {
						log.debug("Got gateway {} from token.", gateway.getName());
						return Single.just(gateway);
					}
					throw error("signature verification failed");
				}));
	}

	/**
	 * Validate claims
	 *
	 * @param token JWT token.
	 * @return Maybe with gateway from token.
	 * @see "https://tools.ietf.org/html/rfc7523#section-3"
	 */
	private UUID validateClaims(JWTClaimsSet claims) {

		var now = Instant.now(clock);
		var tokenProperties = this.applicationProperties.getGateway().getToken();

		// check issuer

		var issuer = claims.getIssuer();
		if (issuer == null) {
			throw error("token does not contain claim " + JwtClaims.ISSUER);
		}
		UUID gatewayId;
		try {
			gatewayId = UUID.fromString(issuer);
		} catch (IllegalArgumentException e) {
			throw error("token does not contain valid claim " + JwtClaims.ISSUER + ": " + issuer);
		}
		MDC.put("gateway", issuer);

		// check expiration

		var expirationTime = claims.getExpirationTime();
		if (expirationTime == null) {
			throw error("token does not contain claim " + JwtClaims.EXPIRATION_TIME);
		}
		if (now.isAfter(expirationTime.toInstant())) {
			throw error("token is expired: " + expirationTime.toInstant());
		}

		// check not before

		var notBefore = claims.getNotBeforeTime();
		if (notBefore == null && tokenProperties.isForceNotBefore()) {
			throw error("token does not contain claim " + JwtClaims.NOT_BEFORE);
		}
		if (notBefore != null && now.isBefore(notBefore.toInstant())) {
			throw error("token is not valid before: " + notBefore.toInstant());
		}

		// check issued at

		var issueTime = claims.getIssueTime();
		if (issueTime == null && tokenProperties.isForceIssuedAt()) {
			throw error("token does not contain claim " + JwtClaims.ISSUED_AT);
		}
		var issuedAtThreshold = tokenProperties.getIssuedAtThreshold();
		if (issueTime != null && issuedAtThreshold.map(now::minus)
				.filter(threshold -> threshold.isAfter(issueTime.toInstant()))
				.isPresent()) {
			throw error("token is too old (older than " + issuedAtThreshold.get() + "): " + issueTime.toInstant());
		}

		// check audience

		var audienceList = claims.getAudience();
		if (audienceList.isEmpty()) {
			throw error("token does not contain claim " + JwtClaims.AUDIENCE);
		}
		if (!audienceList.contains(tokenProperties.getAudience())) {
			throw error("token audience expected: " + tokenProperties.getAudience());
		}

		// check jwt id

		if (claims.getJWTID() == null && tokenProperties.isForceJwtId()) {
			throw error("token does not contain claim " + JwtClaims.JWT_ID);
		}

		return gatewayId;
	}

	/**
	 * Create response with access token for gateway.
	 *
	 * @param gateway Gateway.
	 * @return Response with payload.
	 */
	private HttpResponse<TokenRepsonseVO> getTokenResponse(Gateway gateway) {
		return HttpResponse.ok(new TokenRepsonseVO()
				.setAccessToken(authService.createToken(gateway.getId()))
				.setTokenType(HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER)
				.setExpiresIn(applicationProperties.getAuth().getExpirationDuration().getSeconds())
				.setConfigUri(applicationProperties.getConfigUri())
				.setConfigType(applicationProperties.getConfigType())
				.setTenantId(gateway.getTenant().getId()));
	}

	/**
	 * Construct error with description.
	 *
	 * @param errorDescription Human readable error description.
	 * @return RFC conform response.
	 */
	private HttpStatusException error(String errorDescription) {
		log.debug("Failure: {}", errorDescription);
		return new HttpStatusException(HttpStatus.BAD_REQUEST, new TokenErrorVO()
				.setError("invalid_grant")
				.setErrorDescription(errorDescription));
	}
}
