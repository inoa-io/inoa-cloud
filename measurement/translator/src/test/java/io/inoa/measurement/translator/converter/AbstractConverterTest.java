package io.inoa.measurement.translator.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.telemetry.TelemetryVO;
import io.inoa.measurement.translator.AbstractTest;

public abstract class AbstractConverterTest extends AbstractTest {

	protected static List<TelemetryVO> convert(
			Converter converter,
			TelemetryRawVO raw,
			String type,
			String sensor) {
		assertTrue(converter.supports(type, sensor), "converter supports");
		return converter.convert(raw, type, sensor).toList();
	}

	protected static void assertEmpty(
			Converter converter,
			TelemetryRawVO raw,
			String type,
			String sensor) {
		assertCount(0, convert(converter, raw, type, sensor));
	}

	protected static void assertSingleValue(
			Double expected,
			Converter converter,
			TelemetryRawVO raw,
			String type,
			String sensor) {
		var messages = convert(converter, raw, type, sensor);
		assertCount(1, messages);
		assertEquals(expected, messages.get(0).getValue(), "inoa message value");
	}

	protected static void assertValue(Double expected, List<TelemetryVO> telemetryList) {
		assertCount(1, telemetryList);
		assertEquals(expected, telemetryList.get(0).getValue(), "telemetry value");
	}

	protected static void assertCount(int expected, List<TelemetryVO> telemetryList) {
		assertEquals(expected, telemetryList.size(), "telemetry count: " + telemetryList);
	}
}
