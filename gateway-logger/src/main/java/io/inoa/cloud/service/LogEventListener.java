package io.inoa.cloud.service;

import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import io.inoa.cloud.event.CloudEventVO;
import io.inoa.cloud.log.JSONLogVO;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.validation.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener to receive log events from hono and put them to the log backend.
 *
 * @author Fabian Schlegel
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST)
@Slf4j
@RequiredArgsConstructor
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
		var gatewayId = record.key();
		var payload = record.value();

		try {

			MDC.put("tenantId", tenantId);
			MDC.put("gatewayId", gatewayId);

			// get tenantId and gatewayId from message record

			if (!Pattern.matches(PATTERN_TENANT_ID, tenantId)) {
				log.warn("Got record with unparseable topic: {}", tenantId);
				metrics.counterFailTenantId(tenantId).increment();
				return;
			}

			if (!Pattern.matches(PATTERN_UUID, gatewayId)) {
				log.warn("Got record with unparseable key: {}", gatewayId);
				metrics.counterFailGatewayId(tenantId).increment();
				return;
			}

			// parse payload

			var eventOptional = parse(tenantId, CloudEventVO.class, payload);
			if (eventOptional.isEmpty()) {
				return;
			}
			var event = eventOptional.get();

			// check if we are in charge

			if (!LOG_CLOUD_EVENT_TYPE.equals(event.getType())) {
				log.trace("Not in charge for event type: {}", event.getType());
				metrics.counterIgnore(tenantId).increment();
				return;
			}

			// parse json log

			var jsonLog = parse(tenantId, JSONLogVO.class, event.getData());
			if (jsonLog.isEmpty()) {
				return;
			}

			// Actual log

			log(jsonLog.get());
			metrics.counterSuccess(tenantId).increment();

		} finally {
			MDC.clear();
		}
	}

	private <T> Optional<T> parse(String tenantId, Class<T> type, Object payload) {

		// parse json log

		T result = null;
		try {
			if (payload instanceof String) {
				result = objectMapper.readValue(payload.toString(), type);
			} else {
				result = objectMapper.readValue(objectMapper.writeValueAsString(payload), type);
			}
		} catch (JsonProcessingException e) {
			log.warn("Retrieved invalid {} payload: {}", type.getSimpleName(), payload);
			metrics.counterFailMessageValidate(tenantId).increment();
			return Optional.empty();
		}

		// validate json log

		var violations = validator.validate(result);
		if (!violations.isEmpty()) {
			log.warn("Retrieved invalid {} payload: {}", type.getSimpleName(), payload);
			if (log.isDebugEnabled()) {
				violations.forEach(v -> log.debug("Violation: {} {}", v.getPropertyPath(), v.getMessage()));
			}
			metrics.counterFailMessageValidate(tenantId).increment();
			return Optional.empty();
		}

		return Optional.of(result);
	}

	private void log(JSONLogVO jsonLog) {
		var logger = (Logger) LoggerFactory.getLogger(jsonLog.getTag());
		var event = new LoggingEvent(logger.getName(), logger, getLogLevel(jsonLog), jsonLog.getMsg(), null, null);
		event.setTimeStamp(jsonLog.getTime());
		logger.callAppenders(event);
	}

	private Level getLogLevel(JSONLogVO jsonLog) {
		switch (jsonLog.getLevel()) {
			case 0:
				return Level.OFF;
			case 1:
				return Level.ERROR;
			case 2:
				return Level.WARN;
			case 3:
				return Level.INFO;
			case 4:
				return Level.DEBUG;
			case 5:
				return Level.TRACE;
			default:
				return Level.ALL;
		}
	}
}
