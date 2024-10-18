package io.inoa.it;

import static io.inoa.test.HttpAssertions.assert200;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.inoa.rest.GatewayPageVO;
import io.inoa.test.AbstractIntegrationTest;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test regarding angular app and docs.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("app with static-files")
public class AppFilesIT extends AbstractIntegrationTest {

  @Inject
  @Client("inoa")
  HttpClient client;

  @DisplayName("/index.html be served")
  @Test
  void index() {
    var request = HttpRequest.GET("/").header(HttpHeaders.AUTHORIZATION, keycloak.admin());
    var response = client.toBlocking().exchange(request, String.class);

    assertEquals(HttpStatus.OK, response.getStatus());
    assertEquals(MediaType.TEXT_HTML_TYPE, response.getContentType().orElse(null));
    var body = response.body();
    assertTrue(body.contains("<!DOCTYPE html>"), "html def:\n\n" + body);
    assertTrue(body.contains("<title>INOA Ground Control</title>"), "html title:\n\n" + body);
  }

  @DisplayName("api should work with `yarn start`")
  @Test
  void yarnStart() {
    var request =
        HttpRequest.GET("/gateways").header(HttpHeaders.REFERER, "http://localhost:4200/");
    assert200(() -> client.toBlocking().exchange(request, GatewayPageVO.class));
  }
}
