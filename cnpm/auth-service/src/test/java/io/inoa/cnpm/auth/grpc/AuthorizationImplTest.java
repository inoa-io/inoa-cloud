package io.inoa.cnpm.auth.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.envoyproxy.envoy.service.auth.v3.AttributeContext;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext.HttpRequest;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext.Request;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import io.inoa.cnpm.auth.AbstractTest;
import io.inoa.cnpm.auth.service.AuthorizationService;
import io.inoa.cnpm.auth.service.Token;
import io.inoa.cnpm.tenant.management.UserRoleVO;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
import jakarta.inject.Inject;
import lombok.Getter;

/**
 * Test for {@link AuthorizationImpl}.
 */
@DisplayName("grpc: authorization")
public class AuthorizationImplTest extends AbstractTest {

	@Inject
	AuthorizationService service;

	@DisplayName("authenticated")
	@Test
	void authenticated() {
		var tenant = data.addTenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.addUser(tenant, UserRoleVO.ADMIN);
		var jwt = security.jwt(issuer.getName(), claims -> claims.claim(Token.CLAIM_EMAIL, user.getEmail()));
		var observer = request(tenant.getTenantId(), jwt.serialize());
		assertTrue(observer.isCompleted(), "completed");
		assertEquals(Code.OK.value(), observer.getResponse().getStatus().getCode(), "status");
	}

	@DisplayName("unauthenticated")
	@Test
	void unauthenticated() {
		var tenant = data.addTenant();
		var observer = request(tenant.getTenantId(), "nope");
		assertTrue(observer.isCompleted(), "completed");
		assertEquals(Code.UNAUTHENTICATED.value(), observer.getResponse().getStatus().getCode(), "status");
	}

	private TestStreamObserver request(String tenantId, String token) {

		var http = HttpRequest.newBuilder()
				.putHeaders(HttpHeaders.AUTHORIZATION.toLowerCase(),
						HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + token)
				.putHeaders(properties.getTokenExchange().getHttpHeader(), tenantId)
				.build();
		var request = Request.newBuilder().setHttp(http).build();
		var attributeContext = AttributeContext.newBuilder().setRequest(request).build();
		var checkRequest = CheckRequest.newBuilder().setAttributes(attributeContext).build();

		var observer = new TestStreamObserver();
		new AuthorizationImpl(service).check(checkRequest, observer);
		return observer;
	}

	@Getter
	public static class TestStreamObserver implements StreamObserver<CheckResponse> {

		private CheckResponse response;
		private boolean completed = false;

		@Override
		public void onNext(CheckResponse value) {
			response = value;
		}

		@Override
		public void onCompleted() {
			completed = true;
		}

		@Override
		public void onError(Throwable t) {}
	}
}
