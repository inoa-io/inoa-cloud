package io.kokuwa.fleet.registry.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jwt.JWTClaimsSet;

import io.kokuwa.fleet.registry.HttpResponseAssertions;
import io.kokuwa.fleet.registry.auth.AuthTokenService;
import io.kokuwa.fleet.registry.rest.gateway.AuthApiTestClient;
import io.kokuwa.fleet.registry.rest.gateway.TokenErrorVO;
import io.kokuwa.fleet.registry.rest.gateway.TokenRepsonseVO;
import io.kokuwa.fleet.registry.test.AbstractUnitTest;
import io.kokuwa.fleet.registry.test.Data;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpResponse;

/**
 * Test for {@link AuthController}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("gateway: auth")
public class AuthControllerTest extends AbstractUnitTest {

	@Inject
	AuthApiTestClient client;
	@Inject
	AuthTokenService authTokenService;

	@DisplayName("error: grant_type invalid")
	@Test
	void errorGrantType() {
		var errorDescription = "grant_type is not urn:ietf:params:oauth:grant-type:jwt-bearer";
		var jwt = auth.gatewayToken(Data.GATEWAY_1_UUID, Data.GATEWAY_1_SECRET);
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
		var gatewayUuid = UUID.randomUUID();
		var gatewaySecret = UUID.randomUUID().toString();
		var errorDescription = "gateway " + gatewayUuid + " not found";
		var jwt = auth.gatewayToken(gatewayUuid, gatewaySecret);
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	@DisplayName("error: signature invalid")
	@Test
	void errorSignatureInvalid() {
		var gatewayUuid = Data.GATEWAY_1_UUID;
		var gatewaySecret = UUID.randomUUID().toString();
		var errorDescription = "signature verification failed";
		var jwt = auth.gatewayToken(gatewayUuid, gatewaySecret);
		assertError(() -> client.getToken(AuthController.GRANT_TYPE, jwt), errorDescription);
	}

	@DisplayName("success: full token")
	@Test
	void successFullToken() {

		assertTrue(properties.getGateway().getToken().isForceJwtId(), "jti optional");
		assertTrue(properties.getGateway().getToken().isForceNotBefore(), "nbf optional");
		assertTrue(properties.getGateway().getToken().isForceIssuedAt(), "iat optional");
		assertTrue(properties.getGateway().getToken().getIssuedAtThreshold().isPresent(), "iat threshold missing");

		var gatewayUuid = Data.GATEWAY_1_UUID;
		var gatewaySecret = Data.GATEWAY_1_SECRET;
		var jwt = auth.gatewayToken(gatewayUuid, gatewaySecret);
		assertSuccess(gatewayUuid, jwt);
	}

	@DisplayName("success: minimal token")
	@Test
	void successMinimalToken() {

		properties.getGateway().getToken().setForceJwtId(false);
		properties.getGateway().getToken().setForceNotBefore(false);
		properties.getGateway().getToken().setForceIssuedAt(true);
		properties.getGateway().getToken().setIssuedAtThreshold(Optional.empty());

		var gatewayUuid = Data.GATEWAY_1_UUID;
		var gatewaySecret = Data.GATEWAY_1_SECRET;
		var jwt = auth.gatewayToken(gatewayUuid, gatewaySecret,
				claims -> claims.notBeforeTime(null).jwtID(null));
		assertSuccess(gatewayUuid, jwt);
	}

	// internal

	private void assertSuccess(UUID gateway, String jwt) {
		TokenRepsonseVO response = assert200(() -> client.getToken(AuthController.GRANT_TYPE, jwt));
		assertEquals(gateway, authTokenService.validateToken(response.getAccessToken()).orElse(null), "access_token");
		assertEquals(response.getTokenType(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, "token_type");
		assertEquals(properties.getAuth().getExpirationDuration().getSeconds(), response.getExpiresIn(), "expires_in");
		assertEquals(response.getConfigUri(), properties.getConfigUri(), "config_uri");
		assertEquals(response.getConfigType(), properties.getConfigType(), "config_type");
		assertEquals(response.getTenantUuid(), Data.GATEWAY_1_TENANT_UUID, "tenant_uuid");
	}

	private void assertError(Consumer<JWTClaimsSet.Builder> claimsManipulator, String errorDescription) {
		var jwt = auth.gatewayToken(Data.GATEWAY_1_UUID, Data.GATEWAY_1_SECRET, claimsManipulator);
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
}
