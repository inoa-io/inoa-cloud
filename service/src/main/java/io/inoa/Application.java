package io.inoa;

import io.micronaut.runtime.Micronaut;

public class Application {

	public static void main(String[] args) {
		Micronaut.build(args).banner(false).mainClass(Application.class)
				.environments("exporter", "translator", "app", "mqtt")
				.start();
	}
}
