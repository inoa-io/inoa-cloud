package io.inoa.test.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class TestAssertions {

	private final Validator validator;

	public <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(assertStatus(executeable, HttpStatus.OK).body());
	}

	public <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(assertStatus(executeable, HttpStatus.CREATED).body());
	}

	public <T> void assert204(Supplier<HttpResponse<T>> executeable) {
		assertStatus(executeable, HttpStatus.NO_CONTENT);
	}

	public <T> void assert401(Supplier<HttpResponse<T>> executeable) {
		assertStatus(executeable, HttpStatus.UNAUTHORIZED);
	}

	public <T> void assert403(Supplier<HttpResponse<T>> executeable) {
		assertStatus(executeable, HttpStatus.FORBIDDEN);
	}

	@SuppressWarnings("unchecked")
	private <T> HttpResponse<T> assertStatus(Supplier<HttpResponse<T>> executeable, HttpStatus status) {
		HttpResponse<T> response = null;
		try {
			response = executeable.get();
		} catch (HttpClientResponseException e) {
			response = (HttpResponse<T>) e.getResponse();
		}
		assertEquals(status, response.getStatus(), "invalid status");
		return response;
	}

	private <T> T assertValid(T object) {
		if (object instanceof Iterable) {
			((Iterable<?>) object).forEach(this::assertValid);
		} else {
			var violations = validator.validate(object);
			assertTrue(violations.isEmpty(), () -> "validation failed with:" + violations.stream()
					.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
					.collect(Collectors.joining()));
		}
		return object;
	}
}
