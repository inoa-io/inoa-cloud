package io.inoa.fleet.mqtt.broker;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.mqtt.AbstractMqttTest;
import io.inoa.fleet.mqtt.HonoMqttClient;
import io.inoa.fleet.mqtt.MqttProperties;
import io.inoa.fleet.mqtt.listener.TestListener;
import jakarta.inject.Inject;

public class TelemtryTest extends AbstractMqttTest {

	@Inject
	MqttProperties properties;

	@DisplayName("telemetry message")
	@Test
	void telemetry(TestListener listener) throws MqttException {

		var url = "tcp://" + properties.getHost() + ":" + properties.getPort();
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var psk = UUID.randomUUID().toString().getBytes();
		var payload = UUID.randomUUID().toString().getBytes();

		var client = new HonoMqttClient(url, tenantId, gatewayId, psk);
		client.connect();
		client.publishTelemetry(payload);
		client.disconnect();

		var record = listener.await();
		assertEquals("hono.telemetry." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertArrayEquals(record.value(), payload, "payload");
	}

	@DisplayName("event message")
	@Test
	void event(TestListener listener) throws MqttException {

		var url = "tcp://" + properties.getHost() + ":" + properties.getPort();
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var psk = UUID.randomUUID().toString().getBytes();
		var payload = UUID.randomUUID().toString().getBytes();

		var client = new HonoMqttClient(url, tenantId, gatewayId, psk);
		client.connect();
		client.publishEvent(payload);
		client.disconnect();

		var record = listener.await();
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertArrayEquals(record.value(), payload, "payload");
	}
}
