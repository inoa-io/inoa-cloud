package io.inoa.cnpm.tenant;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut base application class.
 */
public class Application {

	public static void main(String[] args) {
		Micronaut.build(args).banner(false).mainClass(Application.class).start();
	}
}
