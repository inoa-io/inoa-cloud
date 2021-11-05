package io.inoa.cloud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

	@DisplayName("send event to log")
	@Test
	void success() {

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
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
				+ "        \"level\": 0,\n"
				+ "        \"time\": 123456789,\n"
				+ "        \"msg\": \"OK\"\n"
				+ "      }\n"
				+ "}");

		Awaitility.await("counter change")
				.pollInterval(Duration.ofMillis(50))
				.timeout(Duration.ofSeconds(120))
				.until(() -> counter.count() != countBefore);

		// validate translated message
		assertEquals(countBefore + 1, counter.count(), "increment");
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
