package io.inoa.cnpm.auth.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nimbusds.jose.JOSEException;

import io.micronaut.security.token.jwt.generator.claims.JwtClaimsSetAdapter;
import io.micronaut.security.token.jwt.signature.jwks.DefaultJwkValidator;
import io.micronaut.security.token.jwt.signature.jwks.JwksSignature;
import io.micronaut.security.token.jwt.signature.jwks.JwksSignatureConfigurationProperties;
import io.micronaut.security.token.jwt.validator.ExpirationJwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.JwtClaimsValidator;
import io.micronaut.security.token.jwt.validator.NotBeforeJwtClaimsValidator;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to validate with JWTs against remote JWKs.
 */
@Singleton
@Slf4j
public class TokenRemoteService {

	private final Map<String, JwksSignature> jwkSignatures = new HashMap<>();
	private final List<JwtClaimsValidator> jwtClaimsValidator = List.of(
			new ExpirationJwtClaimsValidator(),
			new NotBeforeJwtClaimsValidator());

	/**
	 * Validate with remote signature.
	 *
	 * @param token Token wrapper with jwt and parsed claims.
	 * @return <code>true</code> if valid.
	 */
	@SneakyThrows(JOSEException.class)
	public boolean isValid(Token token) {

		var issuer = token.getIssuer().orElse(null);
		var jwks = jwkSignatures.computeIfAbsent(issuer, url -> {
			var configuration = new JwksSignatureConfigurationProperties();
			configuration.setUrl(url + "/protocol/openid-connect/certs");
			return new JwksSignature(configuration, new DefaultJwkValidator());
		});

		if (!jwks.verify(token.getJwt())) {
			log.info("Ignored token with invalid signature.");
			return false;
		}

		var claimsAdapter = new JwtClaimsSetAdapter(token.getClaims());
		if (!jwtClaimsValidator.stream().allMatch(v -> v.validate(claimsAdapter, null))) {
			log.info("Ignored token with invalid claims.");
			return false;
		}

		return true;
	}
}
