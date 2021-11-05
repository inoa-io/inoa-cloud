package io.inoa.cloud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import javax.inject.Inject;

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

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

/**
 * Test for {@link LogEventListener}.
 *
 * @author Fabian Schlegel
 */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LogEventListenerTest implements TestPropertyProvider {

	@Inject
	@KafkaClient("hono-producer")
	Producer<String, String> producer;
	@Inject
	LogMetrics metrics;

	private static List<ILoggingEvent> logEvents = new ArrayList<>();

	@BeforeAll
	static void setUpListAppender() {
		var log = (Logger) LoggerFactory.getLogger("METERING");
		if (log.getAppender("test") == null) {
			var appender = new ListAppender<ILoggingEvent>();
			appender.setName("test");
			appender.start();
			appender.list = logEvents;
			log.addAppender(appender);
		}
	}

	@BeforeEach
	@AfterEach
	void setUpMdc() {
		logEvents.clear();
		MDC.clear();
	}

	@DisplayName("send event to log")
	@Test
	void success() {

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterSuccess(tenantId);
		var countBefore = counter.count();

		// send message
		send(tenantId, gatewayId,"{\n"
				+ "    \"specversion\" : \"1.0\",\n"
				+ "    \"type\" : \"io.inoa.log.emitted\",\n"
				+ "    \"source\" : \"869a39c8-44ec-4155-9175-1dee6e846978\",\n"
				+ "    \"id\" : \"123456789\",\n"
				+ "    \"time\" : \"2018-04-05T17:31:00Z\",\n"
				+ "    \"datacontenttype\" : \"application/json\",\n"
				+ "    \"data\" : \n"
				+ "      { \n"
				+ "        \"tag\": \"METERING\",\n"
				+ "        \"level\": 3,\n"
				+ "        \"time\": 123456789,\n"
				+ "        \"msg\": \"OK\"\n"
				+ "      }\n"
				+ "}");

		Awaitility.await("Successful Log")
				.pollInterval(Duration.ofMillis(50))
				.timeout(Duration.ofSeconds(120))
				.until(() -> counter.count() != countBefore);

		// validate translated message
		assertEquals(1, logEvents.size(), "Correct logs");
		assertEquals("OK", logEvents.get(0).getMessage(), "Correct message");
		assertEquals(Level.INFO, logEvents.get(0).getLevel(), "Correct message");
		assertEquals("METERING", logEvents.get(0).getLoggerName(), "Correct logger name");
		assertEquals(123456789, logEvents.get(0).getTimeStamp(), "Correct timestamp");
		assertEquals(tenantId, logEvents.get(0).getMDCPropertyMap().get("tenantId"), "Correct MDCs");
		assertEquals(gatewayId.toString(), logEvents.get(0).getMDCPropertyMap().get("gatewayId"), "Correct MDCs");
	}

	private void send(String tenantId, Object gatewayId, String payload) {
		var future = producer.send(new ProducerRecord<>("hono.event." + tenantId, gatewayId.toString(), payload));
		Awaitility.await("message send")
				.pollInterval(Duration.ofMillis(50))
				.timeout(Duration.ofSeconds(5))
				.until(() -> future.isDone());
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
