package io.inoa.fleet.registry.infrastructure;

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
import lombok.extern.slf4j.Slf4j;

/**
 * Property source for micronaut that starts docker-compose (via testcontainers) and returns docker ports as properties.
 * See <b>application-test.yaml</b> for usage of this properties.
 *
 * @author Stephan Schnabel
 */
@Slf4j
public class ComposePropertySource implements PropertySourceLoader {

	private static Map<String, Object> cache;

	@Override
	public Map<String, Object> read(String name, InputStream input) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<PropertySource> load(String resource, ResourceLoader loader) {
		return Optional.empty();
	}

	@Override
	public Optional<PropertySource> loadEnv(String resource, ResourceLoader loader, ActiveEnvironment environment) {
		if (cache == null) {
			cache = handleCompose();
		}
		return Optional.of(new MapPropertySource("testcontainers", cache));
	}

	@SuppressWarnings("resource")
	private Map<String, Object> handleCompose() {

		// check if compose should be used

		var skipCompose = Boolean.parseBoolean(System.getProperty("skipCompose", "false"));
		if (skipCompose) {
			log.info("Use existing containers and skip compose start.");
			return Map.of();
		}

		// start container

		var wait = Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(5));
		var container = new DockerComposeContainer<>(Paths.get("target/compose/docker-compose.yaml").toFile())
				.withExposedService("gateway-registry-service", 8080, wait)
				.withExposedService("gateway-registry-service", 8090, wait)
				.withExposedService("keycloak", 9000, wait)
				.withLogConsumer("gateway-registry-service", new Slf4jLogConsumer(log).withPrefix("service"))
				.withLogConsumer("keycloak", new Slf4jLogConsumer(log).withPrefix("keycloak"))
				.withLogConsumer("postgres", new Slf4jLogConsumer(log).withPrefix("postgres"))
				.withRemoveImages(RemoveImages.ALL)
				.withLocalCompose(false);
		container.start();

		// publish mapped ports

		var properties = Map.<String, Object>of(
				"docker.gateway-registry-service.8080", container.getServicePort("gateway-registry-service", 8080),
				"docker.gateway-registry-service.8090", container.getServicePort("gateway-registry-service", 8090),
				"docker.keycloak.9000", container.getServicePort("keycloak", 9000));
		log.info("Started compose: {}", properties);

		return properties;
	}
}
