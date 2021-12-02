package io.inoa.cnpm.tenant.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext.HttpRequest;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext.Request;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.grpc.Status.Code;
import io.inoa.cnpm.tenant.AbstractTest;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.test.TestStreamObserver;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import jakarta.inject.Inject;

/**
 * Test for {@link AuthorizationService}.
 *
 * @author Rico Pahlisch
 * @author Stephan Schnabel
 */
public class AuthorizationTest extends AbstractTest {

	@Inject
	AuthorizationGrpc.AuthorizationStub grpc;
	@Inject
	SignatureGeneratorConfiguration signature;

	@DisplayName("success with user")
	@Test
	void successWithUser() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var tokenRequest = security.jwt(user);
		var response = assertOk(tenant, tokenRequest);
		var tokenResponse = getAuthorizationToken(response);
		assertNotEquals(tokenRequest, tokenResponse, "jwt not changed");
	}

	@DisplayName("success with application")
	@Test
	void successWithApplcation() throws JOSEException {
		var tenant = data.tenant();
		var tokenRequest = signature.sign(new JWTClaimsSet.Builder().build()).serialize();
		var response = assertOk(tenant, tokenRequest);
		var tokenResponse = getAuthorizationToken(response);
		assertNotEquals(tokenRequest, tokenResponse, "jwt not changed");
	}

	@DisplayName("success with local signature")
	@Test
	void successWithLocalSignature() {

		// xchange user token

		var tenant = data.tenant();
		var user = data.user(tenant);
		var tokenRequest = security.jwt(user);
		var response = assertOk(tenant, tokenRequest);
		var tokenResponse = getAuthorizationToken(response);
		assertNotEquals(tokenRequest, tokenResponse, "jwt not changed");

		// send exchanged token again

		var response2 = assertOk(tenant, tokenResponse);
		var tokenResponse2 = getAuthorizationToken(response2);
		assertEquals(tokenResponse, tokenResponse2, "local token exchanged");
	}

	@DisplayName("fail: token claim email not found")
	@Test
	void failTokenClaimEmailNotFound() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt(user.getEmail(), claims -> claims.claim("email", null));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token claim issuer not available")
	@Test
	void failTokenClaimIssuerNotAvailable() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt(user.getEmail(), claims -> claims.issuer(null));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token claim issuer not url")
	@Test
	void failTokenClaimIssuerNotUrl() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt(user.getEmail(), claims -> claims.issuer("nope"));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token claim issuer not found")
	@Test
	void failTokenClaimIssuerNotFound() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt("nope", user.getEmail(), claims -> {});
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token claim issuer is local but signature is invalid")
	@Test
	void failTokenClaimIssuerFakedLocal() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var tokenExchangeIssuer = properties.getTokenExchange().getIssuer();
		var jwt = security.jwt("nope", user.getEmail(), claims -> claims.issuer(tokenExchangeIssuer));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token expired")
	@Test
	void failTokenExpired() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt(user.getEmail(),
				claims -> claims.expirationTime(Date.from(Instant.now().minusSeconds(5))));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token invalid")
	@Test
	void failTokenInvalid() {
		var tenant = data.tenant();
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, "test");
	}

	@DisplayName("fail: bearer missing")
	@Test
	void failBearerMissing() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), null, jwt);
	}

	@DisplayName("fail: bearer invalid")
	@Test
	void failBearerInvalid() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BASIC, jwt);
	}

	@DisplayName("fail: tenant header missing")
	@Test
	void failTenantHeaderMissing() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt(user);
		assertUnauthenticated(null, HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: tenant not found")
	@Test
	void failTenantNotFound() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = security.jwt(user);
		assertUnauthenticated(data.tenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: tenant disabled")
	@Test
	void failTenantDisabled() {
		var tenant = data.tenantDisabled();
		var user = data.user(tenant);
		var jwt = security.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: tenant deleted")
	@Test
	void failTenantDeleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		var jwt = security.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: user not found")
	@Test
	void failUserFotFound() {
		var tenant = data.tenant();
		var jwt = security.jwt("not-existing-email");
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: user not assigned to tenant")
	@Test
	void failUserNotAssigned() {
		var tenant = data.tenant();
		var user = data.user();
		var jwt = security.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	private CheckResponse assertOk(Tenant tenant, String token) {
		var response = request(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, token);
		assertEquals(Code.OK.value(), response.getStatus().getCode(), "status");
		return response;
	}

	private void assertUnauthenticated(String tenantId, String prefix, String token) {
		assertEquals(Code.UNAUTHENTICATED.value(), request(tenantId, prefix, token).getStatus().getCode(), "status");
	}

	private CheckResponse request(String tenantId, String prefix, String token) {

		var httpRequest = HttpRequest.newBuilder();
		if (token != null) {
			httpRequest
					.putHeaders(HttpHeaders.AUTHORIZATION.toLowerCase(), (prefix == null ? "" : prefix + " ") + token)
					.build();
		}
		if (tenantId != null) {
			httpRequest.putHeaders("x-tenant-id", tenantId).build();
		}
		var request = Request.newBuilder().setHttp(httpRequest).build();
		var attributeContext = AttributeContext.newBuilder().setRequest(request).build();
		var checkRequest = CheckRequest.newBuilder().setAttributes(attributeContext).build();

		var observer = new TestStreamObserver();
		grpc.check(checkRequest, observer);
		Awaitility.await("wait for auth request finished").until(observer::isCompleted);

		return observer.getResponse().get();
	}

	private String getHeader(CheckResponse response, String headerName) {
		return response.getOkResponse().getHeadersList().stream()
				.map(HeaderValueOption::getHeader)
				.filter(header -> headerName.toLowerCase().equals(header.getKey().toLowerCase()))
				.map(HeaderValue::getValue).findAny().orElse(null);
	}

	private String getAuthorizationToken(CheckResponse response) {
		var authorization = getHeader(response, HttpHeaders.AUTHORIZATION);
		assertNotNull(authorization, "header value");
		assertTrue(authorization.startsWith(HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " "), "bearer");
		return authorization.substring(7);
	}
}
