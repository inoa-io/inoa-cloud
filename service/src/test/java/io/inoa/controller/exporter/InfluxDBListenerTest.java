package io.inoa.controller.exporter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.exceptions.BadRequestException;
import io.inoa.rest.TelemetryVO;
import io.inoa.test.AbstractUnitTest;
import io.micrometer.core.instrument.MeterRegistry;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link InfluxDBListener}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("exporter: kafka listener")
@MicronautTest(environments = "exporter")
public class InfluxDBListenerTest extends AbstractUnitTest {

  @Inject InfluxDBClient influx;
  @Inject InfluxDBListener listener;
  @Inject MeterRegistry meterRegistry;

  @DisplayName("write to influx")
  @Test
  void success() {

    var countSuccessBefore = countSuccess();
    var countFailureBefore = countFailure();
    var telemetry =
        new TelemetryVO()
            .tenantId("inoa")
            .gatewayId("GW-0001")
            .urn("urn")
            .deviceId(UUID.randomUUID().toString())
            .deviceType(UUID.randomUUID().toString())
            .sensor(UUID.randomUUID().toString())
            .ext(Map.of("sensor", "should be overidden by origing", "foo", "bar"))
            .timestamp(Instant.now().minusSeconds(7200).truncatedTo(ChronoUnit.SECONDS))
            .value(123.456D);

    // send message

    listener.receive(telemetry);

    // check influx

    var table =
        influx
            .getQueryApi()
            .query("from(bucket:\"test-bucket\") |> range(start: -10h)")
            .iterator()
            .next();
    assertEquals(1, table.getRecords().size(), "records");
    var record = table.getRecords().get(0);
    assertAll(
        "record",
        () -> assertEquals("inoa", record.getMeasurement(), "measurement"),
        () -> assertEquals(telemetry.getTenantId(), record.getValueByKey("tenant_id"), "tenant_id"),
        () ->
            assertEquals(
                telemetry.getGatewayId(), record.getValueByKey("gateway_id"), "gateway_id"),
        () -> assertEquals(telemetry.getUrn(), record.getValueByKey("urn"), "urn"),
        () -> assertEquals(telemetry.getDeviceId(), record.getValueByKey("device_id"), "device_id"),
        () -> assertEquals(telemetry.getDeviceType(), record.getValueByKey("type"), "type"),
        () -> assertEquals(telemetry.getSensor(), record.getValueByKey("sensor"), "sensor"),
        () -> assertEquals("bar", record.getValueByKey("foo"), "ext.foo"),
        () -> assertEquals(telemetry.getTimestamp(), record.getTime(), "timestamp"),
        () -> assertEquals(telemetry.getValue(), record.getValue(), "value"));

    // check metrics

    assertEquals(countSuccessBefore + 1D, countSuccess(), "count success");
    assertEquals(countFailureBefore, countFailure(), "count failure");
  }

  @DisplayName("failure because of invalid payload")
  @Test
  void failure() {

    var countSuccessBefore = countSuccess();
    var countFailureBefore = countFailure();
    var telemetry =
        new TelemetryVO()
            .tenantId("inoa")
            .gatewayId("GW-0001")
            .urn("urn")
            .deviceId(UUID.randomUUID().toString())
            .deviceType(UUID.randomUUID().toString())
            .sensor(UUID.randomUUID().toString())
            .timestamp(Instant.MAX)
            .value(123.456D);

    // send message

    assertThrows(BadRequestException.class, () -> listener.receive(telemetry));

    // check metrics

    assertEquals(countSuccessBefore, countSuccess(), "count success");
    assertEquals(countFailureBefore + 1D, countFailure(), "count failure");
  }

  private int countSuccess() {
    return (int) meterRegistry.counter("inoa_exporter_influxdb_success").count();
  }

  private int countFailure() {
    return (int) meterRegistry.counter("inoa_exporter_influxdb_failure").count();
  }
}
