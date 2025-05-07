package io.inoa.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.validation.validator.Validator;

/**
 * Assertions for micronaut http requests.
 *
 * @author stephan.schnabel@grayc.de
 */
public final class HttpAssertions {

	private static final Validator VALIDATOR = Validator.getInstance();

	public static <T> T assert200(Supplier<HttpResponse<T>> executeable) {
		return assertValid(assertStatus(HttpStatus.OK, executeable, null).body());
	}

	public static <T> T assert200(Supplier<HttpResponse<T>> executeable, String message) {
		return assertValid(assertStatus(HttpStatus.OK, executeable, message).body());
	}

	public static <T> T assert201(Supplier<HttpResponse<T>> executeable) {
		return assertValid(assertStatus(HttpStatus.CREATED, executeable, null).body());
	}

	public static <T> void assert204(Supplier<HttpResponse<T>> executeable) {
		assertStatus(HttpStatus.NO_CONTENT, executeable, null);
	}

	public static <T> void assert204(Supplier<HttpResponse<T>> executeable, String message) {
		assertStatus(HttpStatus.NO_CONTENT, executeable, message);
	}

	public static <T> JsonError assert400(Supplier<HttpResponse<T>> executeable) {
		return assertValid(
				assertStatus(HttpStatus.BAD_REQUEST, executeable, null).getBody(JsonError.class).get());
	}

	public static <T> HttpResponse<T> assert401(Supplier<HttpResponse<T>> executeable) {
		return assertStatus(HttpStatus.UNAUTHORIZED, executeable, null);
	}

	public static <T> HttpResponse<T> assert403(Supplier<HttpResponse<T>> executeable) {
		return assertStatus(HttpStatus.FORBIDDEN, executeable, null);
	}

	public static <T> HttpResponse<T> assert404(Supplier<HttpResponse<T>> executeable) {
		return assertStatus(HttpStatus.NOT_FOUND, executeable, null);
	}

	public static <T> void assert404(String message, Supplier<HttpResponse<T>> executeable) {
		var response = assert404(executeable);
		var error = response.getBody(JsonError.class).orElse(null);
		assertNotNull(
				error, () -> "error json missing, response was: " + response.getBody(String.class));
		assertEquals(message, error.getMessage(), "error message invalid, error was: " + error);
	}

	public static <T> HttpResponse<T> assert409(Supplier<HttpResponse<T>> executeable) {
		return assertStatus(HttpStatus.CONFLICT, executeable, null);
	}

	static <T> HttpResponse<T> assertStatus(
			HttpStatus status, HttpResponse<T> response, String message) {
		assertEquals(status, response.getStatus(), message);
		return response;
	}

	@SuppressWarnings("unchecked")
	static <T> HttpResponse<T> assertStatus(
			HttpStatus status, Supplier<HttpResponse<T>> executeable, String message) {
		try {
			var response = executeable.get();
			assertEquals(status, response.getStatus(), message);
			return response;
		} catch (HttpClientResponseException e) {
			assertEquals(status, e.getResponse().getStatus(), message);
			return (HttpResponse<T>) e.getResponse();
		}
	}

	public static <T> T assertValid(T object) {
		if (object instanceof Iterable<?> iterable) {
			iterable.forEach(HttpAssertions::assertValid);
		} else if (object != null && !(object instanceof Map)) {
			var violations = VALIDATOR.validate(object);
			assertTrue(
					violations.isEmpty(),
					() -> "validation of object <"
							+ object
							+ "> failed with:"
							+ violations.stream()
									.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
									.collect(Collectors.joining()));
		}
		return object;
	}
}
