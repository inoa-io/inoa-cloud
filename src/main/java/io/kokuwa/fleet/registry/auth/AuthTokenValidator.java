package io.kokuwa.fleet.registry.auth;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Singleton;

import org.reactivestreams.Publisher;

import io.kokuwa.fleet.registry.domain.Gateway;
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

	@SuppressWarnings("deprecation")
	@Override
	public Publisher<Authentication> validateToken(String token) {
		Optional<UUID> uuid = service.validateToken(token);
		if (uuid.isEmpty()) {
			return Flowable.empty();
		}
		Optional<Gateway> optionalGateway = gatewayRepository.findByExternalId(uuid.get());
		if (optionalGateway.isEmpty()) {
			return Flowable.empty();
		}
		return Flowable.just(new GatewayAuthentication(optionalGateway.get()));
	}
}
