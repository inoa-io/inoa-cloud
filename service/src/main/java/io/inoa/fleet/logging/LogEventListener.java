package io.inoa.fleet.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;
import io.inoa.rest.CloudEventTypeVO;
import io.inoa.rest.LogEventVO;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import io.micronaut.validation.validator.Validator;
import java.util.Base64;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Kafka listener to receive log events from hono and put them to the log backend.
 *
 * @author Fabian Schlegel
 */
@Requires(classes = CloudEventDataMapper.class)
@KafkaListener(offsetReset = OffsetReset.EARLIEST, redelivery = true)
@Slf4j
@RequiredArgsConstructor
public class LogEventListener {

  private static final Pattern PATTERN_TENANT_ID = Pattern.compile("^[a-z0-9\\-]{4,30}$");
  private static final Pattern PATTERN_UUID = Pattern.compile("^[A-Z][A-Z0-9\\-_]{3,19}$");

  private final Validator validator;
  private final ObjectMapper mapper;
  private final LogMetrics metrics;

  @Topic(patterns = "hono\\.event\\..*")
  void handle(ConsumerRecord<String, CloudEvent> record) {

    var tenantId = record.topic().substring(11);
    var gatewayId = record.key();
    var event = record.value();

    try {

      MDC.put("tenantId", tenantId);
      MDC.put("gatewayId", gatewayId);

      // get tenantId and gatewayId from message record

      if (!PATTERN_TENANT_ID.matcher(tenantId).matches()) {
        log.warn("Got record with unparseable topic: {}", tenantId);
        metrics.counterFailTenantId(tenantId).increment();
        return;
      }
      if (!PATTERN_UUID.matcher(gatewayId).matches()) {
        log.warn("Got record with unparseable key: {}", gatewayId);
        metrics.counterFailGatewayId(tenantId).increment();
        return;
      }

      // check if we are in charge

      if (!CloudEventTypeVO.LOG_EMITTED_VALUE.equals(event.getType())) {
        log.trace("Not in charge for event type: {}", event.getType());
        metrics.counterIgnore(tenantId).increment();
        return;
      }

      // parse log

      LogEventVO logEvent;
      try {
        logEvent =
            PojoCloudEventDataMapper.from(mapper, LogEventVO.class).map(event.getData()).getValue();
      } catch (CloudEventRWException e) {
        log.warn("Failed to parse json log: {}", e.getMessage());
        log.warn(
            "Invalid payload: {}", Base64.getEncoder().encodeToString(event.getData().toBytes()));
        metrics.counterFailMessageRead(tenantId).increment();
        return;
      }

      // validate log

      var violations = validator.validate(logEvent);
      if (!violations.isEmpty()) {
        log.warn("Retrieved invalid payload: {}", logEvent);
        if (log.isDebugEnabled()) {
          violations.forEach(
              v -> log.debug("Violation: {} {}", v.getPropertyPath(), v.getMessage()));
        }
        metrics.counterFailMessageValidate(tenantId).increment();
        return;
      }

      // Actual log

      log(logEvent);
      metrics.counterSuccess(tenantId).increment();

    } finally {
      MDC.clear();
    }
  }

  private void log(LogEventVO logEvent) {
    var logger = (Logger) LoggerFactory.getLogger(logEvent.getTag());
    var event =
        new LoggingEvent(
            logger.getName(), logger, getLogLevel(logEvent), logEvent.getMsg(), null, null);
    event.setTimeStamp(logEvent.getTime());
    logger.callAppenders(event);
  }

  private Level getLogLevel(LogEventVO logEvent) {
    return switch (logEvent.getLevel()) {
      case 0 -> Level.OFF;
      case 1 -> Level.ERROR;
      case 2 -> Level.WARN;
      case 3 -> Level.INFO;
      case 4 -> Level.DEBUG;
      case 5 -> Level.TRACE;
      default -> Level.ALL;
    };
  }
}
