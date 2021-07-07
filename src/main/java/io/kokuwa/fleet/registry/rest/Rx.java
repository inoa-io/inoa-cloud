package io.kokuwa.fleet.registry.rest;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * RX util to avoid boiler plate code.
 *
 * @author Stephan Schnabel
 */
public class Rx {

	public static Action throwNotFound(Class<?> type) {
		return throwStatus(HttpStatus.NOT_FOUND, type.getSimpleName() + " not found.");
	}

	public static Consumer<Boolean> throwConflict() {
		return exists -> {
			if (exists) {
				throw new HttpStatusException(HttpStatus.CONFLICT, "already exists");
			}
		};
	}

	private static Action throwStatus(HttpStatus status, String message) {
		return () -> {
			throw new HttpStatusException(status, message);
		};
	}
}
