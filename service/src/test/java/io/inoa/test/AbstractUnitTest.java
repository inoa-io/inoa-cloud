package io.inoa.test;

import io.inoa.fleet.Data;
import io.inoa.fleet.FleetProperties;
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
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

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
  public @Inject FleetProperties oldProperties;
  public @Inject KafkaSink kafka;

  // test setup

  @BeforeEach
  void init(JdbcOperations jdbc) {

    // cleanup database and other stores

    jdbc.execute(
        c ->
            c.createStatement()
                .execute(
                    Stream.of("thing_type", "gateway", "tenant")
                        .map(table -> "TRUNCATE TABLE " + table + " CASCADE;")
                        .collect(Collectors.joining())));
    kafka.reset();

    // handle token

    oldProperties.getGateway().getToken().setForceNotBefore(true);
    oldProperties.getGateway().getToken().setForceJwtId(true);
    oldProperties.getGateway().getToken().setForceIssuedAt(true);
    oldProperties.getGateway().getToken().setIssuedAtThreshold(Optional.of(Duration.ofSeconds(5)));
  }

  @SuppressWarnings({ "resource", "deprecation", "rawtypes" })
  @Override
  @SneakyThrows
  public Map<String, String> getProperties() {
    if (testProperties == null) {

      // configure logback before starting

      new LogbackLoggingSystem(null, null).refresh();

      // get properties

      var imageProperties = new Properties();
      imageProperties.load(
          AbstractUnitTest.class.getResourceAsStream("/application-test.properties"));

      // start docker containers

      // use fixed ports in CI because k8s services only expose specified ports
      var fixedPorts = System.getenv("CI") != null; 

      var influxOrganisation = "test-org";
      var influxBucket = "test-bucket";
      var influxToken = "changeMe";
      var influxImage = imageProperties.getProperty("image.influxdb");
      var influxContainer = (fixedPorts 
          ? (GenericContainer<?>) new FixedHostPortGenericContainer(influxImage).withFixedExposedPort(8086, 8086)
          : (GenericContainer<?>) new GenericContainer(influxImage).withExposedPorts(8086))
              .withEnv("DOCKER_INFLUXDB_INIT_MODE", "setup")
              .withEnv("DOCKER_INFLUXDB_INIT_USERNAME", "username")
              .withEnv("DOCKER_INFLUXDB_INIT_PASSWORD", "password")
              .withEnv("DOCKER_INFLUXDB_INIT_ORG", influxOrganisation)
              .withEnv("DOCKER_INFLUXDB_INIT_BUCKET", influxBucket)
              .withEnv("DOCKER_INFLUXDB_INIT_ADMIN_TOKEN", influxToken)
              .waitingFor(Wait.forListeningPort());

      var postgresImage = imageProperties.getProperty("image.postgresql");
      var postgres =(fixedPorts 
          ? (GenericContainer<?>) new FixedHostPortGenericContainer(postgresImage).withFixedExposedPort(5432, 5432)
          : (GenericContainer<?>) new GenericContainer(postgresImage).withExposedPorts(5432))
              .withEnv("POSTGRES_DB", "inoa")
              .withEnv("POSTGRES_USER", "inoa")
              .withEnv("POSTGRES_PASSWORD", "changeMe")
              .withExposedPorts(5432)
              .waitingFor(Wait.forListeningPort());

      Stream.of(postgres, influxContainer).parallel().forEach(GenericContainer::start);

      testProperties =
          Map.of(
              // use fixed ports for all tests, this avoids port conflicts
              "inoa.mqtt.port",
              String.valueOf(SocketUtils.findAvailableTcpPort()),
              "inoa.mqtt.tls.port",
              String.valueOf(SocketUtils.findAvailableTcpPort()),
              "inoa.mqtt.tls.generate-key",
              "true",
              "influxdb.url",
              "http://" + influxContainer.getHost() + ":" + influxContainer.getMappedPort(8086),
              "influxdb.token",
              influxToken,
              "influxdb.organisation",
              influxOrganisation,
              "influxdb.bucket",
              influxBucket,
              "datasources.default.host",
              postgres.getHost(),
              "datasources.default.port",
              String.valueOf(postgres.getMappedPort(5432)));
    }
    return testProperties;
  }

  // auth
  public String auth() {
    return auth(new String[0]);
  }

  public String auth(Tenant... tenants) {
    var tenantIds = Stream.of(tenants).map(Tenant::getTenantId).toList();
    return auth(tenantIds.toArray(new String[tenantIds.size()]));
  }

  public String auth(String... tenantIds) {
    return new JwtProvider(signature)
        .builder()
        .subject("admin")
        .claim("email", "test@example.org")
        .claim("aud", "inoa-cloud")
        .claim(oldProperties.getSecurity().getClaimTenants(), tenantIds)
        .toBearer();
  }
}
