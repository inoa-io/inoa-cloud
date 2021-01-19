package io.kokuwa.fleet.registry.auth;

import javax.inject.Singleton;

import org.reactivestreams.Publisher;

import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.validator.TokenValidator;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;

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
	@SuppressWarnings("deprecation")
	public Publisher<Authentication> validateToken(String token) {
		return service.validateToken(token)
				.map(id -> gatewayRepository.findById(id).toFlowable().map(GatewayAuthentication::new))
				.orElseGet(Flowable::empty)
				.cast(Authentication.class);
	}
}
