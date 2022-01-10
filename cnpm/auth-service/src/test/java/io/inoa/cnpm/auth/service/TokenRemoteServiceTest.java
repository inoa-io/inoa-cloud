package io.inoa.cnpm.auth.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jwt.JWTClaimsSet;

import io.inoa.cnpm.auth.AbstractTest;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

/**
 * Test for {@link TokenRemoteService}.
 */
@DisplayName("service: token remote")
public class TokenRemoteServiceTest extends AbstractTest {

	@Inject
	TokenRemoteService service;

	@DisplayName("isValid: success")
	@Test
	void isValidSuccess() {
		assertTrue(service.isValid(token("0", c -> {})));
	}

	@DisplayName("isValid: fail with signature mismatch")
	@Test
	void isValidFailSignatureMismatch() {
		assertFalse(service.isValid(token("0", c -> c.issuer(security.getIssuer("1").toString()))));
	}

	@DisplayName("isValid: fail with jwk url unavailable")
	@Test
	void isValidFailJwkUnavailable() {
		assertFalse(service.isValid(token("nope", c -> c.issuer(security.getIssuer("nope").toString()))));
	}

	@DisplayName("isValid: fail with token expired")
	@Test
	void isValidFailExpired() {
		assertFalse(service.isValid(token("0", c -> c.expirationTime(Date.from(Instant.now().minusSeconds(10))))));
	}

	@DisplayName("isValid: fail with token not before")
	@Test
	void isValidFailNotBefore() {
		assertFalse(service.isValid(token("0", c -> c.notBeforeTime(Date.from(Instant.now().plusSeconds(10))))));
	}

	@SneakyThrows
	private Token token(String issuer, Consumer<JWTClaimsSet.Builder> claimsManipulator) {
		var now = Instant.now();
		var claims = new JWTClaimsSet.Builder()
				.subject(UUID.randomUUID().toString())
				.issueTime(Date.from(now))
				.issuer(security.getIssuer(issuer).toString())
				.expirationTime(Date.from(now.plusSeconds(60)));
		claimsManipulator.accept(claims);
		return new Token(security.sign(issuer, claims.build()).serialize());
	}
}
