package io.inoa.measurement.exporter.messaging;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.influxdb.client.InfluxDBClient;

import io.inoa.measurement.exporter.AbstractTest;
import io.inoa.measurement.telemetry.TelemetryVO;
import jakarta.inject.Inject;

public class TelemetryListenerTest extends AbstractTest {

	@Inject
	TelemetryListener listener;
	@Inject
	InfluxDBClient influx;

	@DisplayName("write to influx")
	@Test
	void success() {

		var telemetry = new TelemetryVO()
				.setTenantId("inoa")
				.setGatewayId(UUID.randomUUID())
				.setUrn("urn")
				.setDeviceId(UUID.randomUUID().toString())
				.setDeviceType(UUID.randomUUID().toString())
				.setSensor(UUID.randomUUID().toString())
				.setExt(Map.of("sensor", "should be overidden by origing", "foo", "bar"))
				.setTimestamp(Instant.now().minusSeconds(7200).truncatedTo(ChronoUnit.SECONDS))
				.setValue(123.456D);

		// send message

		listener.receive(telemetry);

		// check influx

		var table = influx.getQueryApi().query("from(bucket:\"test-bucket\") |> range(start: -10h)").iterator().next();
		assertEquals(1, table.getRecords().size(), "records");
		var record = table.getRecords().get(0);
		assertAll("record",
				() -> assertEquals("inoa", record.getMeasurement(), "measurement"),
				() -> assertEquals(telemetry.getTenantId().toString(), record.getValueByKey("tenant_id"), "tenant_id"),
				() -> assertEquals(telemetry.getGatewayId().toString(), record.getValueByKey("gateway_id"),
						"gateway_id"),
				() -> assertEquals(telemetry.getUrn(), record.getValueByKey("urn"), "urn"),
				() -> assertEquals(telemetry.getDeviceId(), record.getValueByKey("device_id"), "device_id"),
				() -> assertEquals(telemetry.getDeviceType(), record.getValueByKey("type"), "type"),
				() -> assertEquals(telemetry.getSensor(), record.getValueByKey("sensor"), "sensor"),
				() -> assertEquals("bar", record.getValueByKey("foo"), "ext.foo"),
				() -> assertEquals(telemetry.getTimestamp(), record.getTime(), "timestamp"),
				() -> assertEquals(telemetry.getValue(), record.getValue(), "value"));
	}
}
