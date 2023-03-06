package io.inoa.measurement.exporter;

import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
public abstract class AbstractTest implements TestPropertyProvider {

	private static Map<String, String> properties;

	@Override
	@SuppressWarnings("resource")
	public Map<String, String> getProperties() {
		if (properties == null) {

			var organisation = "test-org";
			var bucket = "test-bucket";
			var token = "changeMe";

			var influxContainer = new GenericContainer<>(DockerImageName.parse("influxdb:2.1.1-alpine"))
					.withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
					.withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "username")
					.withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "password")
					.withEnv("DOCKER_INFLUXDB_INIT_ORG", organisation)
					.withEnv("DOCKER_INFLUXDB_INIT_BUCKET", bucket)
					.withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", token)
					.withExposedPorts(8086)
					.waitingFor(Wait.forListeningPort());
			influxContainer.start();

			properties = Map.of(
					"influxdb.url", "http://" + influxContainer.getHost() + ":" + influxContainer.getMappedPort(8086),
					"influxdb.token", token,
					"influxdb.organisation", organisation,
					"influxdb.bucket", bucket);
		}
		return properties;
	}
}
