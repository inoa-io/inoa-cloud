package io.inoa.fleet.registry.auth;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Singleton;

import org.slf4j.MDC;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.inoa.fleet.FleetProperties;
import io.inoa.fleet.registry.domain.CredentialRepository;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.token.jwt.signature.SignatureConfiguration;
import io.micronaut.security.token.jwt.signature.rsa.RSASignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignature;
import io.micronaut.security.token.jwt.signature.secret.SecretSignatureConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Validate gateway token.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class GatewayTokenService {

	private final Clock clock;
	private final FleetProperties properties;
	private final GatewayRepository gatewayRepository;
	private final CredentialRepository credentialRepository;

	public Gateway getGatewayFromToken(String token) {

		// parse token and get claim set

		SignedJWT jwt;
		JWTClaimsSet claims;
		try {
			jwt = SignedJWT.parse(token);
			claims = jwt.getJWTClaimsSet();
		} catch (ParseException e) {
			throw error("failed to parse token: " + e.getMessage());
		}

		// validate claims and get gateway

		var gatewayId = validateClaimsAndReturnGatewayId(claims);
		var optionalGateway = gatewayRepository.findByGatewayId(gatewayId);
		if (optionalGateway.isEmpty()) {
			throw error("gateway " + gatewayId + " not found");
		}
		var gateway = optionalGateway.get();
		MDC.put("tenant", gateway.getTenant().getTenantId().toString());

		// check enabled & deleted

		if (gateway.getTenant().getDeleted() != null) {
			throw error("tenant " + gateway.getTenant().getTenantId() + " deleted");
		}
		if (!gateway.getTenant().getEnabled()) {
			throw error("tenant " + gateway.getTenant().getTenantId() + " disabled");
		}
		if (!gateway.getEnabled()) {
			throw error("gateway " + gatewayId + " disabled");
		}

		// filter if signature is invalid

		var verified = false;
		for (var signature : findSignatures(gateway)) {
			try {
				verified |= signature.verify(jwt);
				log.debug("Token signature valid with {}: {}", signature, verified);
			} catch (JOSEException e) {
				log.debug("Failed to verify signature with {}: {}", signature, e.getMessage());
			}
		}
		if (!verified) {
			throw error("signature verification failed");
		}

		log.debug("Got gateway {} from token.", gateway.getName());
		return gateway;
	}

	/**
	 * Validate claims and return gatewayId.
	 *
	 * @param token JWT token.
	 * @return GatewayId from token.
	 * @see "https://tools.ietf.org/html/rfc7523#section-3"
	 */
	private String validateClaimsAndReturnGatewayId(JWTClaimsSet claims) {

		var now = Instant.now(clock);
		var tokenProperties = properties.getGateway().getToken();

		// check issuer

		var gatewayId = claims.getIssuer();
		if (gatewayId == null) {
			throw error("token does not contain claim " + JWTClaimNames.ISSUER);
		}
		MDC.put("gateway", gatewayId);

		// check expiration

		var expirationTime = claims.getExpirationTime();
		if (expirationTime == null) {
			throw error("token does not contain claim " + JWTClaimNames.EXPIRATION_TIME);
		}
		if (now.isAfter(expirationTime.toInstant())) {
			throw error("token is expired: " + expirationTime.toInstant());
		}

		// check not before

		var notBefore = claims.getNotBeforeTime();
		if (notBefore == null && tokenProperties.isForceNotBefore()) {
			throw error("token does not contain claim " + JWTClaimNames.NOT_BEFORE);
		}
		if (notBefore != null && now.isBefore(notBefore.toInstant())) {
			throw error("token is not valid before: " + notBefore.toInstant());
		}

		// check issued at

		var issueTime = claims.getIssueTime();
		if (issueTime == null && tokenProperties.isForceIssuedAt()) {
			throw error("token does not contain claim " + JWTClaimNames.ISSUED_AT);
		}
		var issuedAtThreshold = tokenProperties.getIssuedAtThreshold();
		if (issueTime != null
				&& issuedAtThreshold
						.map(now::minus)
						.filter(threshold -> threshold.isAfter(issueTime.toInstant()))
						.isPresent()) {
			throw error(
					"token is too old (older than "
							+ issuedAtThreshold.get()
							+ "): "
							+ issueTime.toInstant());
		}

		// check audience

		var audienceList = claims.getAudience();
		if (audienceList.isEmpty()) {
			throw error("token does not contain claim " + JWTClaimNames.AUDIENCE);
		}
		if (!audienceList.contains(tokenProperties.getAudience())) {
			throw error("token audience expected: " + tokenProperties.getAudience());
		}

		// check jwt id

		if (claims.getJWTID() == null && tokenProperties.isForceJwtId()) {
			throw error("token does not contain claim " + JWTClaimNames.JWT_ID);
		}

		return gatewayId;
	}

	/**
	 * Construct error with description.
	 *
	 * @param errorDescription Human readable error description.
	 * @return Unauthorized response.
	 */
	private HttpStatusException error(String errorDescription) {
		log.debug("Failure: {}", errorDescription);
		return new HttpStatusException(HttpStatus.UNAUTHORIZED, errorDescription);
	}

	private List<SignatureConfiguration> findSignatures(Gateway gateway) {
		var signatures = new ArrayList<SignatureConfiguration>();

		var credentials = credentialRepository.findByGateway(gateway);
		if (credentials.isEmpty()) {
			log.debug("No credentials found");
			return List.of();
		}

		for (var credential : credentials) {

			if (!credential.getEnabled()) {
				log.debug(
						"Credential {} with name {} and type {} is disabled",
						credential.getCredentialId(),
						credential.getName(),
						credential.getType());
				continue;
			}

			switch (credential.getType()) {
				case PSK: {
					var config = new SecretSignatureConfiguration(credential.getName());
					config.setSecret(new String(credential.getValue()));
					signatures.add(new SecretSignature(config));
					break;
				}
				case RSA: {
					try {
						var key = (RSAPublicKey) KeyFactory.getInstance("RSA")
								.generatePublic(new X509EncodedKeySpec(credential.getValue()));
						signatures.add(new RSASignature(() -> key));
					} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
						log.error("Unable to parse public key.", e);
					}
					break;
				}
			}
		}

		if (signatures.isEmpty()) {
			log.debug("No valid signatures found");
		}

		return signatures;
	}
}
