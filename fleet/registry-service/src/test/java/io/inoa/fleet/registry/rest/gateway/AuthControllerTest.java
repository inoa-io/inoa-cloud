package io.inoa.fleet.registry.rest.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import io.inoa.fleet.registry.AbstractTest;
import io.inoa.fleet.registry.auth.AuthTokenKeys;
import io.inoa.fleet.registry.auth.AuthTokenService;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.rest.HttpResponseAssertions;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;

/**
 * Test for {@link AuthController}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("gateway: auth")
public class AuthControllerTest extends AbstractTest {

	private final Instant now = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant();

	@Inject
	AuthApiTestClient client;
	@Inject
	AuthTokenService authTokenService;
	@Inject
	AuthTokenKeys authTokenKeys;

	@DisplayName("keys: get jwkset")
	@Test
	void getJwkSet() {
		var json = assert200(() -> client.getKeys());
		assertEquals(authTokenKeys.getJwkSet().toJSONObject(), json, "jwk");
	}

	@DisplayName("error: grant_type invalid")
	@Test
	void errorGrantType() {
		var errorDescription = "grant_type is not urn:ietf:params:oauth:grant-type:jwt-bearer";
		var jwt = token(UUID.randomUUID(), "pleaseChangeThisSecretForANewOne".getBytes());
		assertError(() -> client.getToken("password", jwt), errorDescription);
	}

	@DisplayName("error: token not parseable")
	@Test
	void errorTokenNotParseable() {
		var errorDescription = "failed to parse token";
		var jwt = "jwt";
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	@DisplayName("error: claim iss missing")
	@Test
	void errorClaimIssMissing() {
		assertError(claims -> claims.issuer(null), "token does not contain claim iss");
	}

	@DisplayName("error: claim iss invalid")
	@Test
	void errorClaimIssInvalid() {
		var issuer = "merh";
		assertError(claims -> claims.issuer(issuer), "token does not contain valid claim iss: " + issuer);
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
		var gatewayId = UUID.randomUUID();
		var gatewayPreSharedKey = UUID.randomUUID().toString().getBytes();
		var errorDescription = "gateway " + gatewayId + " not found";
		var jwt = token(gatewayId, gatewayPreSharedKey);
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	@DisplayName("error: signature invalid")
	@Test
	void errorSignatureInvalid() {
		var gateway = data.gateway(data.tenant());
		var gatewayPreSharedKey = UUID.randomUUID().toString().getBytes();
		var errorDescription = "signature verification failed";
		var jwt = token(gateway.getGatewayId(), gatewayPreSharedKey);
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	@DisplayName("error: tenant disabled")
	@Test
	void errorTenantDisabled() {
		var tenant = data.tenant("inoa", false, false);
		var gateway = data.gateway(tenant);
		var credential = data.credentialPSK(gateway);
		var errorDescription = "tenant " + tenant.getTenantId() + " disabled";
		var jwt = token(gateway.getGatewayId(), credential.getValue());
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	@DisplayName("error: tenant deleted")
	@Test
	void errorTenantDeleted() {
		var tenant = data.tenant("inoa", true, true);
		var gateway = data.gateway(tenant);
		var credential = data.credentialPSK(gateway);
		var errorDescription = "tenant " + tenant.getTenantId() + " deleted";
		var jwt = token(gateway.getGatewayId(), credential.getValue());
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	@DisplayName("error: gateway disabled")
	@Test
	void errorGatewayDisabled() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant, false);
		var credential = data.credentialPSK(gateway);
		var errorDescription = "gateway " + gateway.getGatewayId() + " disabled";
		var jwt = token(gateway.getGatewayId(), credential.getValue());
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
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
		var jwt = token(gateway.getGatewayId(), credential.getValue());
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
				.issuer(gateway.getGatewayId().toString())
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
		var errorDescription = "signature verification failed";
		var jwt = token(gateway.getGatewayId(), gatewayPreSharedKey);
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	// internal

	private void assertSuccess(Gateway gateway, String jwt) {
		var response = assert200(() -> client.getToken(AuthController.GRANT_TYPE, jwt));
		var uuidFromResponse = authTokenService.validateToken(response.getAccessToken()).orElse(null);
		assertEquals(gateway.getGatewayId(), uuidFromResponse, "access_token");
		assertEquals(response.getTokenType(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, "token_type");
		assertEquals(properties.getAuth().getExpirationDuration().getSeconds(), response.getExpiresIn(), "expires_in");
		assertEquals(response.getTenantId(), gateway.getTenant().getTenantId(), "tenant_id");
	}

	private void assertError(Consumer<JWTClaimsSet.Builder> claimsManipulator, String errorDescription) {
		var jwt = token(UUID.randomUUID(), UUID.randomUUID().toString().getBytes(), claimsManipulator);
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	private <T> void assertError(Supplier<HttpResponse<T>> executeable, String expectedDescription) {
		var error = HttpResponseAssertions.assert400(executeable).getBody(TokenErrorVO.class).orElse(null);
		assertNotNull(error, "error body missing");
		assertEquals("invalid_grant", error.getError(), "error invalid");
		var errorDescription = error.getErrorDescription();
		assertNotNull(errorDescription, "error description invalid");
		assertTrue(errorDescription.startsWith(expectedDescription), () -> "error description invalid, expected ["
				+ expectedDescription + "] but got [" + errorDescription + "]");
	}

	private String token(UUID gateway, byte[] secret) {
		return token(gateway, secret, claims -> {});
	}

	@SneakyThrows
	private String token(UUID gateway, byte[] secret, Consumer<JWTClaimsSet.Builder> claimsManipulator) {

		var claims = new JWTClaimsSet.Builder()
				.audience(properties.getGateway().getToken().getAudience())
				.jwtID(UUID.randomUUID().toString())
				.issuer(gateway.toString())
				.issueTime(Date.from(now))
				.notBeforeTime(Date.from(now))
				.expirationTime(Date.from(now.plusSeconds(1)));
		claimsManipulator.accept(claims);

		var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
		jwt.sign(new MACSigner(secret));
		return jwt.serialize();
	}

	@Primary
	@Singleton
	Clock clock() {
		return Clock.fixed(now, ZoneOffset.UTC);
	}
}
