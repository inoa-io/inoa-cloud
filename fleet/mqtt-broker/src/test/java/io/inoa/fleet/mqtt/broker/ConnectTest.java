package io.inoa.fleet.mqtt.broker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.mqtt.AbstractMqttTest;
import io.inoa.fleet.mqtt.HonoMqttClient;
import io.inoa.fleet.mqtt.MqttHeader;
import io.inoa.fleet.mqtt.MqttProperties;
import io.inoa.fleet.mqtt.listener.TestListener;
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
		var gatewayId = UUID.randomUUID();
		var psk = UUID.randomUUID().toString().getBytes();
		var client = new HonoMqttClient(url, tenantId, gatewayId, psk);
		var clientId = client.getClientId();

		client.connect();
		var record = listener.await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertEquals(mapper.readValue(record.value(), Map.class),
				Map.of("cause", "connected", "remote-id", clientId, "source", "inoa-mqtt"), "value");
		assertHeader(record, MqttHeader.TENANT_ID, tenantId);
		assertHeader(record, MqttHeader.DEVICE_ID, gatewayId);
		assertHeader(record, MqttHeader.CONTENT_TYPE, MqttHeader.CONTENT_TYPE_EVENT_DC);
		assertHeader(record, MqttHeader.QOS, 1);

		client.disconnect();
		record = listener.await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertEquals(mapper.readValue(record.value(), Map.class),
				Map.of("cause", "disconnected", "remote-id", clientId, "source", "inoa-mqtt"), "value");
		assertHeader(record, MqttHeader.TENANT_ID, tenantId);
		assertHeader(record, MqttHeader.DEVICE_ID, gatewayId);
		assertHeader(record, MqttHeader.CONTENT_TYPE, MqttHeader.CONTENT_TYPE_EVENT_DC);
		assertHeader(record, MqttHeader.QOS, 1);
	}

	@DisplayName("connect & disconnect hard")
	@Test
	void hard(TestListener listener) throws MqttException, IOException {

		var url = "tcp://" + properties.getHost() + ":" + properties.getPort();
		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var psk = UUID.randomUUID().toString().getBytes();
		var client = new HonoMqttClient(url, tenantId, gatewayId, psk);
		var clientId = client.getClientId();

		client.connect();
		var record = listener.await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertEquals(mapper.readValue(record.value(), Map.class),
				Map.of("cause", "connected", "remote-id", clientId, "source", "inoa-mqtt"), "value");
		assertHeader(record, MqttHeader.TENANT_ID, tenantId);
		assertHeader(record, MqttHeader.DEVICE_ID, gatewayId);
		assertHeader(record, MqttHeader.CONTENT_TYPE, MqttHeader.CONTENT_TYPE_EVENT_DC);
		assertHeader(record, MqttHeader.QOS, 1);

		client.disconnectWithoutNotification();
		record = listener.await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(record.key(), gatewayId, "key");
		assertEquals(mapper.readValue(record.value(), Map.class),
				Map.of("cause", "disconnected", "remote-id", clientId, "source", "inoa-mqtt"), "value");
		assertHeader(record, MqttHeader.TENANT_ID, tenantId);
		assertHeader(record, MqttHeader.DEVICE_ID, gatewayId);
		assertHeader(record, MqttHeader.CONTENT_TYPE, MqttHeader.CONTENT_TYPE_EVENT_DC);
		assertHeader(record, MqttHeader.QOS, 1);
	}
}
