package io.inoa.measurement.translator.converter.common;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.model.TelemetryRawVO;
import io.inoa.measurement.translator.converter.AbstractConverterTest;
import jakarta.inject.Inject;

public class JsonConverterTest extends AbstractConverterTest {

	@Inject
	JsonConverter converter;

	@DisplayName("success")
	@Test
	void success() {
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:json")
				.timestamp(Instant.now().toEpochMilli())
				.value("{\"string\":\"sdf\",\"int\":4,\"double\":34.01,\"bool\":true,\"obj\":{}}".getBytes());
		var messages = convert(converter, raw, "example", "json");
		assertCount(2, messages);
	}

	@DisplayName("fail: empty json")
	@Test
	void failEmptyJson() {
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:json")
				.timestamp(Instant.now().toEpochMilli())
				.value("{}".getBytes());
		assertEmpty(converter, raw, "example", "json");
	}

	@DisplayName("fail: invalid json")
	@Test
	void failInvalidJson() {
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:json")
				.timestamp(Instant.now().toEpochMilli())
				.value("nope".getBytes());
		assertEmpty(converter, raw, "example", "json");
	}
}
