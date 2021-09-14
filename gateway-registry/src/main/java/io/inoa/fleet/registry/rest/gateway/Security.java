package io.inoa.fleet.registry.rest.gateway;

import javax.inject.Singleton;

import io.inoa.fleet.registry.auth.GatewayAuthentication;
import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.http.context.ServerRequestContext;
import lombok.RequiredArgsConstructor;

/**
 * Offers jwt related security for gateway interface.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
class Security {

	Gateway getGateway() {
		return ServerRequestContext.currentRequest()
				.flatMap(request -> request.getUserPrincipal(GatewayAuthentication.class))
				.map(GatewayAuthentication::getGateway).orElseThrow();
	}
}
