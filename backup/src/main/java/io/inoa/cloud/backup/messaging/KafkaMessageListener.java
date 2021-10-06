package io.inoa.cloud.backup.messaging;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import io.inoa.cloud.backup.domain.Message;
import io.inoa.cloud.backup.domain.MessageRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka listener to store payloads in database.
 *
 * @author Stephan Schnabel
 */
@KafkaListener(clientId = "backup", groupId = "backup", offsetReset = OffsetReset.EARLIEST)
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListener {

	public static final String COUNTER_NAME = "backup_messages_total";
	public static final String TAG_TOPIC = "topic";

	private final Map<String, Counter> counters = new HashMap<>();
	private final MeterRegistry meterRegistry;
	private final MessageRepository repository;

	@Topic(patterns = "hono\\.telemetry\\..*")
	public void receive(ConsumerRecord<String, String> record) {
		var message = repository.save(toMessage(record));
		log.trace("Retrieved: {}", message);
		counters.computeIfAbsent(record.topic(), topicName -> meterRegistry.counter(COUNTER_NAME, TAG_TOPIC, topicName))
				.increment();
	}

	private Message toMessage(ConsumerRecord<String, String> record) {
		var message = new Message()
				.setTopic(record.topic())
				.setKey(record.key())
				.setValue(record.value())
				.setTimestamp(Instant.ofEpochSecond(record.timestamp()))
				.setTimestampType(record.timestampType())
				.setHeaders(new HashMap<>());
		record.headers().forEach(header -> message.getHeaders().put(header.key(), new String(header.value())));
		return message;
	}
}
