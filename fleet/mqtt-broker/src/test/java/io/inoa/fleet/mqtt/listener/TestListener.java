package io.inoa.fleet.mqtt.listener;

import static io.inoa.fleet.mqtt.AbstractMqttTest.assertHeader;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.mqtt.MqttHeader;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Property;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@KafkaListener(
		groupId = "test",
		offsetReset = OffsetReset.EARLIEST,
		properties = @Property(name = ConsumerConfig.METADATA_MAX_AGE_CONFIG, value = "200"))
@Slf4j
@Getter
public class TestListener {

	private final List<ConsumerRecord<UUID, byte[]>> records = new ArrayList<>();
	private final ObjectMapper mapper = new ObjectMapper();

	public void clear() {
		records.clear();
	}

	public ConsumerRecord<UUID, byte[]> await(String tenantId, UUID gatewayId) {
		Awaitility
				.await()
				.pollDelay(500, TimeUnit.MILLISECONDS)
				.until(() -> !records.stream()
						.allMatch(r -> r.topic().endsWith("." + tenantId) && r.key() == gatewayId));
		assertEquals(1, records.size(), "expected only one entry");
		var record = records.get(0);
		records.remove(record);
		return record;
	}

	@SneakyThrows
	public void awaitConnection(String tenantId, UUID gatewayId, boolean connected) {
		var record = await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(gatewayId, record.key(), "key");
		assertEquals(
				connected ? "connected" : "disconnected",
				mapper.readValue(record.value(), Map.class).get("cause"),
				"cause");
		assertHeader(record, MqttHeader.CONTENT_TYPE, MqttHeader.CONTENT_TYPE_EVENT_DC);
	}

	@Topic(patterns = ".*")
	public void receive(ConsumerRecord<UUID, byte[]> record) {
		log.info("Retrieved record from {} with key: {}", record.topic(), record.key(), record.value());
		records.add(record);
	}
}
