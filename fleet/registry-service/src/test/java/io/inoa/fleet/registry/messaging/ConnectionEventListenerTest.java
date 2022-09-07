package io.inoa.fleet.registry.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.KafkaHeader;
import jakarta.inject.Inject;

@DisplayName("kafka: Connection Event")
public class ConnectionEventListenerTest extends AbstractKafkaTest {

	@Inject
	ConnectionEventProducer producer;

	@DisplayName("connect, disconnect and reconnect again")
	@Test
	void connectDisconnectReconnect() {

		var gateway = data.gateway();
		assertNull(gateway.getStatus().getMqtt().getTimestamp(), "initial timestamp");
		assertFalse(gateway.getStatus().getMqtt().getConnected(), "initial connected");

		var timestamp = Instant.parse("2000-01-01T00:00:00Z");
		producer.send(gateway, timestamp, true);
		var mqtt = Awaitility.await().until(() -> data.find(gateway).getStatus().getMqtt(), m -> m.getConnected());
		assertEquals(timestamp, mqtt.getTimestamp(), "connected timestamp");
		assertTrue(mqtt.getConnected(), "connected");

		timestamp = Instant.parse("2001-01-01T00:00:00Z");
		producer.send(gateway, timestamp, false);
		mqtt = Awaitility.await().until(() -> data.find(gateway).getStatus().getMqtt(), m -> !m.getConnected());
		assertEquals(timestamp, mqtt.getTimestamp(), "disconnected timestamp");
		assertFalse(mqtt.getConnected(), "disconnected");

		timestamp = Instant.parse("2002-01-01T00:00:00Z");
		producer.send(gateway, timestamp, true);
		mqtt = Awaitility.await().until(() -> data.find(gateway).getStatus().getMqtt(), m -> m.getConnected());
		assertEquals(timestamp, mqtt.getTimestamp(), "reconnected timestamp");
		assertTrue(mqtt.getConnected(), "reconnected");
	}

	@DisplayName("fail: invalid content-type")
	@Test
	void failContentType() {
		var tenantId = UUID.randomUUID().toString();
		var gatewayId = UUID.randomUUID();
		var timestamp = String.valueOf(Instant.now().toEpochMilli());
		var contentType = "unkown";
		var body = UUID.randomUUID().toString();
		producer.send(tenantId, gatewayId, contentType, timestamp, body);
		// TODO check metrics
	}

	@DisplayName("fail: invalid body - not json")
	@Test
	void failInvalidBodyJson() {
		var tenantId = UUID.randomUUID().toString();
		var gatewayId = UUID.randomUUID();
		var timestamp = String.valueOf(Instant.now().toEpochMilli());
		var contentType = KafkaHeader.CONTENT_TYPE_EVENT_DC;
		var body = "{this is not a valid json";
		producer.send(tenantId, gatewayId, contentType, timestamp, body);
		// TODO check metrics
	}

	@DisplayName("fail: invalid body - no cause")
	@Test
	void failInvalidBodyCause() {
		var tenantId = UUID.randomUUID().toString();
		var gatewayId = UUID.randomUUID();
		var timestamp = String.valueOf(Instant.now().toEpochMilli());
		var contentType = KafkaHeader.CONTENT_TYPE_EVENT_DC;
		var body = "{\"remote-id\": \"nope\"}";
		producer.send(tenantId, gatewayId, contentType, timestamp, body);
		// TODO check metrics
	}

	@DisplayName("fail: gateway not exists")
	@Test
	void failInvalidGatewayNotExists() {
		var tenantId = UUID.randomUUID().toString();
		var gatewayId = UUID.randomUUID();
		var timestamp = String.valueOf(Instant.now().toEpochMilli());
		var contentType = KafkaHeader.CONTENT_TYPE_EVENT_DC;
		var body = "{\"cause\": \"connected\"}";
		producer.send(tenantId, gatewayId, contentType, timestamp, body);
		// TODO check metrics
	}
}
