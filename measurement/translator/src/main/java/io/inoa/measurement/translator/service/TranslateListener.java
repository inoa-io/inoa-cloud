package io.inoa.measurement.translator.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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

		String tenantId = null;
		if (record.headers().lastHeader("tenant_id") != null) {
			Header deviceId = record.headers().lastHeader("tenant_id");
			tenantId = new String(deviceId.value());
		} else {
			// fallback but the header should always exist
			tenantId = record.topic().substring(15);
		}

		String gatewayId = null;

		if (record.headers().lastHeader("gatewayName") != null) {
			Header deviceId = record.headers().lastHeader("gatewayName");
			gatewayId = new String(deviceId.value());
		} else if (record.headers().lastHeader("device_id") != null) {
			Header deviceId = record.headers().lastHeader("device_id");
			gatewayId = new String(deviceId.value());
		} else {
			// fallback but the header should always exist
			gatewayId = record.key();
		}
		var payload = record.value();

		try {

			MDC.put("tenantId", tenantId);
			MDC.put("gatewayId", gatewayId);

			// parse payload, convert and publish

			String finalTenantId = tenantId;
			String finalGatewayId = gatewayId;
			var telemetryOptional = toRaw(tenantId, payload).stream()
					.map(raw -> service.translate(finalTenantId, finalGatewayId, raw)).flatMap(i -> i.stream())
					.collect(Collectors.toList());
			if (telemetryOptional.isEmpty()) {
				metrics.counterIgnore(tenantId).increment();
				return;
			}
			for (var telemetry : telemetryOptional) {
				producer.send(new ProducerRecord<>("inoa.telemetry." + tenantId, telemetry.getGatewayId(), telemetry));
				metrics.counterSuccess(tenantId, telemetry.getDeviceType(), telemetry.getSensor()).increment();
			}

		} finally {
			MDC.clear();
		}
	}

	private List<TelemetryRawVO> toRaw(String tenantId, String payload) {

		List<TelemetryRawVO> messages = new ArrayList<>();
		try {
			JsonNode node = mapper.readValue(payload, JsonNode.class);
			if (node.isArray()) {
				messages = mapper.readValue(payload, new TypeReference<>() {
					{
					}
				});
			} else {
				messages = new ArrayList<>();
				messages.add(mapper.readValue(payload, TelemetryRawVO.class));
			}
		} catch (JsonProcessingException e) {
			log.warn("Retrieved invalid payload: {}", payload);
			metrics.counterFailMessageRead(tenantId).increment();
			return messages;
		}

		List<TelemetryRawVO> toRemove = new ArrayList<>();
		for (var message : messages) {
			var violations = validator.validate(message);
			if (!violations.isEmpty()) {
				log.warn("Retrieved invalid payload: {}", payload);
				if (log.isDebugEnabled()) {
					violations.forEach(v -> log.debug("Violation: {} {}", v.getPropertyPath(), v.getMessage()));
				}
				metrics.counterFailMessageValidate(tenantId).increment();
				toRemove.add(message);
			}
		}
		for (var remove : toRemove) {
			messages.remove(remove);
		}
		return messages;
	}
}
