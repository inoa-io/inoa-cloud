package io.inoa.cnpm.tenant.test;

import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;

/**
 * Factory for grcp stub.
 *
 * @author Rico Pahlisch
 * @author Stephan Schnabel
 */
@Factory
public class TestFactory {

	@Bean
	AuthorizationGrpc.AuthorizationStub reactiveStub(@GrpcChannel("istioAuth") ManagedChannel channel) {
		return AuthorizationGrpc.newStub(channel);
	}
}
