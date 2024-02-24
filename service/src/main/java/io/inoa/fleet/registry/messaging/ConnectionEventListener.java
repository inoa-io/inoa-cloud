package io.inoa.fleet.registry.messaging;

import java.time.Instant;
import java.util.Map;

import org.slf4j.MDC;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.registry.KafkaHeader;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.messaging.annotation.MessageBody;
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
	void handle(@MessageHeader(KafkaHeader.TENANT_ID) String tenantId,
			@MessageHeader(KafkaHeader.DEVICE_ID) String gatewayId,
			@MessageHeader(KafkaHeader.CONTENT_TYPE) String contentType,
			@MessageHeader(KafkaHeader.CONTENT_TTD) @Nullable Integer ttd,
			@MessageHeader(KafkaHeader.CREATION_TIME) @Nullable Long timestampMillis,
			@MessageBody @Nullable String payload) {

		MDC.put("tenantId", tenantId);
		MDC.put("gatewayId", gatewayId);
		try {

			if (KafkaHeader.CONTENT_TYPE_EMPTY_NOTIFICATION.equals(contentType)) {
				handleOldEventType(tenantId, gatewayId, ttd, timestampMillis);
			}
			if (KafkaHeader.CONTENT_TYPE_EVENT_DC.equals(contentType)) {
				handleNewEventType(tenantId, gatewayId, timestampMillis, payload);
			}
		} finally {
			MDC.remove("tenantId");
			MDC.remove("gatewayId");
		}
	}

	private void handleOldEventType(String tenantId, String gatewayId, Integer ttd, Long timestampMillis) {
		if (repository.findByGatewayId(gatewayId).isEmpty()) {
			log.warn("Gateway {}/{} not found", tenantId, gatewayId);
			return;
		}
		var timestamp = timestampMillis == null ? Instant.now() : Instant.ofEpochMilli(timestampMillis);
		repository.updateStatusMqtt(gatewayId, timestamp, ttd == -1);
		log.info("Gateway {}/{} {}connected at {}", tenantId, gatewayId, ttd == -1 ? "" : "dis", timestamp);
	}

	private void handleNewEventType(String tenantId, String gatewayId, Long timestampMillis, String payload) {

		Map<String, Object> data;
		try {
			data = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			log.warn("Gateway {}/{} send invalid connection payload:", tenantId, gatewayId, e);
			return;
		}

		if (!data.containsKey("cause")) {
			log.warn("Gateway {}/{} send invalid connection payload:", tenantId, gatewayId);
			return;
		}

		if (repository.findByGatewayId(gatewayId).isEmpty()) {
			log.warn("Gateway {}/{} not found", tenantId, gatewayId);
			return;
		}

		var timestamp = timestampMillis == null ? Instant.now() : Instant.ofEpochMilli(timestampMillis);
		var connected = "connected".equals(data.get("cause"));
		repository.updateStatusMqtt(gatewayId, timestamp, connected);
		log.info("Gateway {}/{} {}connected at {}", tenantId, gatewayId, connected ? "" : "dis", timestamp);

	}
}
