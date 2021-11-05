package io.inoa.fleet.registry.auth;

import javax.inject.Singleton;

import org.reactivestreams.Publisher;

import io.inoa.fleet.registry.domain.GatewayRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.validator.TokenValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Validate gateway token.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
public class AuthTokenValidator implements TokenValidator {

	private final AuthTokenService service;
	private final GatewayRepository gatewayRepository;

	@Override
	public Publisher<Authentication> validateToken(String token, HttpRequest<?> request) {
		return service
				.validateToken(token)
				.flatMap(gatewayRepository::findByGatewayId)
				.map(GatewayAuthentication::new)
				.map(Authentication.class::cast)
				.map(Mono::just)
				.orElseGet(Mono::empty);
	}
}
