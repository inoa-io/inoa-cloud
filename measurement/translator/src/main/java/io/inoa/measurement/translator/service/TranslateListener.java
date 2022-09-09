package io.inoa.measurement.translator.service;

import java.util.Optional;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.telemetry.TelemetryVO;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.validation.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
@Slf4j
@RequiredArgsConstructor
public class TranslateListener {

	private final Validator validator;
	private final ObjectMapper mapper;
	private final TranslateService service;
	private final TranslateMetrics metrics;
	private final Producer<String, TelemetryVO> producer;

	@Topic(patterns = "hono\\.telemetry\\..*")
	void receive(ConsumerRecord<String, String> record) {

		var tenantId = record.topic().substring(15);
		var gatewayId = record.key();
		var payload = record.value();

		try {

			MDC.put("tenantId", tenantId);
			MDC.put("gatewayId", gatewayId);

			// parse payload, convert and publish

			var telemetryOptional = toRaw(tenantId, payload).map(raw -> service.translate(tenantId, gatewayId, raw));
			if (telemetryOptional.isEmpty() || telemetryOptional.get().isEmpty()) {
				metrics.counterIgnore(tenantId).increment();
				return;
			}
			for (var telemetry : telemetryOptional.get()) {
				producer.send(new ProducerRecord<>("inoa.telemetry." + tenantId, telemetry.getGatewayId(), telemetry));
				metrics.counterSuccess(tenantId, telemetry.getDeviceType(), telemetry.getSensor()).increment();
			}

		} finally {
			MDC.clear();
		}
	}

	private Optional<TelemetryRawVO> toRaw(String tenantId, String payload) {

		TelemetryRawVO message = null;
		try {
			message = mapper.readValue(payload, TelemetryRawVO.class);
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
