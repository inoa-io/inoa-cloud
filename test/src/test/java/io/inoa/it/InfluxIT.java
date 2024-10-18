package io.inoa.it;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClientFactory;
import io.inoa.test.AbstractIntegrationTest;
import io.micronaut.context.annotation.Value;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InfluxIT extends AbstractIntegrationTest {

  private static final String gatewayId = gatewayId();
  private static final byte[] preSharedKey = UUID.randomUUID().toString().getBytes();

  private com.influxdb.client.InfluxDBClient client;

  @Value("${influxdb.url}")
  private String url;

  @Value("${influxdb.token}")
  private char[] token;

  @Value("${influxdb.organisation}")
  private String organisation;

  @Value("${influxdb.bucket}")
  private String bucket;

  @Value("${influxdb.log-level}")
  private LogLevel level;

  public InfluxIT() {}

  @BeforeEach
  void before() {
    this.client =
        InfluxDBClientFactory.create(url, token, bucket, organisation)
            .disableGzip()
            .setLogLevel(level);
  }

  @DisplayName(value = "1. Verify InfluxDB connection")
  @Test
  void verifyInfluxDB() {
    assertNotNull(this.client.ready());
  }
}
