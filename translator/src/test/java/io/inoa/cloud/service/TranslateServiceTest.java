package io.inoa.cloud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

/**
 * Test for {@link TranslateService}.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
public class TranslateServiceTest {

	@Inject
	TranslateService service;

	@DisplayName("Example - base64 to single value")
	@Test
	void typeExampleSingleValue() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:number")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("1234".getBytes());
		var actual = service.toInoa(tenantId, gatewayId, message);
		var expected = List.of(new InoaTelemetryMessageVO()
				.setTenantId(tenantId)
				.setGatewayId(gatewayId)
				.setUrn(message.getUrn())
				.setDeviceType("example")
				.setDeviceId("0815")
				.setSensor("number")
				.setTimestamp(Instant.ofEpochMilli(message.getTimestamp()))
				.setValue(1234D));
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("Example - base64 to multiple values")
	@Test
	void typeExampleMultipleValues() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:json")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("{\"string\":\"sdf\",\"int\":4,\"double\":34.01,\"bool\":true,\"obj\":{}}".getBytes());
		var actual = service.toInoa(tenantId, gatewayId, message);
		var expected = List.of(
				new InoaTelemetryMessageVO()
						.setTenantId(tenantId)
						.setGatewayId(gatewayId)
						.setUrn(message.getUrn() + ".int")
						.setDeviceType("example")
						.setDeviceId("0815")
						.setSensor("json.int")
						.setTimestamp(Instant.ofEpochMilli(message.getTimestamp()))
						.setValue(4D),
				new InoaTelemetryMessageVO()
						.setTenantId(tenantId)
						.setGatewayId(gatewayId)
						.setUrn(message.getUrn() + ".double")
						.setDeviceType("example")
						.setDeviceId("0815")
						.setSensor("json.double")
						.setTimestamp(Instant.ofEpochMilli(message.getTimestamp()))
						.setValue(34.01D));
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:number")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("NAN".getBytes());
		assertTrue(service.toInoa(tenantId, gatewayId, message).isEmpty(), "inoa");
	}

	@DisplayName("fail: no converter")
	@Test
	void failNoConverter() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("urn:example:0815:nope")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("1234".getBytes());
		assertTrue(service.toInoa(tenantId, gatewayId, message).isEmpty(), "inoa");
	}

	@DisplayName("fail: unsupported urn")
	@Test
	void failUnsupportedUrn() {
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("NOPE")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("1234".getBytes());
		assertTrue(service.toInoa(tenantId, gatewayId, message).isEmpty(), "inoa");
	}
}
