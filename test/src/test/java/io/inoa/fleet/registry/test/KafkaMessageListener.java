package io.inoa.fleet.registry.test;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import io.inoa.fleet.registry.test.KafkaMessageStore.KafkaMessage;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@KafkaListener
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListener {

	private static final String PATTERN_HONO_TELEMETRY = "^hono\\.telemetry\\.(?<tenantId>[0-9a-f\\-]{36})$";

	private final KafkaMessageStore store;
	private final org.apache.kafka.clients.admin.AdminClient admin;

	@PostConstruct
	public void init() {
		admin.createTopics(Set.of(new NewTopic("hono.telemetry.2381b39a-e721-4456-8f9f-8d2c18cec993", 1, (short) 1)));
	}

	@Topic(patterns = PATTERN_HONO_TELEMETRY)
	public void receive(ConsumerRecord<UUID, byte[]> record) {

		var matcher = Pattern.compile(PATTERN_HONO_TELEMETRY).matcher(record.topic());
		if (!matcher.matches()) {
			return;
		}

		var tenantId = UUID.fromString(matcher.group(1));
		var gatewayId = record.key();
		var payload = record.value();
		var message = new KafkaMessage(tenantId, gatewayId, new String(payload));
		store.getMessages().add(message);
		log.info("Retrieved: " + message);
	}
}
