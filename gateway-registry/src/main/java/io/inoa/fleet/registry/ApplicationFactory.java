package io.inoa.fleet.registry;

import java.time.Clock;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;

/**
 * Factory for application.
 *
 * @author Stephan Schnabel
 */
@Factory
public class ApplicationFactory {

	@Singleton
	Clock clock() {
		return Clock.systemUTC();
	}
}
