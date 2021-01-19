package io.kokuwa.fleet.registry;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut application.
 *
 * @author Stephan Schnabel
 */
public class Application {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Micronaut.build(args)
				.mainClass(Application.class)
				.defaultEnvironments("dev")
				.start();
	}
}
