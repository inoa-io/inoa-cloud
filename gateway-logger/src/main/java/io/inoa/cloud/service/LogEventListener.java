package io.inoa.cloud.service;

import java.util.Optional;
import java.util.regex.Pattern;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

import io.inoa.cloud.event.CloudEventVO;
import io.inoa.cloud.log.JSONLogVO;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@KafkaListener(offsetReset = OffsetReset.EARLIEST)
@Slf4j
public class LogEventListener {

	private static final String PATTERN_TENANT_ID = "^[a-z0-9\\-]{4,30}$";
	private static final String PATTERN_UUID = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$";
	private static final String LOG_CLOUD_EVENT_TYPE = "io.inoa.log.emitted";

	private final Validator validator;
	private final ObjectMapper objectMapper;
	private final LogMetrics metrics;

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
			var event = parse(CloudEventVO.class, payload, tenantId);
			if(event.isEmpty()) {
				return;
			}

			// check if we are in charge
			if(!LOG_CLOUD_EVENT_TYPE.equals(event.get().getType())) {
				log.trace("Not in charge for event type: {}", event.get().getType());
				return;
			}

			// parse json log
			var jsonLog = parse(JSONLogVO.class, event.get().getData(), tenantId);
			if(event.isEmpty()) {
				return;
			}

			// Actual log
			log(jsonLog.get());
			metrics.counterSuccess(tenantId).increment();

		} finally {
			MDC.clear();
		}
	}

	private <T> Optional<T> parse(Class<T> clazz, Object input, String tenantId) {
		// parse json log
		T result = null;
		try {
			if(input instanceof String) {
				result = objectMapper.readValue(input.toString(), clazz);
			} else {
				result = objectMapper.readValue(objectMapper.writeValueAsString(input), clazz);
			}
		} catch (JsonProcessingException e) {
			log.warn("Retrieved invalid JSON log: {}", input);
			metrics.counterFailMessageValidate(tenantId).increment();
			Optional.empty();
		}
		// validate json log
		var violations = validator.validate(result);
		if (!violations.isEmpty()) {
			log.warn("Retrieved invalid payload: {}", input);
			if (log.isDebugEnabled()) {
				violations.forEach(v -> log.debug("Violation: {} {}", v.getPropertyPath(), v.getMessage()));
			}
			metrics.counterFailMessageValidate(tenantId).increment();
			Optional.empty();
		}
		return Optional.of(result);
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
