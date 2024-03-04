package io.inoa.controller.ui;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactivestreams.Publisher;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.ServerAuthentication;
import io.micronaut.security.filters.AuthenticationFetcher;
import io.micronaut.security.token.Claims;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * Create authentication fetcher for local angular development.
 *
 * @author stephan.schnabel@grayc.de
 */
@Requires(property = "micronaut.application.name", value = "inoa-k3s")
@Singleton
@Slf4j
public class AngularAuthenticationFetcher implements AuthenticationFetcher<HttpRequest<?>> {

	AngularAuthenticationFetcher() {
		log.error("Angular authentication fetcher is enabled, this should not happen in production.");
	}

	@Override
	public Publisher<Authentication> fetchAuthentication(HttpRequest<?> request) {
		return request.getHeaders().get(HttpHeaders.REFERER, String.class)
				.filter(referer -> referer.startsWith("http://localhost:4200"))
				.map(d -> new ServerAuthentication("admin", Set.of(), Map.of(
						Claims.EXPIRATION_TIME, Date.from(Instant.now().plusSeconds(60)),
						"tenants", List.of("inoa"),
						"given_name", "Super",
						"family_name", "Admin",
						"email", "superadmin@example.org")))
				.map(Authentication.class::cast)
				.map(Publishers::just).orElseGet(Publishers::empty);
	}
}
