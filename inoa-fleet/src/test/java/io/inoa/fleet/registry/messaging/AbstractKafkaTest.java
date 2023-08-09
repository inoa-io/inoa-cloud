package io.inoa.fleet.registry.messaging;

import java.util.Map;

import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import io.inoa.fleet.AbstractTest;
import io.micronaut.test.support.TestPropertyProvider;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractKafkaTest extends AbstractTest implements TestPropertyProvider {

	static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.6"));

	@Override
	public Map<String, String> getProperties() {
		if (!kafka.isRunning()) {
			kafka.start();
		}
		return Map.of("kafka.bootstrap.servers", kafka.getBootstrapServers());
	}
}
