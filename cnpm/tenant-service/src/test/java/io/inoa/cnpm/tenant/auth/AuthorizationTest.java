package io.inoa.cnpm.tenant.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Date;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
import io.inoa.cnpm.tenant.test.TestJwkController;
import io.inoa.cnpm.tenant.test.TestStreamObserver;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
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
	TestJwkController test;

	@DisplayName("success")
	@Test
	void success() {

		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt(user);

		var response = request(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
		assertEquals(Code.OK.value(), response.getStatus().getCode(), "status");
		var headerValue = response.getOkResponse().getHeadersList().stream()
				.map(HeaderValueOption::getHeader)
				.filter(header -> HttpHeaders.AUTHORIZATION.equals(header.getKey()))
				.map(HeaderValue::getValue)
				.findAny().orElse(null);
		assertNotNull(headerValue, "header value");
		assertTrue(headerValue.startsWith(HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " "), "bearer");
	}

	@DisplayName("fail: token claim email not found")
	@Test
	void failTokenClaimEmailNotFound() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt(user.getEmail(), claims -> claims.claim("email", null));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token claim issuer not available")
	@Test
	void failTokenClaimIssuerNotAvailable() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt(user.getEmail(), claims -> claims.issuer(null));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token claim issuer not url")
	@Test
	void failTokenClaimIssuerNotUrl() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt(user.getEmail(), claims -> claims.issuer("nope"));
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token claim issuer not found")
	@Test
	void failTokenClaimIssuerNotFound() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt("nope", user.getEmail(), claims -> {});
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: token expired")
	@Test
	void failTokenExpired() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt(user.getEmail(), claims -> claims.expirationTime(Date.from(Instant.now().minusSeconds(5))));
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
		var jwt = test.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), null, jwt);
	}

	@DisplayName("fail: bearer invalid")
	@Test
	void failBearerInvalid() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BASIC, jwt);
	}

	@DisplayName("fail: tenant header missing")
	@Test
	void failTenantHeaderMissing() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt(user);
		assertUnauthenticated(null, HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: tenant not found")
	@Test
	void failTenantNotFound() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var jwt = test.jwt(user);
		assertUnauthenticated(data.tenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: tenant disabled")
	@Test
	void failTenantDisabled() {
		var tenant = data.tenantDisabled();
		var user = data.user(tenant);
		var jwt = test.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: tenant deleted")
	@Test
	void failTenantDeleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		var jwt = test.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: user not found")
	@Test
	void failUserFotFound() {
		var tenant = data.tenant();
		var jwt = test.jwt("not-existing-email");
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	@DisplayName("fail: user not assigned to tenant")
	@Test
	void failUserNotAssigned() {
		var tenant = data.tenant();
		var user = data.user();
		var jwt = test.jwt(user);
		assertUnauthenticated(tenant.getTenantId(), HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER, jwt);
	}

	private void assertUnauthenticated(String tenant, String prefix, String token) {
		assertEquals(Code.UNAUTHENTICATED.value(), request(tenant, prefix, token).getStatus().getCode(), "status");
	}

	private CheckResponse request(String tenant, String prefix, String token) {

		var httpRequest = HttpRequest.newBuilder();
		if (token != null) {
			httpRequest.putHeaders(HttpHeaders.AUTHORIZATION, (prefix == null ? "" : prefix + " ") + token).build();
		}
		if (tenant != null) {
			httpRequest.putHeaders("x-tenant-id", tenant).build();
		}
		var request = Request.newBuilder().setHttp(httpRequest).build();
		var attributeContext = AttributeContext.newBuilder().setRequest(request).build();
		var checkRequest = CheckRequest.newBuilder().setAttributes(attributeContext).build();

		var observer = new TestStreamObserver();
		grpc.check(checkRequest, observer);
		Awaitility.await("wait for auth request finished").until(observer::isCompleted);

		return observer.getResponse().get();
	}
}
