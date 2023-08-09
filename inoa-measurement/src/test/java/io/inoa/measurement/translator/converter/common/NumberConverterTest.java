package io.inoa.measurement.translator.converter.common;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.model.TelemetryRawVO;
import io.inoa.measurement.translator.converter.AbstractConverterTest;
import jakarta.inject.Inject;

public class NumberConverterTest extends AbstractConverterTest {

	@Inject
	NumberConverter converter;

	@DisplayName("success: long as number")
	@Test
	void successLong() {
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:number")
				.timestamp(Instant.now().toEpochMilli())
				.value("1234".getBytes());
		assertSingleValue(1234D, converter, raw, "example", "number");
	}

	@DisplayName("success: negatice double as number")
	@Test
	void successDouble() {
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:number")
				.timestamp(Instant.now().toEpochMilli())
				.value("-123.456".getBytes());
		assertSingleValue(-123.456D, converter, raw, "example", "number");
	}

	@DisplayName("success: with modifier")
	@Test
	void successModifier() {
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:number-with-modifier")
				.timestamp(Instant.now().toEpochMilli())
				.value("9876".getBytes());
		assertSingleValue(98760D, converter, raw, "example", "number-with-modifier");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:number")
				.timestamp(Instant.now().toEpochMilli())
				.value("NAN".getBytes());
		assertEmpty(converter, raw, "example", "number");
	}
}
