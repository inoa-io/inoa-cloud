package io.inoa.fleet.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import com.nimbusds.jose.JOSEException;

import io.envoyproxy.envoy.service.auth.v3.AttributeContext;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.grpc.stub.StreamObserver;
import io.micronaut.test.annotation.MockBean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrpcTest extends AbstractTest {

	private static class TestStreamObserver implements StreamObserver<CheckResponse> {

		@Getter
		private Optional<CheckResponse> checkResponse = Optional.empty();
		@Getter
		private final CountDownLatch countDownLatch = new CountDownLatch(1);

		@Override
		public void onNext(CheckResponse checkResponse) {
			this.checkResponse = Optional.of(checkResponse);
		}

		@Override
		public void onError(Throwable throwable) {

		}

		@Override
		public void onCompleted() {
			countDownLatch.countDown();
		}
	}

	@Inject
	JwtService jwtService;
	@Inject
	AuthorizationGrpc.AuthorizationStub grpcClient;

	@MockBean(JwtService.class)
	public JwtService jwtService() {
		return mock(JwtService.class);
	}

	@Test
	void successTokenExchange() throws InterruptedException, ParseException, JOSEException {

		TestStreamObserver responseStreamObserver = new TestStreamObserver();
		Jwt mock = mock(Jwt.class);
		Map<String, Object> claims = new HashMap<>();
		claims.put("sub", "test");
		when(mock.getClaims()).thenReturn(claims);

		when(jwtService.parseJwtToken(any())).thenReturn(mock);
		when(jwtService.createJwtToken(any(), any())).thenReturn("my_token");
		// var authorization =
		// request.getAttributes().getRequest().getHttp().getHeadersMap().get("authorization");

		AttributeContext.HttpRequest httpRequest = AttributeContext.HttpRequest.newBuilder()
				.putHeaders("authorization", "bearer test").build();
		AttributeContext.Request request = AttributeContext.Request.newBuilder().setHttp(httpRequest).build();
		AttributeContext attributeContext = AttributeContext.newBuilder().setRequest(request).build();

		CheckRequest checkRequest = CheckRequest.newBuilder().setAttributes(attributeContext).build();
		grpcClient.check(checkRequest, responseStreamObserver);
		responseStreamObserver.getCountDownLatch().await(5, TimeUnit.SECONDS);
		assertTrue(responseStreamObserver.getCheckResponse().isPresent());
		CheckResponse cr = responseStreamObserver.getCheckResponse().get();
		assertNotNull(cr);
		assertEquals(0, cr.getStatus().getCode());
		HeaderValueOption header = cr.getOkResponse().getHeaders(0);
		assertEquals("Authorization", header.getHeader().getKey());
		assertEquals("Bearer my_token", header.getHeader().getValue());
	}
}
