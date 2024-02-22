package io.inoa.fleet.registry.messaging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.AbstractUnitTest;
import io.inoa.fleet.registry.KafkaHeader;
import io.inoa.fleet.registry.domain.Gateway;
import jakarta.inject.Inject;

@DisplayName("kafka: Connection Event")
public class ConnectionEventListenerTest extends AbstractUnitTest {

	@Inject
	ConnectionEventListener listener;

	@DisplayName("connect, disconnect and reconnect again")
	@Test
	void connectDisconnectReconnect() {

		var gateway = data.gateway();
		assertNull(gateway.getStatus().getMqtt().getTimestamp(), "initial timestamp");
		assertFalse(gateway.getStatus().getMqtt().getConnected(), "initial connected");

		var timestamp = Instant.parse("2000-01-01T00:00:00Z");
		handle(gateway, timestamp, true);
		var mqtt = data.find(gateway).getStatus().getMqtt();
		assertEquals(timestamp, mqtt.getTimestamp(), "connected timestamp");
		assertTrue(mqtt.getConnected(), "connected");

		timestamp = Instant.parse("2001-01-01T00:00:00Z");
		handle(gateway, timestamp, false);
		mqtt = data.find(gateway).getStatus().getMqtt();
		assertEquals(timestamp, mqtt.getTimestamp(), "disconnected timestamp");
		assertFalse(mqtt.getConnected(), "disconnected");

		timestamp = Instant.parse("2002-01-01T00:00:00Z");
		handle(gateway, timestamp, true);
		mqtt = data.find(gateway).getStatus().getMqtt();
		assertEquals(timestamp, mqtt.getTimestamp(), "reconnected timestamp");
		assertTrue(mqtt.getConnected(), "reconnected");
	}

	@DisplayName("fail: invalid content-type")
	@Test
	void failContentType() {
		var tenantId = data.tenantName();
		var gatewayId = data.gatewayId();
		var timestamp = Instant.now().toEpochMilli();
		var contentType = "unkown";
		var body = UUID.randomUUID().toString();
		listener.handle(tenantId, gatewayId, contentType, null, timestamp, body);
		// TODO check metrics
	}

	@DisplayName("fail: invalid body - not json")
	@Test
	void failInvalidBodyJson() {
		var tenantId = data.tenantName();
		var gatewayId = data.gatewayId();
		var timestamp = Instant.now().toEpochMilli();
		var contentType = KafkaHeader.CONTENT_TYPE_EVENT_DC;
		var body = "{this is not a valid json";
		listener.handle(tenantId, gatewayId, contentType, null, timestamp, body);
		// TODO check metrics
	}

	@DisplayName("fail: invalid body - no cause")
	@Test
	void failInvalidBodyCause() {
		var tenantId = data.tenantName();
		var gatewayId = data.gatewayId();
		var timestamp = Instant.now().toEpochMilli();
		var contentType = KafkaHeader.CONTENT_TYPE_EVENT_DC;
		var body = "{\"remote-id\": \"nope\"}";
		listener.handle(tenantId, gatewayId, contentType, null, timestamp, body);
		// TODO check metrics
	}

	@DisplayName("fail: gateway not exists")
	@Test
	void failInvalidGatewayNotExists() {
		var tenantId = data.tenantName();
		var gatewayId = data.gatewayId();
		var timestamp = Instant.now().toEpochMilli();
		var contentType = KafkaHeader.CONTENT_TYPE_EVENT_DC;
		var body = "{\"cause\": \"connected\"}";
		listener.handle(tenantId, gatewayId, contentType, null, timestamp, body);
		// TODO check metrics
	}

	private void handle(Gateway gateway, Instant timestamp, boolean connected) {
		listener.handle(
				gateway.getTenant().getTenantId(),
				gateway.getGatewayId(),
				KafkaHeader.CONTENT_TYPE_EVENT_DC,
				null,
				timestamp.toEpochMilli(),
				assertDoesNotThrow(() -> mapper.writeValueAsString(Map.of(
						"cause", connected ? "connected" : "disconnected",
						"remote-id", UUID.randomUUID().toString(),
						"source", "inoa-mqtt"))));
	}
}
