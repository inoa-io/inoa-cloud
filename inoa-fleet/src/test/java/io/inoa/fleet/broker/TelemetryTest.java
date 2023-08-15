package io.inoa.fleet.broker;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.KafkaHeader;
import jakarta.inject.Inject;

public class TelemetryTest extends AbstractMqttTest {

	@Inject
	MqttProperties properties;

	@DisplayName("telemetry message")
	@Test
	void telemetry(TestKafkaListener listener) throws MqttException {

		var url = "ssl://" + properties.getHost() + ":" + properties.getTls().getPort();
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var psk = UUID.randomUUID().toString().getBytes();
		var payload = UUID.randomUUID().toString().getBytes();

		var client = new MqttServiceClient(url, tenantId, gatewayId, psk);
		client.trustAllCertificates().connect();
		listener.awaitConnection(tenantId, gatewayId, true);
		client.publishTelemetry(payload);
		var record = listener.await(tenantId, gatewayId);
		client.disconnect();
		listener.awaitConnection(tenantId, gatewayId, false);

		assertEquals("hono.telemetry." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertArrayEquals(record.value(), payload, "payload");
		assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
		assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_JSON);
		assertHeader(record, KafkaHeader.QOS, 1);
		assertHeader(record, KafkaHeader.ORIG_ADDRESS, "telemetry");
	}

	@DisplayName("event message")
	@Test
	void event(TestKafkaListener listener) throws MqttException {

		var url = "ssl://" + properties.getHost() + ":" + properties.getTls().getPort();
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var psk = UUID.randomUUID().toString().getBytes();
		var payload = UUID.randomUUID().toString().getBytes();

		var client = new MqttServiceClient(url, tenantId, gatewayId, psk);
		client.trustAllCertificates().connect();
		listener.awaitConnection(tenantId, gatewayId, true);
		client.publishEvent(payload);
		var record = listener.await(tenantId, gatewayId);
		client.disconnect();
		listener.awaitConnection(tenantId, gatewayId, false);

		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertArrayEquals(record.value(), payload, "payload");
		assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
		assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_JSON);
		assertHeader(record, KafkaHeader.QOS, 1);
		assertHeader(record, KafkaHeader.ORIG_ADDRESS, "event");
	}
}
