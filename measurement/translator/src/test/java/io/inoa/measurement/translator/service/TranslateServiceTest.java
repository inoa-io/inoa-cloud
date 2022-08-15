package io.inoa.measurement.translator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.telemetry.TelemetryVO;
import io.inoa.measurement.translator.AbstractTest;
import jakarta.inject.Inject;

public class TranslateServiceTest extends AbstractTest {

	@Inject
	TranslateService service;

	@DisplayName("Example - base64 to single value")
	@Test
	void typeExampleSingleValue() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:number")
				.timestamp(Instant.now().toEpochMilli())
				.value("1234".getBytes());
		var actual = service.translate(tenantId, gatewayId, raw);
		var expected = List.of(new TelemetryVO()
				.tenantId(tenantId)
				.gatewayId(gatewayId)
				.urn(raw.getUrn())
				.deviceType("example")
				.deviceId("0815")
				.sensor("number")
				.timestamp(Instant.ofEpochMilli(raw.getTimestamp()))
				.value(1234D));
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("Example - base64 to multiple values")
	@Test
	void typeExampleMultipleValues() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:json")
				.timestamp(Instant.now().toEpochMilli())
				.value("{\"string\":\"sdf\",\"int\":4,\"double\":34.01,\"bool\":true,\"obj\":{}}".getBytes());
		var actual = service.translate(tenantId, gatewayId, raw);
		var expected = List.of(
				new TelemetryVO()
						.tenantId(tenantId)
						.gatewayId(gatewayId)
						.urn(raw.getUrn() + ".int")
						.deviceType("example")
						.deviceId("0815")
						.sensor("json.int")
						.timestamp(Instant.ofEpochMilli(raw.getTimestamp()))
						.value(4D),
				new TelemetryVO()
						.tenantId(tenantId)
						.gatewayId(gatewayId)
						.urn(raw.getUrn() + ".double")
						.deviceType("example")
						.deviceId("0815")
						.sensor("json.double")
						.timestamp(Instant.ofEpochMilli(raw.getTimestamp()))
						.value(34.01D));
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:number")
				.timestamp(Instant.now().toEpochMilli())
				.value("NAN".getBytes());
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}

	@DisplayName("fail: no converter")
	@Test
	void failNoConverter() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:nope")
				.timestamp(Instant.now().toEpochMilli())
				.value("1234".getBytes());
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}

	@DisplayName("fail: unsupported urn")
	@Test
	void failUnsupportedUrn() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var raw = new TelemetryRawVO()
				.urn("NOPE")
				.timestamp(Instant.now().toEpochMilli())
				.value("1234".getBytes());
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}
}
