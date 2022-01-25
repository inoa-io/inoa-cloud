package io.inoa.measurement.translator.converter.common;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.translator.converter.AbstractConverterTest;
import jakarta.inject.Inject;

public class NumberConverterTest extends AbstractConverterTest {

	@Inject
	NumberConverter converter;

	@DisplayName("success: long as number")
	@Test
	void successLong() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:number")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("1234".getBytes());
		assertSingleValue(1234D, converter, raw, "example", "number");
	}

	@DisplayName("success: negatice double as number")
	@Test
	void successDouble() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:number")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("-123.456".getBytes());
		assertSingleValue(-123.456D, converter, raw, "example", "number");
	}

	@DisplayName("success: with modifier")
	@Test
	void successModifier() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:number-with-modifier")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("9876".getBytes());
		assertSingleValue(98760D, converter, raw, "example", "number-with-modifier");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var raw = new TelemetryRawVO()
				.setUrn("urn:example:0815:number")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("NAN".getBytes());
		assertEmpty(converter, raw, "example", "number");
	}
}
