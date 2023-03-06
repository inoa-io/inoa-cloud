package io.inoa.measurement.translator.converter.common;

import static io.inoa.measurement.translator.converter.LogSink.assertMessage;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.translator.converter.AbstractConverterTest;
import io.inoa.measurement.translator.converter.LogSink;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

public class S0ConverterTest extends AbstractConverterTest {

	@Inject
	S0Converter converter;

	@BeforeEach
	void setup() {
		LogSink.setUp(S0Converter.class);
	}

	@DisplayName("success: with modifier")
	@Test
	void successWithModifier() {
		var telemetry = telemetry("1234567");
		assertSingleValue(1234.567, converter, telemetry, "s0", "gas");
		assertMessage(S0Converter.class, "S0 count has value: 1234567");
	}

	@DisplayName("fail: count empty")
	@Test
	void failByteCountEmpty() {
		assertEmpty(converter, telemetry(""), "s0", "gas");
		assertMessage(S0Converter.class, "Retrieved invalid S0 message (empty)");
	}

	@DisplayName("fail: count NaN")
	@Test
	void failByteCountNaN() {
		assertEmpty(converter, telemetry("DreiZehnKommaZweiEins"), "s0", "gas");
		assertMessage(S0Converter.class, "Retrieved invalid S0 message (not a number)");
	}

	@DisplayName("fail: count negative")
	@Test
	void failByteCountNegative() {
		assertEmpty(converter, telemetry("-42"), "s0", "gas");
		assertMessage(S0Converter.class, "Retrieved invalid S0 message (negative value)");
	}

	@SneakyThrows
	private TelemetryRawVO telemetry(String value) {
		return new TelemetryRawVO()
				.urn("urn:s0:0:gas")
				.timestamp(Instant.now().toEpochMilli())
				.value(value.getBytes(StandardCharsets.UTF_8));
	}
}
