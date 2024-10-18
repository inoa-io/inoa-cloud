package io.inoa.test.client;

import static io.inoa.test.HttpAssertions.assert200;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.inoa.rest.GatewayApiTestClient;
import io.inoa.rest.PropertiesApiTestClient;
import io.micronaut.http.HttpHeaderValues;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class GatewayClientFactory {

  private final GatewayApiTestClient gatewayClient;
  private final PropertiesApiTestClient propertiesClient;

  public GatewayClient get(String gatewayId, byte[] preSharedKey) {
    return new GatewayClient(gatewayId, preSharedKey);
  }

  @RequiredArgsConstructor
  public class GatewayClient {

    @Getter private final String tenantId = "inoa";
    @Getter private final String gatewayId;
    private final byte[] preSharedKey;

    private String token;
    private MqttBrokerClient mqtt;

    private String bearer() {
      if (token == null) {
        var now = Instant.now();
        var claims =
            new JWTClaimsSet.Builder()
                .audience("inoa-cloud")
                .jwtID(UUID.randomUUID().toString())
                .issuer(gatewayId)
                .issueTime(Date.from(now))
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(10)));
        var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
        assertDoesNotThrow(
            () -> jwt.sign(new MACSigner(preSharedKey)), "failed to sign gateway jwt");
        token = jwt.serialize();
      }
      return HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + token;
    }

    // registry

    public Map<String, String> getProperties() {
      return assert200(() -> propertiesClient.getProperties(bearer()), "failed to get properties");
    }

    public Map<String, String> setProperties(Map<String, String> properties) {
      return assert200(
          () -> propertiesClient.setProperties(bearer(), properties), "failed to get properties");
    }

    public Map<String, Object> getConfiguration() {
      return assert200(
          () -> gatewayClient.getConfiguration(bearer()), "failed to get configuration");
    }

    // mqtt

    public MqttBrokerClient mqtt() {
      if (mqtt == null) {
        var mqttUrl = getConfiguration().get("mqtt.url");
        assertNotNull(mqttUrl, "mqtt.url is null");
        mqtt =
            assertDoesNotThrow(
                () -> new MqttBrokerClient(mqttUrl.toString(), "inoa", gatewayId, preSharedKey),
                "failed to create mqtt client");
      }
      return mqtt;
    }
  }
}
