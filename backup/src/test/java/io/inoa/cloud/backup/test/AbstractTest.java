package io.inoa.cloud.backup.test;

import java.util.Map;

import javax.inject.Inject;

import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import io.inoa.cloud.backup.domain.MessageRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

/**
 * Base for all unit tests.
 *
 * @author Stephan Schnabel
 */
@MicronautTest(transactional = false, environments = "h2")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractTest implements TestPropertyProvider {

	public static final String TOPIC1 = "hono.telemetry.abc";
	public static final String TOPIC2 = "hono.telemetry.def";

	@Inject
	@KafkaClient
	public Producer<String, String> kafkaProducer;
	@Inject
	public MessageRepository repository;
	@Inject
	public MeterRegistry meterRegistry;

	@BeforeEach
	void setUp() {
		repository.deleteAll();
	}

	@Override
	public Map<String, String> getProperties() {
		var kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.2"));
		if (!kafka.isRunning()) {
			kafka.start();
		}
		return Map.of(
				"kafka.bootstrap.servers", kafka.getBootstrapServers(),
				"kafka.consumers.backup.metadata.max.age.ms", "200");
	}
}
