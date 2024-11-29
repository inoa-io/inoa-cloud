package io.inoa.fleet.logging;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.inoa.rest.CloudEventTypeVO;
import io.inoa.test.AbstractUnitTest;
import io.micronaut.http.MediaType;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Test for {@link LogEventListener}.
 *
 * @author Fabian Schlegel
 */
public class LogEventListenerTest extends AbstractUnitTest {

  private static String tenantId = "inoa";
  private static String gatewayId = "GW-0001";
  private static List<ILoggingEvent> logsGateway = new ArrayList<>();
  private static List<ILoggingEvent> logsListener = new ArrayList<>();

  @Inject LogEventListener listener;
  @Inject LogMetrics metrics;

  @BeforeAll
  static void setUpListAppender() {

    var log = (Logger) LoggerFactory.getLogger("METERING");
    if (log.getAppender("test") == null) {
      var appender = new ListAppender<ILoggingEvent>();
      appender.setName("test");
      appender.start();
      appender.list = logsGateway;
      log.addAppender(appender);
    }

    log = (Logger) LoggerFactory.getLogger(LogEventListener.class);
    if (log.getAppender("test") == null) {
      var appender = new ListAppender<ILoggingEvent>();
      appender.setName("test");
      appender.start();
      appender.list = logsListener;
      log.addAppender(appender);
    }
  }

  @BeforeEach
  @AfterEach
  void setUpMdc() {
    logsGateway.clear();
    logsListener.clear();
    MDC.clear();
  }

  @DisplayName("send event to log")
  @Test
  void success() {

    // send message

    var countSuccessBefore = metrics.counterSuccess(tenantId).count();
    var countIgnoreBefore = metrics.counterIgnore(tenantId).count();
    var counterFailMessageReadBefore = metrics.counterFailMessageRead(tenantId).count();
    send(
        CloudEventTypeVO.LOG_EMITTED_VALUE,
        tenantId,
        gatewayId,
        Map.of("tag", "METERING", "level", 3, "time", 123456789, "msg", "OK"));
    assertEquals(
        countSuccessBefore + 1, metrics.counterSuccess(tenantId).count(), "metrics success");
    assertEquals(countIgnoreBefore, metrics.counterIgnore(tenantId).count(), "metrics ignore");
    assertEquals(
        counterFailMessageReadBefore,
        metrics.counterFailMessageRead(tenantId).count(),
        "metrics fail");

    // validate translated message

    assertEquals(0, logsListener.size(), "listener logs");
    assertEquals(1, logsGateway.size(), "gateway logs");
    var logEvent = logsGateway.get(0);
    assertAll(
        "log event",
        () -> assertEquals("OK", logEvent.getFormattedMessage(), "message"),
        () -> assertEquals(Level.INFO, logEvent.getLevel(), "level"),
        () -> assertEquals("METERING", logEvent.getLoggerName(), "logger"),
        () -> assertEquals(123456789, logEvent.getTimeStamp(), "timestamp"),
        () -> assertEquals(tenantId, logEvent.getMDCPropertyMap().get("tenantId"), "mdc tenant"),
        () ->
            assertEquals(gatewayId, logEvent.getMDCPropertyMap().get("gatewayId"), "mdc gateway"));
  }

  @DisplayName("ignore event")
  @Test
  void ignore() {

    // send message

    var countSuccessBefore = metrics.counterSuccess(tenantId).count();
    var countIgnoreBefore = metrics.counterIgnore(tenantId).count();
    var counterFailMessageReadBefore = metrics.counterFailMessageRead(tenantId).count();
    send("wurst", tenantId, gatewayId, "{}");
    assertEquals(countSuccessBefore, metrics.counterSuccess(tenantId).count(), "metrics success");
    assertEquals(countIgnoreBefore + 1, metrics.counterIgnore(tenantId).count(), "metrics ignore");
    assertEquals(
        counterFailMessageReadBefore,
        metrics.counterFailMessageRead(tenantId).count(),
        "metrics fail");

    // validate logs

    assertEquals(0, logsGateway.size(), "gateway logs");
    assertEquals(1, logsListener.size(), "listener logs");
    var logEvent = logsListener.get(0);
    assertAll(
        "log event",
        () ->
            assertEquals(
                "Not in charge for event type: wurst", logEvent.getFormattedMessage(), "message"),
        () -> assertEquals(Level.TRACE, logEvent.getLevel(), "level"),
        () -> assertEquals(tenantId, logEvent.getMDCPropertyMap().get("tenantId"), "mdc tenant"),
        () ->
            assertEquals(gatewayId, logEvent.getMDCPropertyMap().get("gatewayId"), "mdc gateway"));
  }

  @DisplayName("illegal payload event")
  @Test
  void illegalPayload() {

    // send message

    var countSuccessBefore = metrics.counterSuccess(tenantId).count();
    var countIgnoreBefore = metrics.counterIgnore(tenantId).count();
    var counterFailMessageReadBefore = metrics.counterFailMessageRead(tenantId).count();
    send(CloudEventTypeVO.LOG_EMITTED_VALUE, tenantId, gatewayId, List.of("nope"));
    assertEquals(
        countSuccessBefore, metrics.counterSuccess(tenantId).count(), "success metrics increased");
    assertEquals(countIgnoreBefore, metrics.counterIgnore(tenantId).count(), "metrics ignore");
    assertEquals(
        counterFailMessageReadBefore + 1,
        metrics.counterFailMessageRead(tenantId).count(),
        "metrics fail");

    // validate logs

    assertEquals(0, logsGateway.size(), "gateway logs");
    assertEquals(2, logsListener.size(), "listener logs");
  }

  private void send(String type, String tenantId, String gatewayId, Object payload) {
    listener.handle(
        new ConsumerRecord<>(
            "hono.event." + tenantId,
            0,
            0,
            gatewayId,
            CloudEventBuilder.v1()
                .withSource(URI.create("test"))
                .withId("test")
                .withType(type)
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withData(PojoCloudEventData.wrap(payload, mapper::writeValueAsBytes))
                .build()));
  }
}
