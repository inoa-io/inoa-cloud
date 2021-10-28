package io.inoa.fleet.registry.usecase;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Hex;

import io.inoa.fleet.registry.infrastructure.ComposeTest;
import lombok.SneakyThrows;

/**
 * Test usecase: create tenant with gateway and token handling.
 *
 * @author Stephan Schnabel
 */
@DisplayName("Wumm")
public class WummTest extends ComposeTest {

	@DisplayName("DVH4013 with work/power in/out")
	@Test
	@SneakyThrows
	void dvh4013() {

		// create gateway

		var gateway = registry.withUser(USER_TENANT_A).createGateway();

		// send telemetry

		var timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		monitoring.reset();
		gateway.mqtt().connect()
				.sendTelemetry("urn:dvh4013:0815:0x0000", timestamp, Hex.decodeHex("0103017B0C16"))
				.sendTelemetry("urn:dvh4013:0815:0x0002", timestamp, Hex.decodeHex("01030201C80C16"))
				.sendTelemetry("urn:dvh4013:0815:0x4001", timestamp, Hex.decodeHex("0103017B0C16"))
				.sendTelemetry("urn:dvh4013:0815:0x4101", timestamp, Hex.decodeHex("01030201C80C16"));
		monitoring.awaitExporterKafkaRecords("wait for messaged stored in influx", 4);
		influxdb.awaitTables(gateway, 4);

		// check messages in influx

		var fluxTables = influxdb.findByGateway(gateway);
		assertEquals(4, fluxTables.size(), "flux tables");
		assertAll("influxdb",
				() -> assertAll("0x0000", influxdb.asserts(
						influxdb.filterByDeviceTypeAndSensor(fluxTables, "dvh4013", "0x0000"),
						gateway, "urn:dvh4013:0815:0x0000", timestamp, 12.3D,
						Map.of("label", "power_in"))),
				() -> assertAll("0x0002", influxdb.asserts(
						influxdb.filterByDeviceTypeAndSensor(fluxTables, "dvh4013", "0x0002"),
						gateway, "urn:dvh4013:0815:0x0002", timestamp, 45.6D,
						Map.of("label", "power_out"))),
				() -> assertAll("0x4000", influxdb.asserts(
						influxdb.filterByDeviceTypeAndSensor(fluxTables, "dvh4013", "0x4001"),
						gateway, "urn:dvh4013:0815:0x4001", timestamp, 123D,
						Map.of("label", "work_in", "obis", "1-0:1.8.0*255"))),
				() -> assertAll("0x8102", influxdb.asserts(
						influxdb.filterByDeviceTypeAndSensor(fluxTables, "dvh4013", "0x4101"),
						gateway, "urn:dvh4013:0815:0x4101", timestamp, 456D,
						Map.of("label", "work_out", "obis", "1-0:2.8.0*255"))));
	}
}
