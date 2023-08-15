package io.inoa.fleet.broker;

import static io.inoa.fleet.broker.AbstractMqttTest.assertHeader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.registry.KafkaHeader;
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
public class TestKafkaListener {

	private final List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
	private final ObjectMapper mapper = new ObjectMapper();

	public void clear() {
		records.clear();
	}

	public ConsumerRecord<String, byte[]> await(String tenantId, String gatewayId) {
		Awaitility.await().pollDelay(500, TimeUnit.MILLISECONDS).until(() -> !records.isEmpty());
		assertEquals(1, records.size(), "expected only one entry");
		var record = records.get(0);
		records.remove(record);
		assertEquals(gatewayId, record.key(), "gatewayId");
		assertTrue(record.topic().endsWith(tenantId), "tenantId");
		return record;
	}

	@SneakyThrows
	public void awaitConnection(String tenantId, String gatewayId, boolean connected) {
		var record = await(tenantId, gatewayId);
		assertEquals("hono.event." + tenantId, record.topic(), "topic");
		assertEquals(gatewayId, record.key(), "key");
		assertEquals(
				connected ? "connected" : "disconnected",
				mapper.readValue(record.value(), Map.class).get("cause"),
				"cause");
		assertHeader(record, KafkaHeader.CONTENT_TYPE, KafkaHeader.CONTENT_TYPE_EVENT_DC);
	}

	@Topic(patterns = ".*")
	public void receive(ConsumerRecord<String, byte[]> record) {
		log.info("Retrieved record from {} with key: {}", record.topic(), record.key(), record.value());
		records.add(record);
	}
}
