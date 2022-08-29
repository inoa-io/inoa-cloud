package io.inoa.fleet.mqtt;

import java.util.Map;

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

	static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.2"));

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
}
