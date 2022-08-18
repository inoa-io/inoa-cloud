package io.inoa.fleet.mqtt;

import io.micronaut.runtime.Micronaut;

/**
 * Micronaut application.
 *
 * @author Stephan Schnabel
 */
public class Application {

	public static void main(String[] args) {
		Micronaut
				.build(args)
				.mainClass(Application.class)
				.banner(false)
				.start();
	}
}
