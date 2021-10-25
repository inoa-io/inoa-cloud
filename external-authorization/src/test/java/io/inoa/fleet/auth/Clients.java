package io.inoa.fleet.auth;

import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;

@Factory
public class Clients {
	@Bean
	AuthorizationGrpc.AuthorizationStub reactiveStub(@GrpcChannel("istioAuth") ManagedChannel channel) {
		return AuthorizationGrpc.newStub(channel);
	}
}
