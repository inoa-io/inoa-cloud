package io.inoa.cnpm.auth.service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.MDC;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.envoyproxy.envoy.service.auth.v3.AttributeContext.HttpRequest;
import io.inoa.cnpm.auth.ApplicationProperties;
import io.inoa.cnpm.tenant.management.IssuerVO;
import io.inoa.cnpm.tenant.management.TenantVO;
import io.inoa.cnpm.tenant.management.UserRoleVO;
import io.inoa.cnpm.tenant.management.UserVO;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authorization service for token exchange.
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class AuthorizationService {

	private static final String PREFIX = HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " ";

	private final ApplicationProperties properties;
	private final TokenLocalService localService;
	private final TokenRemoteService remoteService;
	private final DataClient data;

	public Optional<SignedJWT> exchangeToken(HttpRequest request) {
		var headers = request.getHeadersMap();

		var tokenOptional = parseToken(headers);
		if (tokenOptional.isEmpty()) {
			log.trace("No token found.");
			return Optional.empty();
		}
		var token = tokenOptional.get();

		// verify local signature

		if (localService.verify(token.getJwt())) {
			log.trace("Bybass local signature.");
			return Optional.of(token.getJwt());
		}

		// get tenant from request

		var tenantOptional = getTenantFromRequest(headers);
		var issuerOptional = tenantOptional.flatMap(tenant -> getIssuer(token, tenant));
		if (issuerOptional.isEmpty()) {
			return Optional.empty();
		}
		var tenant = tenantOptional.get();
		var issuer = issuerOptional.get();

		// verify remote signature and issuer

		if (!remoteService.isValid(token)) {
			log.info("Ignore request because issuer validation failed for token.");
			return Optional.empty();
		}

		// handle application token

		if (token.getEmail().isEmpty() && token.getAudience().stream().anyMatch(issuer.getServices()::contains)) {
			return Optional.of(exchange(token, tenant, null, UserRoleVO.ADMIN));
		}

		// handle user token

		return getUserFromClaims(token, tenant).map(user -> exchange(token, tenant, user.getUserId(), user.getRole()));
	}

	private Optional<Token> parseToken(Map<String, String> headers) {
		return Optional
				.ofNullable(headers.get(HttpHeaders.AUTHORIZATION.toLowerCase()))
				.filter(value -> value.startsWith(PREFIX))
				.map(value -> value.substring(PREFIX.length()))
				.flatMap(value -> {
					try {
						var token = new Token(value);
						MDC.put(JwtClaims.AUDIENCE, token.getAudience().toString());
						MDC.put(JwtClaims.ISSUER, token.getIssuer().orElse(null));
						MDC.put(Token.CLAIM_AZP, token.getAzp().orElse(null));
						return Optional.of(token);
					} catch (ParseException e) {
						log.info("Ignored token because parsing failed: {}", e.getMessage());
						return Optional.empty();
					}
				});
	}

	private Optional<TenantVO> getTenantFromRequest(Map<String, String> headers) {

		// get tenantId

		var tenantIdOptional = Optional
				.ofNullable(headers.get(properties.getTokenExchange().getHttpHeader()))
				.map(String::strip)
				.filter(StringUtils::isNotEmpty);
		if (tenantIdOptional.isEmpty()) {
			// probes are without tenantId header, so do not log expected event on info
			log.debug("Ignore request because tenantId header not found.");
			return Optional.empty();
		}
		var tenantId = tenantIdOptional.get();
		MDC.put(Token.CLAIM_TENANT_ID, tenantId);

		// get tenant

		var tenantOptional = data.findTenant(tenantId);
		if (tenantOptional.isEmpty()) {
			log.info("Ignore request because tenant {} is unknown.", tenantId);
			return Optional.empty();
		}
		if (!tenantOptional.get().getEnabled()) {
			log.info("Ignore request because tenant {} is disabled.", tenantId);
			return Optional.empty();
		}

		return tenantOptional;
	}

	private Optional<IssuerVO> getIssuer(Token token, TenantVO tenant) {

		// get issuer

		var issuerOptional = token.getIssuer();
		if (issuerOptional.isEmpty()) {
			log.info("Ignore request because issuer claim not found.");
			return Optional.empty();
		}
		var issuer = issuerOptional.get();

		// get matching issuer from tenant

		var tenantIssuer = tenant.getIssuers().stream().filter(i -> i.getUrl().toString().equals(issuer)).findFirst();
		if (tenantIssuer.isEmpty()) {
			log.info("Ignore request because issuer {} is invalid for tenant {}.", issuer, tenant.getTenantId());
			return Optional.empty();
		}

		return tenantIssuer;
	}

	private Optional<UserVO> getUserFromClaims(Token token, TenantVO tenant) {

		var email = token.getEmail();
		if (email.isEmpty()) {
			log.info("Ignore request because email claim not found.");
			return Optional.empty();
		}

		var user = data.findUser(tenant.getTenantId(), email.get());
		if (user.isEmpty()) {
			log.info("Ignore request because user email is unknown.");
		} else {
			MDC.put("userId", user.get().getUserId().toString());
		}

		return user;
	}

	public SignedJWT exchange(Token token, TenantVO tenant, UUID userId, UserRoleVO role) {
		log.debug("Exchange token for with issuer {}.", token.getIssuer());
		return localService.sign(new JWTClaimsSet.Builder(token.getClaims())
				.claim(Token.CLAIM_TENANT_ID, tenant.getTenantId())
				.claim(Token.CLAIM_USER_ID, userId == null ? null : userId.toString())
				.claim(Token.CLAIM_ROLES, List.of(role.getValue()))
				.issuer(properties.getTokenExchange().getIssuer())
				.issueTime(Date.from(Instant.now()))
				.build());
	}
}
