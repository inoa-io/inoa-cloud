package io.inoa.cloud.converter.common;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.cloud.converter.AbstractConverterTest;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import jakarta.inject.Inject;

/**
 * Test for {@link JsonConverter}.
 *
 * @author Stephan Schnabel
 */
public class JsonConverterTest extends AbstractConverterTest {

	@Inject
	JsonConverter converter;

	@DisplayName("success")
	@Test
	void success() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:json")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("{\"string\":\"sdf\",\"int\":4,\"double\":34.01,\"bool\":true,\"obj\":{}}".getBytes());
		var messages = convert(converter, hono, "example", "json");
		assertCount(2, messages);
	}

	@DisplayName("fail: empty json")
	@Test
	void failEmptyJson() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:json")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("{}".getBytes());
		assertEmpty(converter, hono, "example", "json");
	}

	@DisplayName("fail: invalid json")
	@Test
	void failInvalidJson() {
		var hono = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:json")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("nope".getBytes());
		assertEmpty(converter, hono, "example", "json");
	}
}
