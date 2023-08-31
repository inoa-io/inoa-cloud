package io.inoa.test.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.micronaut.context.annotation.Context;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.validation.validator.Validator;

@Context
public class HttpAssertions {

	private static Validator VALIDATOR;

	HttpAssertions(Validator validator) {
		VALIDATOR = validator;
	}

	public static <T> T assert200(Supplier<HttpResponse<T>> executeable, String message) {
		return assertValid(assertStatus(HttpStatus.OK, executeable, message).body());
	}

	public static <T> T assert201(Supplier<HttpResponse<T>> executeable, String message) {
		return assertValid(assertStatus(HttpStatus.CREATED, executeable, message).body());
	}

	public static <T> void assert204(Supplier<HttpResponse<T>> executeable, String message) {
		assertStatus(HttpStatus.NO_CONTENT, executeable, message);
	}

	public static <T> HttpResponse<T> assertStatus(HttpStatus status, HttpResponse<T> response, String message) {
		assertEquals(status, response.getStatus(), message);
		return response;
	}

	public static <T> HttpResponse<T> assertStatus(HttpStatus status, Supplier<HttpResponse<T>> exec, String message) {
		HttpResponse<T> response = null;
		try {
			response = exec.get();
		} catch (HttpClientResponseException e) {
			response = (HttpResponse<T>) e.getResponse();
		}
		assertEquals(status, response.getStatus(), message);
		return response;
	}

	private static <T> T assertValid(T object) {
		if (object instanceof Iterable<?> iterable) {
			iterable.forEach(HttpAssertions::assertValid);
		} else {
			var violations = VALIDATOR.validate(object);
			assertTrue(violations.isEmpty(), () -> "validation failed with:" + violations.stream()
					.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
					.collect(Collectors.joining()));
		}
		return object;
	}
}
