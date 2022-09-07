package io.inoa.fleet.registry.messaging;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.registry.KafkaHeader;
import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageHeader;
import lombok.SneakyThrows;

@KafkaClient
public interface ConnectionEventProducer {

	@SneakyThrows
	default void send(Gateway gateway, Instant timestamp, boolean connected) {
		send(
				gateway.getTenant().getTenantId(),
				gateway.getGatewayId(),
				KafkaHeader.CONTENT_TYPE_EVENT_DC,
				String.valueOf(timestamp.toEpochMilli()),
				new ObjectMapper().writeValueAsString(Map.of(
						"cause", connected ? "connected" : "disconnected",
						"remote-id", UUID.randomUUID().toString(),
						"source", "inoa-mqtt")));
	}

	@Topic("hono.event.test")
	void send(
			@MessageHeader(KafkaHeader.TENANT_ID) String tenantId,
			@MessageHeader(KafkaHeader.DEVICE_ID) UUID gatewayId,
			@MessageHeader(KafkaHeader.CONTENT_TYPE) String contentType,
			@MessageHeader(KafkaHeader.CREATION_TIME) String timestampMillis,
			String body);
}
