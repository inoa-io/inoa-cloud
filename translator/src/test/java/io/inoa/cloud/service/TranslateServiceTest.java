package io.inoa.cloud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.cloud.converter.DVH4013Converter;
import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

/**
 * Test for {@link TranslateService}.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestMethodOrder(MethodName.class)
public class TranslateServiceTest {

	@Inject
	TranslateService service;

	@DisplayName("DVH4013 - base64 to double")
	@Test
	void typeDVH4013() {
		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("urn:" + DVH4013Converter.DVH4013 + ":123:456")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("1234".getBytes());
		var actual = service.toInoa(tenantId, gatewayId, message).orElse(null);
		var expected = new InoaTelemetryMessageVO()
				.setTenantId(tenantId)
				.setGatewayId(gatewayId)
				.setUrn(message.getUrn())
				.setDeviceType(DVH4013Converter.DVH4013)
				.setDeviceId("123")
				.setSensor("456")
				.setTimestamp(Instant.ofEpochMilli(message.getTimestamp()))
				.setValue(1234D);
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("urn:" + DVH4013Converter.DVH4013 + ":123:456")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("NAN".getBytes());
		assertTrue(service.toInoa(tenantId, gatewayId, message).isEmpty(), "inoa");
	}

	@DisplayName("fail: no converter")
	@Test
	void failNoConverter() {
		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("urn:nope:08:15")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("1234".getBytes());
		assertTrue(service.toInoa(tenantId, gatewayId, message).isEmpty(), "inoa");
	}

	@DisplayName("fail: unsupported urn")
	@Test
	void failUnsupportedUrn() {
		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var message = new HonoTelemetryMessageVO()
				.setUrn("NOPE")
				.setTimestamp(Instant.now().toEpochMilli())
				.setValue("1234".getBytes());
		assertTrue(service.toInoa(tenantId, gatewayId, message).isEmpty(), "inoa");
	}
}
