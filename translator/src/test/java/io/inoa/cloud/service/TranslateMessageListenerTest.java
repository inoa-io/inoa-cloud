package io.inoa.cloud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.micrometer.core.instrument.Counter;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import lombok.Getter;

/**
 * Test for {@link TranslateMessageListener}.
 *
 * @author Stephan Schnabel
 */
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TranslateMessageListenerTest implements TestPropertyProvider {

	@Inject
	@KafkaClient("hono-producer")
	Producer<String, String> producer;
	@Inject
	TestListener testListener;
	@Inject
	TranslateMetrics metrics;

	@DisplayName("send message to translate")
	@Test
	void success() {

		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var deviceType = "dvh4013";
		var deviceId = UUID.randomUUID().toString();
		var sensor = UUID.randomUUID().toString();
		var urn = "urn:" + deviceType + ":" + deviceId + ":" + sensor;
		var timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		var counter = metrics.counterSuccess(tenantId, deviceType, sensor);
		var countBefore = counter.count();

		// send message

		send(tenantId, gatewayId,
				"{\"urn\":\"" + urn + "\",\"timestamp\":" + timestamp.toEpochMilli() + ",\"value\":\"Mg==\"}");

		// validate translated message

		Awaitility.await("retrieve inoa message")
				.pollInterval(Duration.ofMillis(50))
				.timeout(Duration.ofSeconds(5))
				.until(() -> !testListener.getRecords().isEmpty());
		var actual = testListener.getRecords().get(0).value();
		var expected = new InoaTelemetryMessageVO()
				.setTenantId(tenantId)
				.setGatewayId(gatewayId)
				.setUrn(urn)
				.setDeviceType(deviceType)
				.setDeviceId(deviceId)
				.setSensor(sensor)
				.setTimestamp(timestamp)
				.setValue(2D);
		assertEquals(expected, actual, "inoa message");
		assertEquals(countBefore + 1, counter.count(), "increment");
	}

	@DisplayName("fail: invalid tenant id")
	@Test
	void failTenantId() {

		var tenantId = "nope";
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailTenantId(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"urn:00:00:00\",\"timestamp\":0,\"value\":\"Mg==\"}");
		assertIncrement(counter, countBefore);
	}

	@DisplayName("fail: invalid gateway id")
	@Test
	void failGatewayId() {

		var tenantId = UUID.randomUUID();
		var gatewayId = "nope";
		var counter = metrics.counterFailGatewayId(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"urn:00:00:00\",\"timestamp\":0,\"value\":\"Mg==\"}");
		assertIncrement(counter, countBefore);
	}

	@DisplayName("fail: message read")
	@Test
	void failMessageRead() {

		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailMessageRead(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "i am not a valid json");
		assertIncrement(counter, countBefore);
	}

	@DisplayName("fail: message validate")
	@Test
	void failMessageValidate() {

		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailMessageValidate(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"abc\",\"timestamp\":123,\"value\":\"Mg==\"}");
		assertIncrement(counter, countBefore);
	}

	@DisplayName("fail: no converter")
	@Test
	void failConverter() {

		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailConverter(tenantId, "00", "00");
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"urn:00:00:00\",\"timestamp\":0,\"value\":\"Mg==\"}");
		assertIncrement(counter, countBefore);
	}

	@DisplayName("fail: invalid value")
	@Test
	void failValue() {

		var tenantId = UUID.randomUUID();
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailValue(tenantId, "dvh4013", "00");
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"urn:dvh4013:00:00\",\"timestamp\":0,\"value\":\"bm9wZQo=\"}");
		assertIncrement(counter, countBefore);
	}

	private void send(Object tenantId, Object gatewayId, String payload) {
		var future = producer.send(new ProducerRecord<>("hono.telemetry." + tenantId, gatewayId.toString(), payload));
		Awaitility.await("message send")
				.pollInterval(Duration.ofMillis(50))
				.timeout(Duration.ofSeconds(5))
				.until(() -> future.isDone());
	}

	private void assertIncrement(Counter counter, double before) {
		Awaitility.await("increment")
				.pollInterval(Duration.ofMillis(50))
				.timeout(Duration.ofSeconds(2))
				.until(counter::count, after -> before + 1 == after);
	}

	@Override
	public Map<String, String> getProperties() {
		var kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.2"));
		if (!kafka.isRunning()) {
			kafka.start();
		}
		return Map.of(
				"kafka.bootstrap.servers", kafka.getBootstrapServers(),
				"kafka.consumers.default.metadata.max.age.ms", "200");
	}

	@KafkaListener(groupId = "test", offsetReset = OffsetReset.EARLIEST)
	@Getter
	public static class TestListener {

		private final List<ConsumerRecord<UUID, InoaTelemetryMessageVO>> records = new ArrayList<>();

		@Topic(patterns = "^inoa\\.telemetry\\..*$")
		public void receive(ConsumerRecord<UUID, InoaTelemetryMessageVO> record) {
			records.add(record);
		}
	}

}
