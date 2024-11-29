package io.inoa.fleet.exporter;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for telemetry exporter.
 *
 * @author stephan.schnabel@grayc.de
 */
@Factory
public class ExporterFactory {

  private static final Logger log = LoggerFactory.getLogger(ExporterFactory.class);

  @PostConstruct
  void log() {
    log.info("Starting controller: {}", getClass().getPackage().getName());
  }

  @Singleton
  InfluxDBClientOptions options(
      @Value("${influxdb.url:`http://influxdb:8086`}") String url,
      @Value("${influxdb.token:changeMe}") char[] token,
      @Value("${influxdb.organisation:default}") String organisation,
      @Value("${influxdb.bucket:default}") String bucket,
      @Value("${influxdb.log-level:NONE}") LogLevel logLevel) {
    return InfluxDBClientOptions.builder()
        .url(url)
        .authenticateToken(token)
        .org(organisation)
        .bucket(bucket)
        .logLevel(logLevel)
        .build();
  }

  @Requires(property = "influxdb.enabled", notEquals = "false")
  @Singleton
  @Bean(preDestroy = "close")
  InfluxDBClient client(InfluxDBClientOptions options) {
    return InfluxDBClientFactory.create(options);
  }
}
