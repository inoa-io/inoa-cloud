package io.inoa.fleet.mqtt.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Property;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@KafkaListener(
		groupId = "test",
		offsetReset = OffsetReset.EARLIEST,
		properties = @Property(name = ConsumerConfig.METADATA_MAX_AGE_CONFIG, value = "200"))
@Slf4j
@Getter
public class TestListener {

	private final List<ConsumerRecord<UUID, byte[]>> records = new ArrayList<>();

	public void clear() {
		records.clear();
	}

	public ConsumerRecord<UUID, byte[]> await() {
		Awaitility.await().until(() -> !records.isEmpty());
		return records.get(0);
	}

	@Topic(patterns = ".*")
	public void receive(ConsumerRecord<UUID, byte[]> record) {
		log.info("Retrieved record from {} with key: {}", record.topic(), record.key(), new String(record.value()));
		records.add(record);
	}
}
