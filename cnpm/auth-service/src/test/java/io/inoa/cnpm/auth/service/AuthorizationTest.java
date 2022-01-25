package io.inoa.cnpm.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.nimbusds.jwt.SignedJWT;

import io.envoyproxy.envoy.service.auth.v3.AttributeContext.HttpRequest;
import io.inoa.cnpm.auth.AbstractTest;
import io.inoa.cnpm.tenant.management.TenantVO;
import io.inoa.cnpm.tenant.management.UserRoleVO;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

/**
 * Test for {@link AuthorizationService}.
 */
@DisplayName("service: authorization")
public class AuthorizationTest extends AbstractTest {

	@Inject
	AuthorizationService service;

	@DisplayName("success with user in role viewer")
	@ParameterizedTest
	@EnumSource(UserRoleVO.class)
	void successWithUser(UserRoleVO role) {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, role);
		var jwtRequest = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, user.getEmail()));
		var jwtResponse = assertOk(tenant, jwtRequest);
		assertNotEquals(jwtRequest.serialize(), jwtResponse.serialize(), "jwt not changed");
		assertToken(jwtResponse, tenant, user.getUserId(), role);
	}

	@DisplayName("success with application")
	@Test
	void successWithApplcation() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var audience = issuer.getServices().iterator().next();
		var jwtRequest = security.jwt(issuer.getName(), claims -> claims.audience(audience));
		var jwtResponse = assertOk(tenant, jwtRequest);
		assertNotEquals(jwtRequest.serialize(), jwtResponse.serialize(), "jwt not changed");
		assertToken(jwtResponse, tenant, null, UserRoleVO.ADMIN);
	}

	@DisplayName("success with local signature")
	@Test
	void successWithLocalSignature() {

		// exchange user token

		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwtRequest = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, user.getEmail()));
		var jwtResponse = assertOk(tenant, jwtRequest);
		assertNotEquals(jwtRequest.serialize(), jwtResponse.serialize(), "jwt not changed");

		// send exchanged token again

		var jwtResponse2 = assertOk(tenant, jwtResponse);
		assertEquals(jwtResponse.serialize(), jwtResponse2.serialize(), "local token exchanged");
	}

	@DisplayName("fail: token claim email not found")
	@Test
	void failTokenClaimEmailNotFound() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var jwt = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, null));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: token claim issuer not available")
	@Test
	void failTokenClaimIssuerNotAvailable() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims
				.claim(Token.CLAIM_EMAIL, user.getEmail())
				.issuer("http://localhost:0"));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: token claim issuer not found")
	@Test
	void failTokenClaimIssuerNotFound() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims
				.claim(Token.CLAIM_EMAIL, user.getEmail())
				.issuer(null));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: token claim issuer is local but signature is invalid")
	@Test
	void failTokenClaimIssuerFakedLocal() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims
				.claim(Token.CLAIM_EMAIL, user.getEmail())
				.issuer(properties.getTokenExchange().getIssuer()));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: token expired")
	@Test
	void failTokenExpired() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims
				.claim(Token.CLAIM_EMAIL, user.getEmail())
				.expirationTime(Date.from(Instant.now().minusSeconds(1))));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: token not parsable")
	@Test
	void failTokenNotParseable() {
		var tenant = data.addTenant();
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, "test");
	}

	@DisplayName("fail: token with invalid signature")
	@Test
	void failTokenSignature() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt("1", claims -> claims
				.claim(Token.CLAIM_EMAIL, user.getEmail())
				.issuer(issuer.getUrl().toString()));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: token missing")
	@Test
	void failTokenMissing() {
		var tenant = data.addTenant();
		assertUnauthenticated(tenant.getTenantId(), null, null);
	}

	@DisplayName("fail: bearer missing")
	@Test
	void failBearerMissing() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, user.getEmail()));
		assertUnauthenticated(tenant.getTenantId(), null, jwt.serialize());
	}

	@DisplayName("fail: bearer invalid")
	@Test
	void failBearerInvalid() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, user.getEmail()));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BASIC, jwt.serialize());
	}

	@DisplayName("fail: tenant header missing")
	@Test
	void failTenantHeaderMissing() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, user.getEmail()));
		assertUnauthenticated(null, HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: tenant not found")
	@Test
	void failTenantNotFound() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, user.getEmail()));
		assertUnauthenticated("nope", HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: tenant disabled")
	@Test
	void failTenantDisabled() {
		var tenant = data.addTenant(false, "0");
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, user.getEmail()));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	@DisplayName("fail: user not found")
	@Test
	void failUserFotFound() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var jwt = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, "nope"));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt.serialize());
	}

	private SignedJWT assertOk(TenantVO tenant, SignedJWT request) {
		var response = request(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, request.serialize());
		assertTrue(response.isPresent());
		return response.get();
	}

	private void assertUnauthenticated(String tenantId, String prefix, String token) {
		assertTrue(request(tenantId, prefix, token).isEmpty());
	}

	@SneakyThrows
	private void assertToken(SignedJWT jwt, TenantVO tenant, UUID userId, UserRoleVO role) {
		var claims = jwt.getJWTClaimsSet();
		assertEquals(tenant.getTenantId(), claims.getClaim(Token.CLAIM_TENANT_ID), "tenant");
		assertEquals(userId == null ? null : userId.toString(), claims.getClaim(Token.CLAIM_USER_ID), "user");
		assertEquals(List.of(role.getValue()), claims.getClaim(Token.CLAIM_ROLES), "role");
	}

	private Optional<SignedJWT> request(String tenantId, String prefix, String token) {
		var request = HttpRequest.newBuilder();
		if (token != null) {
			request.putHeaders(HttpHeaders.AUTHORIZATION.toLowerCase(), (prefix == null ? "" : prefix + " ") + token);
		}
		if (tenantId != null) {
			request.putHeaders(properties.getTokenExchange().getHttpHeader(), tenantId);
		}
		return service.exchangeToken(request.build());
	}
}
