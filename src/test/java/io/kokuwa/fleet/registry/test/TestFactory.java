package io.kokuwa.fleet.registry.test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import javax.inject.Singleton;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;

/**
 * Factory for provided fixed time for tests.
 *
 * @author Stephan Schnabel
 */
@Factory
public class TestFactory {

	@Singleton
	Instant now() {
		return OffsetDateTime.of(LocalDate.ofYearDay(2000, 1), LocalTime.MIN, ZoneOffset.UTC).toInstant();
	}

	@Primary
	@Singleton
	Clock clock(Instant now) {
		return Clock.fixed(now, ZoneOffset.UTC);
	}
}
