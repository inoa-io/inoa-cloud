package io.inoa.measurement.translator.converter.common;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.measurement.translator.AbstractTranslatorTest;
import io.inoa.messaging.TelemetryRawVO;

/**
 * Test for {@link JsonConverter}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("translator: converter json")
public class JsonConverterTest extends AbstractTranslatorTest {

	@Inject
	JsonConverter converter;

	@DisplayName("success")
	@Test
	void success() {
		var raw = TelemetryRawVO.of(
				"urn:example:0815:json",
				"{\"string\":\"sdf\",\"int\":4,\"double\":34.01,\"bool\":true,\"obj\":{}}");
		var messages = convert(converter, raw, "example", "json");
		assertCount(2, messages);
	}

	@DisplayName("fail: empty json")
	@Test
	void failEmptyJson() {
		var raw = TelemetryRawVO.of("urn:example:0815:json", "{}");
		assertEmpty(converter, raw, "example", "json");
	}

	@DisplayName("fail: invalid json")
	@Test
	void failInvalidJson() {
		var raw = TelemetryRawVO.of("urn:example:0815:json", "nope");
		assertEmpty(converter, raw, "example", "json");
	}
}
