package io.inoa.fleet.registry.infrastructure;

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
 *
 * @author Stephan Schnabel
 */
@Slf4j
public class ComposePropertySource implements PropertySourceLoader {

	private static final File COMPOSE_FILE = Paths.get("target/compose/docker-compose.yaml").toFile();
	private static final String CONTAINER_POSTGRES = "postgres";
	private static final String CONTAINER_KEYCLOAK = "keycloak";
	private static final String CONTAINER_KAFKA = "kafka";
	private static final String CONTAINER_TENANT = "tenant-service";
	private static final String CONTAINER_REGISTRY = "gateway-registry";
	private static final String CONTAINER_BRIDGE = "gateway-registry-hono-bridge";
	private static final String CONTAINER_TRANSLATE = "inoa-translator";
	private static final String CONTAINER_EXPORTER = "inoa-exporter";
	private static final String CONTAINER_HONO_MQTT = "hono-adapter-mqtt";
	private static final String CONTAINER_INFLUXDB = "influxdb";

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
				var waitForHealthcheck = Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(10));
				var container = new DockerComposeContainer<>(COMPOSE_FILE)
						.withServices(
								"zookeeper",
								CONTAINER_KAFKA,
								CONTAINER_INFLUXDB,
								CONTAINER_POSTGRES,
								"hono-service-auth",
								"hono-service-command-router",
								CONTAINER_HONO_MQTT,
								CONTAINER_BRIDGE,
								CONTAINER_TENANT,
								CONTAINER_REGISTRY,
								CONTAINER_KEYCLOAK,
								CONTAINER_TRANSLATE,
								CONTAINER_EXPORTER)
						.withExposedService(CONTAINER_INFLUXDB, 8086)
						.withExposedService(CONTAINER_TENANT, 8080)
						.withExposedService(CONTAINER_REGISTRY, 8080)
						.withExposedService(CONTAINER_REGISTRY, 8090)
						.withExposedService(CONTAINER_KEYCLOAK, 8080)
						.withExposedService(CONTAINER_TRANSLATE, 8090)
						.withExposedService(CONTAINER_EXPORTER, 8090)
						.withExposedService(CONTAINER_HONO_MQTT, 1883)
						.waitingFor(CONTAINER_INFLUXDB, waitForHealthcheck)
						.waitingFor(CONTAINER_TENANT, waitForHealthcheck)
						.waitingFor(CONTAINER_REGISTRY, waitForHealthcheck)
						.waitingFor(CONTAINER_KEYCLOAK, waitForHealthcheck)
						.waitingFor(CONTAINER_BRIDGE, waitForHealthcheck)
						.waitingFor(CONTAINER_HONO_MQTT, waitForHealthcheck)
						.waitingFor(CONTAINER_TRANSLATE, waitForHealthcheck)
						.waitingFor(CONTAINER_EXPORTER, waitForHealthcheck)
						.withLogConsumer(CONTAINER_TENANT, new Slf4jLogConsumer(log).withPrefix(CONTAINER_TENANT))
						.withLogConsumer(CONTAINER_REGISTRY, new Slf4jLogConsumer(log).withPrefix(CONTAINER_REGISTRY))
						.withLogConsumer(CONTAINER_BRIDGE, new Slf4jLogConsumer(log).withPrefix(CONTAINER_BRIDGE))
						.withLogConsumer(CONTAINER_KEYCLOAK, new Slf4jLogConsumer(log).withPrefix(CONTAINER_KEYCLOAK))
						.withLogConsumer(CONTAINER_TRANSLATE, new Slf4jLogConsumer(log).withPrefix(CONTAINER_TRANSLATE))
						.withLogConsumer(CONTAINER_EXPORTER, new Slf4jLogConsumer(log).withPrefix(CONTAINER_EXPORTER))
						.withRemoveImages(RemoveImages.ALL)
						.withLocalCompose(false);
				container.start();
				cache = Map.of(
						"test.tenant-service.8080", "localhost:" + container.getServicePort(CONTAINER_TENANT, 8080),
						"test.gateway-registry.8080", "localhost:" + container.getServicePort(CONTAINER_REGISTRY, 8080),
						"test.gateway-registry.8090", "localhost:" + container.getServicePort(CONTAINER_REGISTRY, 8090),
						"test.keycloak.8080", "localhost:" + container.getServicePort(CONTAINER_KEYCLOAK, 8080),
						"test.mqtt.1883", "localhost:" + container.getServicePort(CONTAINER_HONO_MQTT, 1883),
						"test.inoa-exporter.8090", "localhost:" + container.getServicePort(CONTAINER_EXPORTER, 8090),
						"test.inoa-translator.8090", "localhost:" + container.getServicePort(CONTAINER_TRANSLATE, 8090),
						"test.influxdb.8086", "localhost:" + container.getServicePort(CONTAINER_INFLUXDB, 8086));
			} else {
				log.info("Use existing containers and skip compose start.");
				cache = Map.of(
						"test.tenant-service.8080", CONTAINER_TENANT + ":" + 8080,
						"test.gateway-registry.8080", CONTAINER_REGISTRY + ":" + 8080,
						"test.gateway-registry.8090", CONTAINER_REGISTRY + ":" + 8090,
						"test.keycloak.8080", CONTAINER_KEYCLOAK + ":" + 8080,
						"test.mqtt.1883", CONTAINER_HONO_MQTT + ":" + 1883,
						"test.inoa-exporter.8090", CONTAINER_EXPORTER + ":" + 8090,
						"test.inoa-translator.8090", CONTAINER_TRANSLATE + ":" + 8090,
						"test.influxdb.8086", CONTAINER_INFLUXDB + ":" + 8086);
			}
			log.info("Use properties: {}", cache);
		}
		return Optional.of(new MapPropertySource("testcontainers", cache));
	}
}
