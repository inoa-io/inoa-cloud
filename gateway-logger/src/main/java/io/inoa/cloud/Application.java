package io.inoa.cloud;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut base application class.
 *
 * @author Fabian Schlegel
 */
public class Application {

	public static void main(String[] args) {
		Micronaut.build(args).banner(false).mainClass(Application.class).start();
	}
}
