package io.inoa.measurement.exporter;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut application.
 *
 * @author sschnabe
 */
public class Application {

	public static void main(String[] args) {
		Micronaut.build(args).banner(false).mainClass(Application.class).start();
	}
}
