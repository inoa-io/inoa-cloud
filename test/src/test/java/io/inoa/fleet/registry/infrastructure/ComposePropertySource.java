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
	private static final String CONTAINER_SERVICE = "gateway-registry-service";

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
				var waitForHealthcheck = Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(2));
				var container = new DockerComposeContainer<>(COMPOSE_FILE)
						.withServices(CONTAINER_SERVICE, CONTAINER_KEYCLOAK, CONTAINER_POSTGRES)
						.withExposedService(CONTAINER_SERVICE, 8080, waitForHealthcheck)
						.withExposedService(CONTAINER_SERVICE, 8090, waitForHealthcheck)
						.withExposedService(CONTAINER_KEYCLOAK, 8080, waitForHealthcheck)
						.withLogConsumer(CONTAINER_SERVICE, new Slf4jLogConsumer(log).withPrefix("service"))
						.withLogConsumer(CONTAINER_KEYCLOAK, new Slf4jLogConsumer(log).withPrefix(CONTAINER_KEYCLOAK))
						.withRemoveImages(RemoveImages.ALL)
						.withLocalCompose(false);
				container.start();
				cache = Map.of(
						"test.service.8080", "localhost:" + container.getServicePort(CONTAINER_SERVICE, 8080),
						"test.service.8090", "localhost:" + container.getServicePort(CONTAINER_SERVICE, 8090),
						"test.keycloak.8080", "localhost:" + container.getServicePort(CONTAINER_KEYCLOAK, 8080));
			} else {
				log.info("Use existing containers and skip compose start.");
				cache = Map.of(
						"test.service.8080", CONTAINER_SERVICE + ":" + 8080,
						"test.service.8090", CONTAINER_SERVICE + ":" + 8090,
						"test.keycloak.8080", CONTAINER_KEYCLOAK + ":" + 8080);
			}
			log.info("Use properties: {}", cache);
		}
		return Optional.of(new MapPropertySource("testcontainers", cache));
	}
}
