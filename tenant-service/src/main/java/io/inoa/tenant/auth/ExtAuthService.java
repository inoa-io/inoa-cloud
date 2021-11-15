package io.inoa.tenant.auth;

import com.google.rpc.Status;

import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckRequest;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.envoyproxy.envoy.service.auth.v3.OkHttpResponse;
import io.grpc.stub.StreamObserver;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class ExtAuthService extends AuthorizationGrpc.AuthorizationImplBase {

	private final JwtService jwtService;
	private final InoaAuthProperties inoaAuthProperties;

	@Override
	public void check(CheckRequest request, StreamObserver<CheckResponse> responseObserver) {
		CheckResponse response = CheckResponse.newBuilder()
				.setStatus(Status.newBuilder().setCode(io.grpc.Status.UNAUTHENTICATED.getCode().value()).build())
				.build();

		var authorization = request.getAttributes().getRequest().getHttp().getHeadersMap().get("authorization");
		if (authorization != null) {
			var tenant = request.getAttributes().getRequest().getHttp().getHeadersMap()
					.get(inoaAuthProperties.getTenantHeader());
			String[] split = authorization.split(" ");
			if (split.length == 2) {
				try {
					String token = split[1];
					HeaderValue headerValue = HeaderValue.newBuilder().setKey("Authorization").setValue(String
							.format("Bearer %s", jwtService.createJwtToken(jwtService.parseJwtToken(token), tenant)))
							.build();

					OkHttpResponse httpResponse = OkHttpResponse.newBuilder()
							.addHeaders(HeaderValueOption.newBuilder().setHeader(headerValue).build()).build();
					response = CheckResponse.newBuilder()
							.setStatus(Status.newBuilder().setCode(io.grpc.Status.OK.getCode().value()).build())
							.setOkResponse(httpResponse).build();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		// Use responseObserver to send a single response back
		responseObserver.onNext(response);

		// When you are done, you must call onCompleted.
		responseObserver.onCompleted();
	}
}
