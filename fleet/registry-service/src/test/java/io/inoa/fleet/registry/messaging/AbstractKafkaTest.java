package io.inoa.fleet.registry.messaging;

import io.inoa.fleet.registry.Data;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractKafkaTest implements TestPropertyProvider {

    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.2"));

    @Inject
    public Data data;

    @Override
    public Map<String, String> getProperties() {
        if (!kafka.isRunning()) {
            kafka.start();
        }
        return Map.of("kafka.bootstrap.servers", kafka.getBootstrapServers());
    }
}
