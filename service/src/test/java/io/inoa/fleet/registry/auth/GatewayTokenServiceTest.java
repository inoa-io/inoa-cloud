package io.inoa.fleet.registry.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.AbstractUnitTest;
import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Test for {@link GatewayTokenService}.
 *
 * @author Stephan Schnabel
 */
public class GatewayTokenServiceTest extends AbstractUnitTest {

	private final Instant now = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();
	@Inject
	GatewayTokenService service;
	@Inject
	GatewayTokenHelper gatewayToken;

	@DisplayName("error: token not parseable")
	@Test
	void errorTokenNotParseable() {
		var error = "failed to parse token";
		var jwt = "jwt";
		assertError(() -> service.getGatewayFromToken(jwt), error);
	}

	@DisplayName("error: claim iss missing")
	@Test
	void errorClaimIssMissing() {
		assertError(claims -> claims.issuer(null), "token does not contain claim iss");
	}

	@DisplayName("error: claim exp missing")
	@Test
	void errorClaimExpMissing() {
		assertError(claims -> claims.expirationTime(null), "token does not contain claim exp");
	}

	@DisplayName("error: claim exp outdated")
	@Test
	void errorClaimExpOutdated() {
		var exp = now.minusSeconds(1);
		assertError(claims -> claims.expirationTime(Date.from(exp)), "token is expired: " + exp);
	}

	@DisplayName("error: claim nbf missing")
	@Test
	void errorClaimNbfMissing() {
		assertTrue(properties.getGateway().getToken().isForceNotBefore(), "nbf optional");
		assertError(claims -> claims.notBeforeTime(null), "token does not contain claim nbf");
	}

	@DisplayName("error: claim nbf in future")
	@Test
	void errorClaimNbfInFuture() {
		var nbf = now.plusSeconds(1);
		assertError(claims -> claims.notBeforeTime(Date.from(nbf)), "token is not valid before: " + nbf);
	}

	@DisplayName("error: claim iat missing")
	@Test
	void errorClaimIatMissing() {
		assertTrue(properties.getGateway().getToken().isForceIssuedAt(), "iat optional");
		assertError(claims -> claims.issueTime(null), "token does not contain claim iat");
	}

	@DisplayName("error: claim iat exceeds threshold")
	@Test
	void errorClaimIatThreshold() {
		assertTrue(properties.getGateway().getToken().isForceIssuedAt(), "iat optional");
		assertTrue(properties.getGateway().getToken().getIssuedAtThreshold().isPresent(), "iat threshold missing");
		var threshold = properties.getGateway().getToken().getIssuedAtThreshold().get();
		var iat = now.minus(threshold).minusSeconds(1);
		assertError(claims -> claims.issueTime(Date.from(iat)),
				"token is too old (older than " + threshold + "): " + iat);
	}

	@DisplayName("error: claim jti missing")
	@Test
	void errorClaimJtiMissing() {
		assertTrue(properties.getGateway().getToken().isForceJwtId(), "jti optional");
		assertError(claims -> claims.jwtID(null), "token does not contain claim jti");
	}

	@DisplayName("error: claim aud missing")
	@Test
	void errorClaimAudMissing() {
		assertError(claims -> claims.audience(List.of()), "token does not contain claim aud");
	}

	@DisplayName("error: claim aud mismatch")
	@Test
	void errorClaimAudMismatch() {
		assertError(claims -> claims.audience("a"),
				"token audience expected: " + properties.getGateway().getToken().getAudience());
	}

	@DisplayName("error: gateway not found")
	@Test
	void errorGatewayNotFound() {
		var gatewayId = data.gatewayId();
		var gatewayPreSharedKey = UUID.randomUUID().toString().getBytes();
		var error = "gateway " + gatewayId + " not found";
		var jwt = gatewayToken.token(gatewayId, gatewayPreSharedKey);
		assertError(() -> service.getGatewayFromToken(jwt), error);
	}

	@DisplayName("error: signature invalid")
	@Test
	void errorSignatureInvalid() {
		var gateway = data.gateway(data.tenant());
		var gatewayPreSharedKey = UUID.randomUUID().toString().getBytes();
		var error = "signature verification failed";
		var jwt = gatewayToken.token(gateway.getGatewayId(), gatewayPreSharedKey);
		assertError(() -> service.getGatewayFromToken(jwt), error);
	}

	@DisplayName("error: tenant disabled")
	@Test
	void errorTenantDisabled() {
		var tenant = data.tenant("inoa", false, false);
		var gateway = data.gateway(tenant);
		var credential = data.credentialPSK(gateway);
		var error = "tenant " + tenant.getTenantId() + " disabled";
		var jwt = gatewayToken.token(gateway.getGatewayId(), credential.getValue());
		assertError(() -> service.getGatewayFromToken(jwt), error);
	}

	@DisplayName("error: tenant deleted")
	@Test
	void errorTenantDeleted() {
		var tenant = data.tenant("inoa", true, true);
		var gateway = data.gateway(tenant);
		var credential = data.credentialPSK(gateway);
		var error = "tenant " + tenant.getTenantId() + " deleted";
		var jwt = gatewayToken.token(gateway.getGatewayId(), credential.getValue());
		assertError(() -> service.getGatewayFromToken(jwt), error);
	}

	@DisplayName("error: gateway disabled")
	@Test
	void errorGatewayDisabled() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant, false);
		var credential = data.credentialPSK(gateway);
		var error = "gateway " + gateway.getGatewayId() + " disabled";
		var jwt = gatewayToken.token(gateway.getGatewayId(), credential.getValue());
		assertError(() -> service.getGatewayFromToken(jwt), error);
	}

	@DisplayName("success: full token with PSK")
	@Test
	void successFullToken() {

		assertTrue(properties.getGateway().getToken().isForceJwtId(), "jti optional");
		assertTrue(properties.getGateway().getToken().isForceNotBefore(), "nbf optional");
		assertTrue(properties.getGateway().getToken().isForceIssuedAt(), "iat optional");
		assertTrue(properties.getGateway().getToken().getIssuedAtThreshold().isPresent(), "iat threshold missing");

		var gateway = data.gateway(data.tenant());
		var credential = data.credentialPSK(gateway);
		var jwt = gatewayToken.token(gateway.getGatewayId(), credential.getValue());
		assertSuccess(gateway, jwt);
	}

	@DisplayName("success: minimal token with RSA")
	@Test
	void successMinimalToken() throws JOSEException {

		properties.getGateway().getToken().setForceJwtId(false);
		properties.getGateway().getToken().setForceNotBefore(false);
		properties.getGateway().getToken().setForceIssuedAt(false);
		properties.getGateway().getToken().setIssuedAtThreshold(Optional.empty());

		var keyPair = data.generateKeyPair();
		var gateway = data.gateway(data.tenant());
		data.credentialRSA(gateway, keyPair);

		var claims = new JWTClaimsSet.Builder()
				.audience(properties.getGateway().getToken().getAudience())
				.issuer(gateway.getGatewayId())
				.expirationTime(Date.from(now.plusSeconds(1)));
		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), claims.build());
		jwt.sign(new RSASSASigner(keyPair.getPrivate()));
		assertSuccess(gateway, jwt.serialize());
	}

	@DisplayName("error: with psk")
	@Test
	void errorPsk() {
		var gateway = data.gateway(data.tenant());
		var gatewayPreSharedKey = UUID.randomUUID().toString().getBytes();
		data.credentialPSK(gateway);
		var error = "signature verification failed";
		var jwt = gatewayToken.token(gateway.getGatewayId(), gatewayPreSharedKey);
		assertError(() -> service.getGatewayFromToken(jwt), error);
	}

	// internal

	private void assertSuccess(Gateway gateway, String jwt) {
		assertEquals(gateway, service.getGatewayFromToken(jwt), "gateway");
	}

	private void assertError(Consumer<JWTClaimsSet.Builder> claimsManipulator, String error) {
		var jwt = gatewayToken.token(data.gatewayId(), UUID.randomUUID().toString().getBytes(), claimsManipulator);
		assertError(() -> service.getGatewayFromToken(jwt), error);
	}

	private void assertError(Supplier<Gateway> executeable, String expectedError) {
		var exception = assertThrows(HttpStatusException.class, () -> executeable.get(), "expected: " + expectedError);
		var actualError = exception.getMessage();
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus(), "status");
		assertNotNull(actualError, "error message");
		assertTrue(actualError.startsWith(expectedError),
				() -> "error invalid, expected [" + expectedError + "] but got [" + actualError + "]");
	}

	@Primary
	@Singleton
	Clock clock() {
		return Clock.fixed(now, ZoneOffset.UTC);
	}
}
