package io.inoa.controller.translator.converter.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.controller.translator.AbstractTranslatorTest;
import io.inoa.messaging.TelemetryRawVO;
import jakarta.inject.Inject;

/**
 * Test for {@link NumberConverter}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("translator: converter number")
public class NumberConverterTest extends AbstractTranslatorTest {

	@Inject NumberConverter converter;

	@DisplayName("success: long as number")
	@Test
	void successLong() {
		var raw = TelemetryRawVO.of("urn:example:0815:number", "1234");
		assertSingleValue(1234D, converter, raw, "example", "number");
	}

	@DisplayName("success: negatice double as number")
	@Test
	void successDouble() {
		var raw = TelemetryRawVO.of("urn:example:0815:number", "-123.456");
		assertSingleValue(-123.456D, converter, raw, "example", "number");
	}

	@DisplayName("success: with modifier")
	@Test
	void successModifier() {
		var raw = TelemetryRawVO.of("urn:example:0815:number-with-modifier", 9876);
		assertSingleValue(98760D, converter, raw, "example", "number-with-modifier");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var raw = TelemetryRawVO.of("urn:example:0815:number", "NAN");
		assertEmpty(converter, raw, "example", "number");
	}
}
