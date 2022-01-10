package io.inoa.cnpm.auth.grpc;

import org.slf4j.MDC;

import com.google.rpc.Status;

import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.envoyproxy.envoy.service.auth.v3.OkHttpResponse;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import io.inoa.cnpm.auth.service.AuthorizationService;
import io.inoa.cnpm.auth.service.Token;
import io.micronaut.http.HttpHeaderValues;
import io.micronaut.http.HttpHeaders;
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Authorization GRPC for token exchange.
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class AuthorizationImpl extends AuthorizationGrpc.AuthorizationImplBase {

	private static final CheckResponse UNAUTHENTICATED = CheckResponse.newBuilder()
			.setStatus(Status.newBuilder().setCode(Code.UNAUTHENTICATED.value()).build())
			.build();

	private final AuthorizationService service;

	@Override
	public void check(CheckRequest request, StreamObserver<CheckResponse> observer) {
		log.trace("Got request: {}", request);
		try {
			observer.onNext(service.exchangeToken(request.getAttributes().getRequest().getHttp())
					.map(jwt -> HeaderValue.newBuilder()
							.setKey(HttpHeaders.AUTHORIZATION)
							.setValue(HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + jwt.serialize())
							.build())
					.map(header -> CheckResponse.newBuilder()
							.setStatus(Status.newBuilder().setCode(Code.OK.value()).build())
							.setOkResponse(OkHttpResponse.newBuilder()
									.addHeaders(HeaderValueOption.newBuilder().setHeader(header).build())
									.build())
							.build())
					.orElse(UNAUTHENTICATED));
		} catch (RuntimeException e) {
			log.error("Error while token exchange.", e);
			observer.onNext(UNAUTHENTICATED);
		} finally {
			observer.onCompleted();
			MDC.remove(JwtClaims.ISSUER);
			MDC.remove(JwtClaims.AUDIENCE);
			MDC.remove(Token.CLAIM_AZP);
			MDC.remove(Token.CLAIM_TENANT_ID);
			MDC.remove(Token.CLAIM_EMAIL);
		}
	}
}
