package io.inoa.fleet;

import java.time.Clock;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

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

	@Singleton
	ObjectMapper cloudEventObjectMapper() {
		return new ObjectMapper().findAndRegisterModules();
	}
}
