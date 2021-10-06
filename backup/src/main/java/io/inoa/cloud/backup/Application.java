package io.inoa.cloud.backup;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut base application class.
 *
 * @author Stephan Schnabel
 */
public class Application {

	public static void main(String[] args) {
		Micronaut.build(args).banner(false).mainClass(Application.class).start();
	}
}
