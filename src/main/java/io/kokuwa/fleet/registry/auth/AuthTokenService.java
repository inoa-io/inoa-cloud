package io.kokuwa.fleet.registry.auth;

import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Singleton;

import org.slf4j.MDC;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.kokuwa.fleet.registry.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to issue and validate registry tokens.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class AuthTokenService {

	private final Clock clock;
	private final ApplicationProperties properties;
	private final AuthTokenKeys keys;

	/**
	 * Create registry signed JWT for gateway.
	 *
	 * @param gatewayUuid Gateway uuid to create JWT for.
	 * @return Signed JWT.
	 * @see "https://tools.ietf.org/html/rfc7523#section-4"
	 */
	public String createToken(UUID gatewayUuid) {

		var now = Instant.now(clock);
		var properties = this.properties.getAuth();

		// claims for gateway

		var claims = new JWTClaimsSet.Builder()
				.subject(gatewayUuid.toString())
				.issuer(properties.getIssuer())
				.issueTime(Date.from(now))
				.notBeforeTime(Date.from(now))
				.expirationTime(Date.from(now.plus(properties.getExpirationDuration())))
				.audience(properties.getAudience())
				.jwtID(UUID.randomUUID().toString())
				.build();

		// sign token

		try {
			var key = keys.getSigningKey();
			var jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(key.getKeyID()).build(), claims);
			jwt.sign(new RSASSASigner(key));
			return jwt.serialize();
		} catch (JOSEException e) {
			throw new IllegalStateException("Failed to sign token.", e);
		}
	}

	public Optional<UUID> validateToken(String token) {
		try {

			// parse token

			var jwt = SignedJWT.parse(token);
			var claims = jwt.getJWTClaimsSet();

			// validate issuer to abort verification early

			if (!Objects.equals(claims.getIssuer(), properties.getAuth().getIssuer())) {
				return Optional.empty();
			}

			// set subject as mdc

			var subject = claims.getSubject();
			if (subject != null) {
				MDC.put("gateway", subject);
			}

			// verify expiration

			var expired = Optional.ofNullable(claims.getExpirationTime()).map(Date::toInstant);
			if (expired.isEmpty() || expired.get().isBefore(Instant.now(clock))) {
				log.debug("Token expired {} ago.", expired.orElse(null));
				return Optional.empty();
			}

			// verify token

			var keyId = jwt.getHeader().getKeyID();
			if (keyId == null) {
				log.debug("Failed to verify signature, key id is missing.");
				return Optional.empty();
			}
			var key = keys.getKey(keyId);
			if (key.isEmpty()) {
				log.debug("Failed to verify signature, key id {} unknown.", keyId);
				return Optional.empty();
			}
			if (!jwt.verify(new RSASSAVerifier(key.get()))) {
				log.debug("Failed to verify signature.");
				return Optional.empty();
			}

			return Optional.of(UUID.fromString(subject));
		} catch (ParseException e) {
			log.debug("Failed to parse token payload: {}", e.getMessage());
			return Optional.empty();
		} catch (JOSEException e) {
			log.debug("Failed to verify signature: {}", e.getMessage());
			return Optional.empty();
		}
	}
}
