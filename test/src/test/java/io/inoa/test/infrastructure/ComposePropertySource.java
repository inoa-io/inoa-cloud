package io.inoa.test.infrastructure;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.DockerComposeContainer.RemoveImages;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.micronaut.context.env.ActiveEnvironment;
import io.micronaut.context.env.MapPropertySource;
import io.micronaut.context.env.PropertySource;
import io.micronaut.context.env.PropertySourceLoader;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.core.io.socket.SocketUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Property source for micronaut that starts docker-compose (via testcontainers) and returns docker ports as properties.
 * See <b>application-test.yaml</b> for usage of this properties.
 */
@Slf4j
public class ComposePropertySource implements PropertySourceLoader {

	private static final Duration TIMEOUT = Duration.ofMinutes(10);
	private static final File COMPOSE_FILE = Paths.get("target/compose/docker-compose.yaml").toFile();
	private static final String INFLUXDB = "influxdb";
	private static final String POSTGRES = "postgres";
	private static final String KEYCLOAK = "keycloak";
	private static final String KAFKA = "kafka";
	private static final String ZOOKEEPER = "zookeeper";
	private static final String GRAFANA = "grafana";
	private static final String ENVOY = "envoy";
//	private static final String HONO_MQTT = "hono-adapter-mqtt";
	private static final String TENANT = "cnpm-tenant-service";
	private static final String AUTH = "cnpm-auth-service";
	private static final String ECHO = "cnpm-echo-service";
//	private static final String REGISTRY = "fleet-registry-service";
//	private static final String BRDIGE = "fleet-registry-hono-bridge";
	private static final String PROVISIONER = "measurement-provisioner";
	private static final String TRANSLATOR = "measurement-telemetry-translator";
	private static final String EXPORTER = "measurement-telemetry-exporter";

	private static Map<String, Object> cache;

	@Override
	public Map<String, Object> read(String name, InputStream input) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<PropertySource> load(String resource, ResourceLoader loader) {
		return Optional.empty();
	}

	@SuppressWarnings("resource")
	@Override
	public Optional<PropertySource> loadEnv(String resource, ResourceLoader loader, ActiveEnvironment environment) {
		if (cache == null) {
			if (SocketUtils.isTcpPortAvailable(5380)) {
				log.info("Start docker-compose containers.");
				var waitForHealthcheck = Wait.forHealthcheck().withStartupTimeout(TIMEOUT);
				var container = new DockerComposeContainer<>(COMPOSE_FILE)
						.withServices(
								POSTGRES, INFLUXDB, ZOOKEEPER, KAFKA,
								KEYCLOAK, ENVOY, TENANT, AUTH, ECHO,
//								HONO_MQTT, "hono-adapter-mqtt", "hono-service-command-router",
//								REGISTRY, BRDIGE,
								PROVISIONER, GRAFANA, TRANSLATOR, EXPORTER)
						.withExposedService(KEYCLOAK, 8080)
						.withExposedService(INFLUXDB, 8086)
						.withExposedService(GRAFANA, 3000)
						.withExposedService(ENVOY, 8080)
//						.withExposedService(HONO_MQTT, 1883)
						.waitingFor(TENANT, waitForHealthcheck)
						.waitingFor(AUTH, waitForHealthcheck)
//						.waitingFor(REGISTRY, waitForHealthcheck)
//						.waitingFor(BRDIGE, waitForHealthcheck)
						.waitingFor(TRANSLATOR, waitForHealthcheck)
						.waitingFor(EXPORTER, waitForHealthcheck)
						.withLogConsumer(KEYCLOAK, new Slf4jLogConsumer(log).withPrefix(KEYCLOAK))
						.withLogConsumer(ENVOY, new Slf4jLogConsumer(log).withPrefix(ENVOY))
						.withLogConsumer(TENANT, new Slf4jLogConsumer(log).withPrefix(TENANT))
						.withLogConsumer(AUTH, new Slf4jLogConsumer(log).withPrefix(AUTH))
//						.withLogConsumer(REGISTRY, new Slf4jLogConsumer(log).withPrefix(REGISTRY))
//						.withLogConsumer(BRDIGE, new Slf4jLogConsumer(log).withPrefix(BRDIGE))
						.withLogConsumer(PROVISIONER, new Slf4jLogConsumer(log).withPrefix(PROVISIONER))
						.withLogConsumer(TRANSLATOR, new Slf4jLogConsumer(log).withPrefix(TRANSLATOR))
						.withLogConsumer(EXPORTER, new Slf4jLogConsumer(log).withPrefix(EXPORTER))
						.withRemoveImages(RemoveImages.LOCAL)
						.withLocalCompose(true);
				container.start();
				cache = Map.of(
						"test.keycloak.8080", "localhost:" + container.getServicePort(KEYCLOAK, 8080),
						"test.influxdb.8086", "localhost:" + container.getServicePort(INFLUXDB, 8086),
						"test.grafana.3000", "localhost:" + container.getServicePort(GRAFANA, 3000),
						"test.envoy.8080", "localhost:" + container.getServicePort(ENVOY, 8080)
//						,"test.mqtt.1883", "localhost:" + container.getServicePort(HONO_MQTT, 1883)
				);
			} else {
				log.info("Use existing containers and skip compose start.");
				cache = Map.of(
						"test.keycloak.8080", KEYCLOAK + ":" + 8080,
						"test.influxdb.8086", INFLUXDB + ":" + 8086,
						"test.grafana.3000", GRAFANA + ":" + 3000,
						"test.envoy.8080", ENVOY + ":" + 8080
//						,"test.mqtt.1883", HONO_MQTT + ":" + 1883
				);
			}
			log.info("Use properties: {}", cache);
		}
		return Optional.of(new MapPropertySource("testcontainers", cache));
	}
}
