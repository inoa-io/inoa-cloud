package io.inoa.it;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert404;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.inoa.test.AbstractIntegrationTest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for micronaut endpoints.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("micronaut endpoints")
public class MicronautEndpointsIT extends AbstractIntegrationTest {

  @Inject
  @Client("inoa")
  HttpClient client;

  @DisplayName("health")
  @Test
  void health() {
    var health = assert200(() -> client.toBlocking().exchange("/endpoints/health", Map.class));
    assertEquals("UP", health.get("status"));
    var details = Map.class.cast(health.get("details"));
    assertNotNull(details.get("jdbc"), "jdbc health indicator missing");
    assertNotNull(details.get("kafka"), "kafka health indicator missing");
  }

  @DisplayName("health/readiness")
  @Test
  void healthReadiness() {
    var health =
        assert200(() -> client.toBlocking().exchange("/endpoints/health/readiness", Map.class));
    assertEquals("UP", health.get("status"));
    var details = Map.class.cast(health.get("details"));
    assertNotNull(details.get("jdbc"), "jdbc health indicator missing");
    assertNotNull(details.get("kafka"), "kafka health indicator missing");
  }

  @DisplayName("health/liveness")
  @Test
  void healthLiveness() {
    var health =
        assert200(() -> client.toBlocking().exchange("/endpoints/health/liveness", Map.class));
    assertNotEquals("DOWN", health.get("status"));
    assertNull(health.get("details"), "unexpected details");
  }

  @DisplayName("flyway")
  @Test
  void flyway() {
    assert200(() -> client.toBlocking().exchange("/endpoints/flyway"));
  }

  @DisplayName("prometheus")
  @Test
  void prometheus() {
    assert200(() -> client.toBlocking().exchange("/endpoints/prometheus"));
  }

  @DisplayName("info")
  @Test
  void info() {
    assert404(() -> client.toBlocking().exchange("/endpoints/info"));
  }

  @DisplayName("routes")
  @Test
  void routes() {
    assert404(() -> client.toBlocking().exchange("/endpoints/routes"));
  }

  @DisplayName("refresh")
  @Test
  void refresh() {
    assert404(() -> client.toBlocking().exchange("/endpoints/refresh"));
  }

  @DisplayName("stop")
  @Test
  void stop() {
    assert404(() -> client.toBlocking().exchange("/endpoints/stop"));
  }

  @DisplayName("env")
  @Test
  void env() {
    assert404(() -> client.toBlocking().exchange("/endpoints/env"));
  }
}
