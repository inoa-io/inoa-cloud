package io.inoa.fleet.thing;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut application.
 *
 */
public class Application {

	public static void main(String[] args) {
		Micronaut.build(args)
				.mainClass(Application.class)
				.defaultEnvironments("dev")
				.start();
	}
}
