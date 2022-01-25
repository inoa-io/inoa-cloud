package io.inoa.measurement.provisioner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClient;

import io.inoa.measurement.provisioner.grafana.Assignment;
import io.inoa.measurement.provisioner.grafana.GrafanaClient;
import io.inoa.measurement.provisioner.grafana.UserRole;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
public abstract class AbstractTest implements TestPropertyProvider {

	private static Map<String, String> properties;

	@Inject
	InfluxDBClient influx;
	@Inject
	GrafanaClient grafana;

	protected Optional<com.influxdb.client.domain.Organization> getInfluxOrganization(String name) {
		return influx.getOrganizationsApi().findOrganizations().stream()
				.filter(org -> Objects.equals(org.getName(), name))
				.findAny();
	}

	protected Optional<io.inoa.measurement.provisioner.grafana.Organization> getGrafanaOrganization(String name) {
		return grafana.findOrganizationByName(name);
	}

	protected void assertGrafanaRole(String tenantId, String email, UserRole expectedRole) {
		grafana.findUserByEmail(email).ifPresent(user -> {
			var actualRole = grafana.findOrganizationsByUser(user.getId()).stream()
					.filter(org -> org.getName().equals(tenantId))
					.map(Assignment::getRole)
					.findAny().orElse(null);
			assertEquals(expectedRole, actualRole, "role");
		});
	}

	@Override
	@SuppressWarnings("resource")
	public Map<String, String> getProperties() {
		if (properties == null) {

			var org = "test-org";
			var bucket = "test-bucket";
			var token = "test-token";
			var username = "admin";
			var password = "password";

			var influxContainer = new GenericContainer<>(DockerImageName.parse("influxdb:2.1.1-alpine"))
					.withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
					.withEnv("DOCKER_INFLUXDB_INIT_USERNAME", username)
					.withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", password)
					.withEnv("DOCKER_INFLUXDB_INIT_ORG", org)
					.withEnv("DOCKER_INFLUXDB_INIT_BUCKET", bucket)
					.withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", token)
					.withExposedPorts(8086)
					.waitingFor(Wait.forListeningPort());
			var grafanaContainer = new GenericContainer<>(DockerImageName.parse("grafana/grafana:8.3.3"))
					.withEnv("GF_SECURITY_ADMIN_USER", username)
					.withEnv("GF_SECURITY_ADMIN_PASSWORD", password)
					.withEnv("GF_ANALYTICS_CHECK_FOR_UPDATES", Boolean.FALSE.toString())
					.withEnv("GF_ANALYTICS_REPORTING_ENABLED", Boolean.FALSE.toString())
					.withExposedPorts(3000)
					.waitingFor(Wait.forListeningPort());
			Stream.of(influxContainer, grafanaContainer).parallel().forEach(GenericContainer::start);

			properties = Map.of(
					"grafana.url", "http://" + grafanaContainer.getHost() + ":" + grafanaContainer.getMappedPort(3000),
					"grafana.username", username,
					"grafana.password", password,
					"influxdb.url", "http://" + influxContainer.getHost() + ":" + influxContainer.getMappedPort(8086),
					"influxdb.log-level", LogLevel.NONE.toString(),
					"influxdb.token", token,
					"influxdb.org", org,
					"influxdb.bucket", bucket);
		}
		return properties;
	}
}
