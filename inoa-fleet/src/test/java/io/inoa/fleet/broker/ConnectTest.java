package io.inoa.fleet.broker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.registry.KafkaHeader;
import jakarta.inject.Inject;

public class ConnectTest extends AbstractMqttTest {

	@Inject
	MqttProperties properties;
	@Inject
	ObjectMapper mapper;

	@DisplayName("connect & disconnect graceful")
	@Test
	void graceful(TestListener listener) throws MqttException, IOException {

		var url = "tcp://" + properties.getHost() + ":" + properties.getPort();
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var psk = UUID.randomUUID().toString().getBytes();
		var client = new MqttClient(url, tenantId, gatewayId, psk);
		var clientId = client.getClientId();

		client.connect();
		var record = listener.await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertEquals(mapper.readValue(record.value(), Map.class),
				Map.of("cause", "connected", "remote-id", clientId, "source", "inoa-mqtt"), "value");
		assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
		assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
		assertHeader(record, KafkaHeader.QOS, 1);

		client.disconnect();
		record = listener.await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertEquals(mapper.readValue(record.value(), Map.class),
				Map.of("cause", "disconnected", "remote-id", clientId, "source", "inoa-mqtt"), "value");
		assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
		assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
		assertHeader(record, KafkaHeader.QOS, 1);
	}

	@DisplayName("connect & disconnect hard")
	@Test
	void hard(TestListener listener) throws MqttException, IOException {

		var url = "tcp://" + properties.getHost() + ":" + properties.getPort();
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var psk = UUID.randomUUID().toString().getBytes();
		var client = new MqttClient(url, tenantId, gatewayId, psk);
		var clientId = client.getClientId();

		client.connect();
		var record = listener.await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertEquals(mapper.readValue(record.value(), Map.class),
				Map.of("cause", "connected", "remote-id", clientId, "source", "inoa-mqtt"), "value");
		assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
		assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
		assertHeader(record, KafkaHeader.QOS, 1);

		client.disconnectWithoutNotification();
		record = listener.await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertEquals(mapper.readValue(record.value(), Map.class),
				Map.of("cause", "disconnected", "remote-id", clientId, "source", "inoa-mqtt"), "value");
		assertHeader(record, KafkaHeader.TENANT_ID, tenantId);
		assertHeader(record, KafkaHeader.DEVICE_ID, gatewayId);
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
		assertHeader(record, KafkaHeader.QOS, 1);
	}
}
