package io.inoa.fleet.registry.auth;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.validator.TokenValidator;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * Validate gateway token.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
public class GatewayTokenValidator implements TokenValidator<HttpRequest<?>> {

  private final GatewayTokenService service;

  @Override
  public Publisher<Authentication> validateToken(String token, HttpRequest<?> request) {
    return request.getPath().startsWith("/gateway/")
        ? Mono.just(service.getGatewayFromToken(token))
            .map(GatewayAuthentication::new)
            .map(Authentication.class::cast)
        : Mono.empty();
  }
}
