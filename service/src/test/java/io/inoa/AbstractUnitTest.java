package io.inoa;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.inoa.fleet.ApplicationProperties;
import io.inoa.fleet.Data;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.rest.JwtProvider;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.logging.impl.LogbackLoggingSystem;
import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;
import jakarta.inject.Inject;
import lombok.SneakyThrows;

/**
 * Base for all unit tests.
 *
 * @author stephan.schnabel@grayc.de
 */
public abstract class AbstractUnitTest extends AbstractMicronautTest {

	public @Inject SignatureGeneratorConfiguration signature;
	public @Inject Data data;
	public @Inject ApplicationProperties properties;
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

		properties.getGateway().getToken().setForceNotBefore(true);
		properties.getGateway().getToken().setForceJwtId(true);
		properties.getGateway().getToken().setForceIssuedAt(true);
		properties.getGateway().getToken().setIssuedAtThreshold(Optional.of(Duration.ofSeconds(5)));
	}

	@SuppressWarnings("resource")
	@Override
	@SneakyThrows
	public Map<String, String> getSuiteProperties() {

		// configure logback before starting

		new LogbackLoggingSystem(null, null).refresh();

		// get properties

		var properties = new Properties();
		properties.load(AbstractUnitTest.class.getResourceAsStream("/application-test.properties"));

		// start docker containers

		var influxOrganisation = "test-org";
		var influxBucket = "test-bucket";
		var influxToken = "changeMe";
		var influxContainer = new GenericContainer<>(properties.getProperty("image.influxdb"))
				.withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
				.withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "username")
				.withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "password")
				.withEnv("DOCKER_INFLUXDB_INIT_ORG", influxOrganisation)
				.withEnv("DOCKER_INFLUXDB_INIT_BUCKET", influxBucket)
				.withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", influxToken)
				.withExposedPorts(8086)
				.waitingFor(Wait.forListeningPort());

		var postgres = new GenericContainer<>(properties.getProperty("image.postgresql"))
				.withEnv("POSTGRES_DB", "inoa")
				.withEnv("POSTGRES_USER", "inoa").withEnv("POSTGRES_PASSWORD", "changeMe")
				.withExposedPorts(5432)
				.waitingFor(Wait.forListeningPort());

		Stream.of(postgres, influxContainer).parallel().forEach(GenericContainer::start);

		return Map.of(
				"influxdb.url", "http://" + influxContainer.getHost() + ":" + influxContainer.getMappedPort(8086),
				"influxdb.token", influxToken,
				"influxdb.organisation", influxOrganisation,
				"influxdb.bucket", influxBucket,
				"datasources.default.host", postgres.getHost(),
				"datasources.default.port", String.valueOf(postgres.getMappedPort(5432)));
	}

	// auth

	public String auth(Tenant tenant) {
		return auth(tenant.getTenantId());
	}

	public String auth(Tenant... tenants) {
		return new JwtProvider(signature).builder()
				.subject("admin")
				.claim(properties.getSecurity().getClaimTenants(), Stream.of(tenants).map(Tenant::getTenantId).toList())
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
				.claim(properties.getSecurity().getClaimTenants(), List.of(tenantId)).toBearer();
	}

	public String auth(String... tenantIds) {
		return new JwtProvider(signature).builder()
				.subject("admin")
				.claim("email", "test@example.org")
				.claim(properties.getSecurity().getClaimTenants(), tenantIds).toBearer();
	}
}
