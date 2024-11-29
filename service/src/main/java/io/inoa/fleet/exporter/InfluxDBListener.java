package io.inoa.fleet.exporter;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import io.inoa.rest.TelemetryVO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Listener to write messages to InfluxDB.
 *
 * @author sschnabe
 */
@Requires(bean = InfluxDBClient.class)
@KafkaListener(offsetReset = OffsetReset.EARLIEST, redelivery = true)
public class InfluxDBListener {

  private static final Logger log = LoggerFactory.getLogger(InfluxDBListener.class);

  private final WriteApiBlocking influx;
  private final Counter counterSuccess;
  private final Counter counterFailure;

  public InfluxDBListener(InfluxDBClient influx, MeterRegistry meterRegistry) {
    this.influx = influx.getWriteApiBlocking();
    this.counterSuccess = meterRegistry.counter("inoa_exporter_influxdb_success");
    this.counterFailure = meterRegistry.counter("inoa_exporter_influxdb_failure");
  }

  @Topic(patterns = "inoa\\.telemetry\\..*")
  void receive(TelemetryVO telemetry) {
    try {

      var tenantId = telemetry.getTenantId();
      var gatewayId = telemetry.getGatewayId();
      MDC.put("tenantId", tenantId);
      MDC.put("gatewayId", gatewayId);
      log.trace("Retrieved: {}", telemetry);

      var point =
          Point.measurement("inoa")
              .time(telemetry.getTimestamp(), WritePrecision.MS)
              .addField("value", telemetry.getValue());
      var ext = telemetry.getExt();
      if (ext != null && !ext.isEmpty()) {
        point.addTags(telemetry.getExt());
      }

      influx.writePoint(
          point
              .addTag("tenant_id", tenantId)
              .addTag("gateway_id", gatewayId)
              .addTag("urn", telemetry.getUrn())
              .addTag("device_id", telemetry.getDeviceId())
              .addTag("type", telemetry.getDeviceType())
              .addTag("sensor", telemetry.getSensor()));

      counterSuccess.increment();

    } catch (RuntimeException e) {
      log.error("Failed to write to database", e);
      counterFailure.increment();
      throw e;
    } finally {
      MDC.remove("tenantId");
      MDC.remove("gatewayId");
    }
  }
}
