package io.inoa.cloud.service;

import java.util.regex.Pattern;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

import io.inoa.cloud.event.CloudEventVO;
import io.inoa.cloud.log.JSONLogVO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.validation.validator.Validator;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener to receive log events from hono and put them to the log backend.
 *
 * @author Fabian Schlegel
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST)
@Slf4j
public class LogEventListener {

	private static final String PATTERN_TENANT_ID = "^[a-z0-9\\-]{4,30}$";
	private static final String PATTERN_UUID = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";
	private static final String LOG_CLOUD_EVENT_TYPE = "io.inoa.log.emitted";

	private final Validator validator;
	private final ObjectMapper objectMapper;
	private final LogMetrics metrics;

	public LogEventListener(
			Validator validator,
			ObjectMapper objectMapper,
			LogMetrics metrics) {
		this.validator = validator;
		this.objectMapper = objectMapper;
		this.metrics = metrics;
	}

	@Topic(patterns = "hono\\.event\\..*")
	void receive(ConsumerRecord<String, String> record) {

		var tenantId = record.topic().substring(11);
		var key = record.key();
		var payload = record.value();

		try {

			MDC.put("tenantId", tenantId);
			MDC.put("gatewayId", key);

			// get tenantId and gatewayId from message record

			if (!Pattern.matches(PATTERN_TENANT_ID, tenantId)) {
				log.warn("Got record with unparseable topic: {}", key);
				metrics.counterFailTenantId(tenantId).increment();
				return;
			}

			if (!Pattern.matches(PATTERN_UUID, key)) {
				log.warn("Got record with unparseable key: {}", key);
				metrics.counterFailGatewayId(tenantId).increment();
				return;
			}

			// parse payload
			CloudEventVO event;
			try {
				event = objectMapper.readValue(payload, CloudEventVO.class);
			} catch (JsonProcessingException e) {
				log.warn("Retrieved invalid payload: {}", payload);
				metrics.counterFailMessageRead(tenantId).increment();
				return;
			}

			// validate payload
			var payloadViolations = validator.validate(event);
			if (!payloadViolations.isEmpty()) {
				log.warn("Retrieved invalid payload: {}", payload);
				if (log.isDebugEnabled()) {
					payloadViolations.forEach(v -> log.debug("Violation: {} {}", v.getPropertyPath(), v.getMessage()));
				}
				metrics.counterFailMessageValidate(tenantId).increment();
				return;
			}

			// check if we are in charge
			if(!LOG_CLOUD_EVENT_TYPE.equals(event.getType())) {
				log.trace("Not in charge for event type: {}", event.getType());
				return;
			}

			// parse json log
			JSONLogVO jsonLogVO;
			try {
				jsonLogVO = objectMapper.readValue(objectMapper.writeValueAsString(event.getData()), JSONLogVO.class);
			} catch (JsonProcessingException e) {
				log.warn("Retrieved invalid JSON log: {}", event.getData());
				metrics.counterFailMessageValidate(tenantId).increment();
				return;
			}

			// validate json log
			var jsonLogViolations = validator.validate(jsonLogVO);
			if (!jsonLogViolations.isEmpty()) {
				log.warn("Retrieved invalid payload: {}", event.getData());
				if (log.isDebugEnabled()) {
					jsonLogViolations.forEach(v -> log.debug("Violation: {} {}", v.getPropertyPath(), v.getMessage()));
				}
				metrics.counterFailMessageValidate(tenantId).increment();
				return;
			}

			// Actual log
			log(jsonLogVO);
			metrics.counterSuccess(tenantId).increment();

		} finally {
			MDC.clear();
		}
	}

	private void log(JSONLogVO jsonLogVO) {
		Level level;
		switch (jsonLogVO.getLevel()) {
		case 0:
			level = Level.OFF;
			break;
		case 1:
			level = Level.ERROR;
			break;
		case 2:
			level = Level.WARN;
			break;
		case 3:
			level = Level.INFO;
			break;
		case 4:
			level = Level.DEBUG;
			break;
		case 5:
			level = Level.TRACE;
			break;
		default:
			level = Level.ALL;
		}
		var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(jsonLogVO.getTag());
		var event = new LoggingEvent(logger.getName(), logger, level, jsonLogVO.getMsg(), null, null);
		event.setTimeStamp(jsonLogVO.getTime());
		logger.callAppenders(event);
	}
}
