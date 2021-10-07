package io.inoa.cloud.service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.validation.validator.Validator;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener to translate messages from hono to messages in inoa.
 *
 * @author Stephan Schnabel
 */
@KafkaListener(clientId = "translator", groupId = "translator", offsetReset = OffsetReset.EARLIEST)
@Slf4j
public class TranslateMessageListener {

	private static final String PATTERN_UUID = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";

	private final Validator validator;
	private final ObjectMapper objectMapper;
	private final TranslateService service;
	private final TranslateMetrics metrics;
	private final KafkaProducer<UUID, InoaTelemetryMessageVO> producer;

	public TranslateMessageListener(
			Validator validator,
			ObjectMapper objectMapper,
			TranslateService service,
			TranslateMetrics metrics,
			@KafkaClient("inoa-producer") KafkaProducer<UUID, InoaTelemetryMessageVO> producer) {
		this.validator = validator;
		this.objectMapper = objectMapper;
		this.service = service;
		this.metrics = metrics;
		this.producer = producer;
	}

	@Topic(patterns = "hono\\.telemetry\\..*")
	void receive(ConsumerRecord<String, String> record) {

		var key = record.key();
		var topic = record.topic();
		var payload = record.value();

		try {

			MDC.put("tenantId", topic);
			MDC.put("gatewayId", key);

			// get tenantId and gatewayId from message record

			var tenantIdString = topic.substring(15);
			MDC.put("tenantId", tenantIdString);
			if (!Pattern.matches(PATTERN_UUID, tenantIdString)) {
				log.warn("Got record with unparseable topic: {}", key);
				metrics.counterFailTenantId(tenantIdString).increment();
				return;
			}
			var tenantId = UUID.fromString(tenantIdString);

			if (!Pattern.matches(PATTERN_UUID, key)) {
				log.warn("Got record with unparseable key: {}", key);
				metrics.counterFailGatewayId(tenantId).increment();
				return;
			}
			var gatewayId = UUID.fromString(key);

			// parse payload, convert and publish

			var inoaOptional = toHonoMessage(tenantId, payload).map(hono -> service.toInoa(tenantId, gatewayId, hono));
			if (inoaOptional.isEmpty() || inoaOptional.get().isEmpty()) {
				metrics.counterIgnore(tenantId).increment();
			} else {
				for (var inoa : inoaOptional.get()) {
					producer.send(new ProducerRecord<>("inoa.telemetry." + tenantId, inoa.getGatewayId(), inoa));
					metrics.counterSuccess(tenantId, inoa.getDeviceType(), inoa.getSensor()).increment();
				}
			}

		} finally {
			MDC.clear();
		}
	}

	private Optional<HonoTelemetryMessageVO> toHonoMessage(UUID tenantId, String payload) {

		HonoTelemetryMessageVO message = null;
		try {
			message = objectMapper.readValue(payload, HonoTelemetryMessageVO.class);
		} catch (JsonProcessingException e) {
			log.warn("Retrieved invalid payload: {}", payload);
			metrics.counterFailMessageRead(tenantId).increment();
			return Optional.empty();
		}

		var violations = validator.validate(message);
		if (!violations.isEmpty()) {
			log.warn("Retrieved invalid payload: {}", payload);
			if (log.isDebugEnabled()) {
				violations.forEach(v -> log.debug("Violation: {} {}", v.getPropertyPath(), v.getMessage()));
			}
			metrics.counterFailMessageValidate(tenantId).increment();
			return Optional.empty();
		}

		return Optional.of(message);
	}
}
