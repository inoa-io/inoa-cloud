package io.inoa.cloud.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

/**
 * Abstract test.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
public abstract class AbstractConverterTest {

	protected static List<InoaTelemetryMessageVO> convert(
			Converter converter,
			HonoTelemetryMessageVO hono,
			String type,
			String sensor) {
		assertTrue(converter.supports(type, sensor), "converter supports");
		return converter.convert(hono, type, sensor).collect(Collectors.toList());
	}

	protected static void assertEmpty(
			Converter converter,
			HonoTelemetryMessageVO hono,
			String type,
			String sensor) {
		assertCount(0, convert(converter, hono, type, sensor));
	}

	protected static void assertSingleValue(
			Double expected,
			Converter converter,
			HonoTelemetryMessageVO hono,
			String type,
			String sensor) {
		var messages = convert(converter, hono, type, sensor);
		assertCount(1, messages);
		assertEquals(expected, messages.get(0).getValue(), "inoa message value");
	}

	protected static void assertValue(Double expected, List<InoaTelemetryMessageVO> messages) {
		assertCount(1, messages);
		assertEquals(expected, messages.get(0).getValue(), "inoa message value");
	}

	protected static void assertCount(int expected, List<InoaTelemetryMessageVO> messages) {
		assertEquals(expected, messages.size(), "inoa message count, messages: " + messages);
	}
}
