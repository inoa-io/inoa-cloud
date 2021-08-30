package io.inoa.fleet.registry.infrastructure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Validator;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.fleet.registry.rest.HttpResponseAssertions;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

/**
 * Base for all integration tests.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class ComposeTest {

	@Inject
	Validator validator;
	@Inject
	public Auth auth;

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert200(executeable).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(HttpResponseAssertions.assert201(executeable).body());
	}

	private <T> T assertValid(T object) {
		if (object instanceof Iterable) {
			Iterable.class.cast(object).forEach(this::assertValid);
		} else {
			var violations = validator.validate(object);
			assertTrue(violations.isEmpty(), () -> "validation failed with:" + violations.stream()
					.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
					.collect(Collectors.joining()));
		}
		return object;
	}
}
