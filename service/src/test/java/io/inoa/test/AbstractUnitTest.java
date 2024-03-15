package io.inoa.test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.inoa.fleet.ApplicationProperties;
import io.inoa.fleet.Data;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.rest.JwtProvider;
import io.micronaut.core.io.socket.SocketUtils;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.json.JsonMapper;
import io.micronaut.logging.impl.LogbackLoggingSystem;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

/**
 * Base for all unit tests.
 *
 * @author stephan.schnabel@grayc.de
 */
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractUnitTest extends AbstractTest implements TestPropertyProvider {

	private static Map<String, String> testProperties;

	public @Inject JsonMapper mapper;
	public @Inject Validator validator;
	public @Inject SignatureGeneratorConfiguration signature;
	public @Inject Data data;
	public @Inject ApplicationProperties oldProperties;
	public @Inject KafkaSink kafka;

	// test setup

	@BeforeEach
	void init(JdbcOperations jdbc) {

		// cleanup database and other stores

		jdbc.execute(c -> c.createStatement().execute(Stream
				.of("thing_type", "gateway", "tenant")
				.map(table -> "TRUNCATE TABLE " + table + " CASCADE;")
				.collect(Collectors.joining())));
		kafka.reset();

		// handle token

		oldProperties.getGateway().getToken().setForceNotBefore(true);
		oldProperties.getGateway().getToken().setForceJwtId(true);
		oldProperties.getGateway().getToken().setForceIssuedAt(true);
		oldProperties.getGateway().getToken().setIssuedAtThreshold(Optional.of(Duration.ofSeconds(5)));
	}

	@SuppressWarnings("resource")
	@Override
	@SneakyThrows
	public Map<String, String> getProperties() {
		if (testProperties == null) {

			// configure logback before starting

			new LogbackLoggingSystem(null, null).refresh();

			// get properties

			var imageProperties = new Properties();
			imageProperties.load(AbstractUnitTest.class.getResourceAsStream("/application-test.properties"));

			// start docker containers

			var influxOrganisation = "test-org";
			var influxBucket = "test-bucket";
			var influxToken = "changeMe";
			var influxContainer = new GenericContainer<>(imageProperties.getProperty("image.influxdb"))
					.withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
					.withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "username")
					.withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "password")
					.withEnv("DOCKER_INFLUXDB_INIT_ORG", influxOrganisation)
					.withEnv("DOCKER_INFLUXDB_INIT_BUCKET", influxBucket)
					.withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", influxToken)
					.withExposedPorts(8086)
					.waitingFor(Wait.forListeningPort());

			var postgres = new GenericContainer<>(imageProperties.getProperty("image.postgresql"))
					.withEnv("POSTGRES_DB", "inoa")
					.withEnv("POSTGRES_USER", "inoa").withEnv("POSTGRES_PASSWORD", "changeMe")
					.withExposedPorts(5432)
					.waitingFor(Wait.forListeningPort());

			Stream.of(postgres, influxContainer).parallel().forEach(GenericContainer::start);

			testProperties = Map.of(
					// use fixed ports for all tests, this avoids port conflicts
					"inoa.mqtt.port", String.valueOf(SocketUtils.findAvailableTcpPort()),
					"inoa.mqtt.tls.port", String.valueOf(SocketUtils.findAvailableTcpPort()),
					"inoa.mqtt.tls.generate-key", "true",
					"influxdb.url", "http://" + influxContainer.getHost() + ":" + influxContainer.getMappedPort(8086),
					"influxdb.token", influxToken,
					"influxdb.organisation", influxOrganisation,
					"influxdb.bucket", influxBucket,
					"datasources.default.host", postgres.getHost(),
					"datasources.default.port", String.valueOf(postgres.getMappedPort(5432)));
		}
		return testProperties;
	}

	// auth

	public String auth(Tenant tenant) {
		return auth(tenant.getTenantId());
	}

	public String auth(Tenant... tenants) {
		return new JwtProvider(signature).builder()
				.subject("admin")
				.claim(oldProperties.getSecurity().getClaimTenants(),
						Stream.of(tenants).map(Tenant::getTenantId).toList())
				.toBearer();
	}

	public String auth() {
		return new JwtProvider(signature).builder()
				.subject("admin")
				.claim("email", "test@example.org")
				.toBearer();
	}

	public String auth(String tenantId) {
		return new JwtProvider(signature).builder()
				.subject("admin")
				.claim("email", "test@example.org")
				.claim(oldProperties.getSecurity().getClaimTenants(), List.of(tenantId)).toBearer();
	}

	public String auth(String... tenantIds) {
		return new JwtProvider(signature).builder()
				.subject("admin")
				.claim("email", "test@example.org")
				.claim(oldProperties.getSecurity().getClaimTenants(), tenantIds).toBearer();
	}
}
