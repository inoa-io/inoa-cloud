package io.inoa.cloud.converter.common;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.cloud.converter.AbstractConverterTest;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import jakarta.inject.Inject;

/**
 * Test for {@link NumberConverter}.
 *
 * @author Stephan Schnabel
 */
public class NumberConverterTest extends AbstractConverterTest {

	@Inject
	NumberConverter converter;

	@DisplayName("success: long as number")
	@Test
	void successLong() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:number")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("1234".getBytes());
		assertSingleValue(1234D, converter, hono, "example", "number");
	}

	@DisplayName("success: negatice double as number")
	@Test
	void successDouble() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:number")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("-123.456".getBytes());
		assertSingleValue(-123.456D, converter, hono, "example", "number");
	}

	@DisplayName("success: with modifier")
	@Test
	void successModifier() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:number-with-modifier")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("9876".getBytes());
		assertSingleValue(98760D, converter, hono, "example", "number-with-modifier");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:number")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("NAN".getBytes());
		assertEmpty(converter, hono, "example", "number");
	}
}
