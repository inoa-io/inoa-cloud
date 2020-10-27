package io.kokuwa.fleet.registry.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.kokuwa.fleet.registry.ApplicationProperties;
import io.kokuwa.fleet.registry.HttpResponseAssertions;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;

/**
 * Base for all unit tests.
 *
 * @author Stephan Schnabel
 */
@MicronautTest(transactional = false, environments = "h2")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractUnitTest {

	@Inject
	public Validator validator;
	@Inject
	public Instant now;
	@Inject
	public AuthProvider auth;
	@Inject
	public ApplicationProperties properties;

	void init() {
		properties.getGateway().getToken().setForceNotBefore(true);
		properties.getGateway().getToken().setForceJwtId(true);
		properties.getGateway().getToken().setForceIssuedAt(true);
		properties.getGateway().getToken().setIssuedAtThreshold(Optional.of(Duration.ofSeconds(5)));
		properties.getAuth().setExpirationDuration(Duration.ofSeconds(5));
		properties.getAuth().setAudience(UUID.randomUUID().toString());
		properties.getAuth().setIssuer(UUID.randomUUID().toString());
	}

	public <T> T assertValid(T object) {
		var violations = validator.validate(object);
		assertTrue(violations.isEmpty(), () -> "validation failed with:" + violations.stream()
				.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
				.collect(Collectors.joining()));
		return object;
	}

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert200(executeable).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert201(executeable).body());
	}
}
