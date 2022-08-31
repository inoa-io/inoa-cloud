package io.inoa.fleet.registry.messaging;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.registry.domain.ConnectionStatus;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener to receive log events from hono and put them to the log
 * backend.
 *
 * @author Rico Pahlisch
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST, redelivery = true)
@Slf4j
@RequiredArgsConstructor
public class EventListener {

	private final GatewayRepository gatewayRepository;

	private final ObjectMapper objectMapper;

	@Topic(patterns = "hono\\.event\\..*")
	void handle(@MessageHeader("device_id") UUID gatewayId, @MessageHeader("tenant_id") String tenantId,
			@MessageHeader("content-type") String contentType, ConsumerRecord<String, String> record) {
		if (!contentType.equals("application/vnd.eclipse-hono-dc-notification+json")) {
			return;
		}
		try {
			Map<String, String> body = objectMapper.readValue(record.value(), new TypeReference<>() {
			});
			if (body.get("cause") != null) {
				Optional<Gateway> optionalGateway = gatewayRepository.findByGatewayId(gatewayId);
				if (optionalGateway.isPresent()) {
					try {
						MDC.put("tenantId", tenantId);
						MDC.put("gatewayId", gatewayId.toString());
						if (body.get("cause").equals("connected")) {
							optionalGateway.get().setConnectionStatus(
									new ConnectionStatus().setTimestamp(Instant.now()).setConnected(true));
						} else {
							optionalGateway.get().setConnectionStatus(
									new ConnectionStatus().setTimestamp(Instant.now()).setConnected(false));
						}
						gatewayRepository.update(optionalGateway.get());
					} finally {
						MDC.clear();
					}
				} else {
					log.warn("no gateway found with uuid {}", gatewayId);
				}
			}
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
		}
	}
}
