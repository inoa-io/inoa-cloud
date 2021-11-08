package io.inoa.cloud.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;

/**
 * Test for {@link LogEventListener}.
 *
 * @author Fabian Schlegel
 */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogEventListenerTest implements TestPropertyProvider {

	@Inject
	Producer<String, String> producer;
	@Inject
	LogMetrics metrics;

	private static List<ILoggingEvent> logsGateway = new ArrayList<>();
	private static List<ILoggingEvent> logsListener = new ArrayList<>();

	@BeforeAll
	static void setUpListAppender() {

		Awaitility.setDefaultTimeout(Duration.ofSeconds(10));
		Awaitility.setDefaultPollInterval(Duration.ofMillis(100));

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

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var countSuccessBefore = metrics.counterSuccess(tenantId).count();

		// send message
		send(tenantId, gatewayId, "{"
				+ "    \"specversion\" : \"1.0\","
				+ "    \"type\" : \"io.inoa.log.emitted\","
				+ "    \"source\" : \"869a39c8-44ec-4155-9175-1dee6e846978\","
				+ "    \"id\" : \"123456789\","
				+ "    \"time\" : \"2018-04-05T17:31:00Z\","
				+ "    \"datacontenttype\" : \"application/json\","
				+ "    \"data\" : "
				+ "      { "
				+ "        \"tag\": \"METERING\","
				+ "        \"level\": 3,"
				+ "        \"time\": 123456789,"
				+ "        \"msg\": \"OK\""
				+ "      }"
				+ "}");

		Awaitility
				.await("Successful Log")
				.until(() -> metrics.counterSuccess(tenantId).count(), count -> count != countSuccessBefore);

		// validate translated message

		assertEquals(0, logsListener.size(), "listener logs");
		assertEquals(1, logsGateway.size(), "gateway logs");
		var logEvent = logsGateway.get(0);
		assertAll("log event",
				() -> assertEquals("OK", logEvent.getFormattedMessage(), "message"),
				() -> assertEquals(Level.INFO, logEvent.getLevel(), "level"),
				() -> assertEquals("METERING", logEvent.getLoggerName(), "logger"),
				() -> assertEquals(123456789, logEvent.getTimeStamp(), "timestamp"),
				() -> assertEquals(tenantId, logEvent.getMDCPropertyMap().get("tenantId"), "mdc tenant"),
				() -> assertEquals(gatewayId.toString(), logEvent.getMDCPropertyMap().get("gatewayId"), "mdc gateway"));
	}

	@DisplayName("ignore event")
	@Test
	void ignore() {

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var countSuccessBefore = metrics.counterSuccess(tenantId).count();
		var countIgnoreBefore = metrics.counterIgnore(tenantId).count();

		// send message
		send(tenantId, gatewayId, "{"
				+ "    \"specversion\" : \"1.0\","
				+ "    \"type\" : \"wurst\","
				+ "    \"source\" : \"869a39c8-44ec-4155-9175-1dee6e846978\","
				+ "    \"id\" : \"123456789\","
				+ "    \"time\" : \"2018-04-05T17:31:00Z\","
				+ "    \"datacontenttype\" : \"application/json\","
				+ "    \"data\" : {}"
				+ "}");

		Awaitility
				.await("ignore log statement")
				.until(() -> metrics.counterIgnore(tenantId).count(), count -> count == countIgnoreBefore + 1);
		assertEquals(countSuccessBefore, metrics.counterSuccess(tenantId).count(), "success metrics increased");

		// validate logs

		assertEquals(0, logsGateway.size(), "gateway logs");
		assertEquals(1, logsListener.size(), "listener logs");
		var logEvent = logsListener.get(0);
		assertAll("log event",
				() -> assertEquals("Not in charge for event type: wurst", logEvent.getFormattedMessage(), "message"),
				() -> assertEquals(Level.TRACE, logEvent.getLevel(), "level"),
				() -> assertEquals(tenantId, logEvent.getMDCPropertyMap().get("tenantId"), "mdc tenant"),
				() -> assertEquals(gatewayId.toString(), logEvent.getMDCPropertyMap().get("gatewayId"), "mdc gateway"));
	}

	private void send(String tenantId, Object gatewayId, String payload) {
		var future = producer.send(new ProducerRecord<>("hono.event." + tenantId, gatewayId.toString(), payload));
		Awaitility.await("message send").until(() -> future.isDone());
	}

	@Override
	public Map<String, String> getProperties() {
		var kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.2"));
		kafka.start();
		return Map.of(
				"kafka.bootstrap.servers", kafka.getBootstrapServers(),
				"kafka.consumers.default.metadata.max.age.ms", "200");
	}
}
