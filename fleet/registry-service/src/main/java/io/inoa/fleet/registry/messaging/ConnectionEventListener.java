package io.inoa.fleet.registry.messaging;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.registry.KafkaHeader;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener to receive log events abount gateway connection events.
 *
 * @author Rico Pahlisch
 * @author Stephan Schnabel
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST, redelivery = true)
@Slf4j
@RequiredArgsConstructor
public class ConnectionEventListener {

	private final GatewayRepository repository;
	private final ObjectMapper mapper;

	@Topic(patterns = "hono\\.event\\..*")
	void handle(
			@MessageHeader(KafkaHeader.TENANT_ID) String tenantId,
			@MessageHeader(KafkaHeader.DEVICE_ID) UUID gatewayId,
			@MessageHeader(KafkaHeader.CONTENT_TYPE) String contentType,
			@MessageHeader(KafkaHeader.CREATION_TIME) Long timestampMillis,
			String payload) {

		if (!KafkaHeader.CONTENT_TYPE_EVENT_DC.equals(contentType)) {
			return;
		}

		try {
			MDC.put("tenantId", tenantId);
			MDC.put("gatewayId", gatewayId.toString());

			Map<String, Object> data;
			try {
				data = mapper.readValue(payload, Map.class);
			} catch (JsonProcessingException e) {
				log.warn("Gateway {}/{} send invalid connection payload: {}", tenantId, gatewayId, payload, e);
				return;
			}

			if (!data.containsKey("cause")) {
				log.warn("Gateway {}/{} send invalid connection payload: {}", tenantId, gatewayId, payload);
				return;
			}

			if (repository.findByGatewayId(gatewayId).isEmpty()) {
				log.warn("Gateway {}/{} not found", tenantId, gatewayId);
				return;
			}

			var timestamp = Instant.ofEpochMilli(timestampMillis);
			var connected = data.get("cause").equals("connected");
			repository.updateStatusMqtt(gatewayId, timestamp, connected);
			log.debug("Gateway {}/{} {}connected at {}", tenantId, gatewayId, connected ? "" : "dis", timestamp);

		} finally {
			MDC.remove("tenantId");
			MDC.remove("gatewayId");
		}
	}
}
