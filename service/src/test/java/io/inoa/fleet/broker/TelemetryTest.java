package io.inoa.fleet.broker;

import static io.inoa.fleet.broker.MqttAssertions.assertConnectionEvent;
import static io.inoa.fleet.broker.MqttAssertions.assertHeader;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.AbstractUnitTest;
import io.inoa.fleet.registry.KafkaHeader;
import jakarta.inject.Inject;

public class TelemetryTest extends AbstractUnitTest {

	@Inject
	MqttProperties properties;

	@DisplayName("telemetry message")
	@Test
	void telemetry() throws MqttException {

		var url = "ssl://" + properties.getHost() + ":" + properties.getTls().getPort();
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var psk = UUID.randomUUID().toString().getBytes();
		var payload = UUID.randomUUID().toString().getBytes();

		var client = new MqttServiceClient(url, tenantId, gatewayId, psk);
		client.trustAllCertificates().connect();
		assertConnectionEvent(kafka, tenantId, gatewayId, true);
		client.publishTelemetry(payload);
		var record = kafka.awaitHonoTelemetry(tenantId, gatewayId);
		client.disconnect();
		assertConnectionEvent(kafka, tenantId, gatewayId, false);

		assertArrayEquals(record.value(), payload, "payload");
		assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
		assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_JSON);
		assertHeader(record, KafkaHeader.QOS, 1);
		assertHeader(record, KafkaHeader.ORIG_ADDRESS, "telemetry");
	}

	@DisplayName("event message")
	@Test
	void event() throws MqttException {

		var url = "ssl://" + properties.getHost() + ":" + properties.getTls().getPort();
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var psk = UUID.randomUUID().toString().getBytes();
		var payload = Map.of("uuid", UUID.randomUUID());

		var client = new MqttServiceClient(url, tenantId, gatewayId, psk);
		client.trustAllCertificates().connect();
		assertConnectionEvent(kafka, tenantId, gatewayId, true);
		client.publishEvent(assertDoesNotThrow(() -> mapper.writeValueAsBytes(payload)));
		var record = kafka.awaitHonoEvent(tenantId, gatewayId);
		client.disconnect();
		assertConnectionEvent(kafka, tenantId, gatewayId, false);

		assertEquals(payload.toString(), record.value().toString(), "payload");
		assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
		assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_JSON);
		assertHeader(record, KafkaHeader.QOS, 1);
		assertHeader(record, KafkaHeader.ORIG_ADDRESS, "event");
	}
}
