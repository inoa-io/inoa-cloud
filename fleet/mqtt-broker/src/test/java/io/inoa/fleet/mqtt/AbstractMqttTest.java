package io.inoa.fleet.mqtt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import io.inoa.fleet.mqtt.listener.TestListener;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractMqttTest implements TestPropertyProvider {

	static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.6"));

	@BeforeEach
	void clear(TestListener listener) {
		listener.clear();
	}

	@Override
	public Map<String, String> getProperties() {
		if (!kafka.isRunning()) {
			kafka.start();
		}
		return Map.of("kafka.bootstrap.servers", kafka.getBootstrapServers());
	}

	public static void assertHeader(ConsumerRecord<?, ?> record, String name, Object value) {
		var headers = StreamSupport.stream(record.headers().headers(name).spliterator(), false).toList();
		assertEquals(1, headers.size(), "header size for " + name);
		assertArrayEquals(value.toString().getBytes(), headers.get(0).value(), "header value for " + name);
	}
}
