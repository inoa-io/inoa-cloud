package io.inoa.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Singleton;

import org.junit.jupiter.api.function.Executable;

import com.influxdb.LogLevel;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import io.inoa.test.Await;
import io.inoa.test.client.GatewayClientFactory.GatewayClient;
import io.micronaut.context.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class InfluxDBClient {

	private final com.influxdb.client.InfluxDBClient client;
	private final String organisation;
	private final String bucket;

	@SuppressWarnings("resource")
	InfluxDBClient(
			@Value("${influxdb.url}") String url,
			@Value("${influxdb.token}") char[] token,
			@Value("${influxdb.organisation}") String organisation,
			@Value("${influxdb.bucket}") String bucket,
			@Value("${influxdb.log-level}") LogLevel level) {
		this.client = InfluxDBClientFactory.create(url, token, bucket, organisation)
				.disableGzip()
				.setLogLevel(level);
		this.organisation = organisation;
		this.bucket = bucket;
	}

	// query

	public List<FluxTable> findByGateway(GatewayClient gateway) {
		return findByGatewayId(gateway.getGatewayId());
	}

	public List<FluxTable> findByGatewayId(String gatewayId) {
		var query = "from(bucket:\""
				+ bucket
				+ "\")"
				+ " |> range(start: -10h)"
				+ " |> filter(fn: (r) => r.gateway_id == \""
				+ gatewayId
				+ "\")";
		return client.getQueryApi().query(query, organisation);
	}

	public List<FluxTable> awaitTables(GatewayClient gateway, int amount) {
		return Await.await(log, "wait for " + amount + " messages for " + gateway.getGatewayId())
				.timeout(Duration.ofSeconds(30))
				.until(() -> findByGateway(gateway), tables -> tables.size() >= amount);
	}

	// extract

	public FluxRecord filterByDeviceTypeAndSensor(
			List<FluxTable> tables, String deviceType, String sensor) {
		var records = tables.stream()
				.flatMap(table -> table.getRecords().stream())
				.filter(record -> record.getValueByKey("type").equals(deviceType))
				.filter(record -> record.getValueByKey("sensor").equals(sensor))
				.collect(Collectors.toList());
		assertEquals(1, records.size(), "record filtering failed: " + tables);
		return records.get(0);
	}

	// asserts

	public Set<Executable> asserts(
			FluxRecord record, GatewayClient gateway, String urn, Instant timestamp, Double value) {
		return asserts(record, gateway, urn, timestamp, value, Map.of());
	}

	public Set<Executable> asserts(
			FluxRecord record,
			GatewayClient gateway,
			String urn,
			Instant timestamp,
			Double value,
			Map<String, String> tags) {
		var urnParts = urn.split(":")[1];
		return asserts(
				record,
				gateway,
				urn,
				urnParts,
				urn.split(":")[2],
				urn.split(":")[3],
				timestamp,
				value,
				tags);
	}

	public Set<Executable> asserts(
			FluxRecord record,
			GatewayClient gateway,
			String urn,
			String deviceType,
			String deviceId,
			String sensor,
			Instant timestamp,
			Double value,
			Map<String, String> tags) {
		var executables = new HashSet<Executable>();
		executables.add(() -> assertEquals("inoa", record.getMeasurement(), "measurement"));
		executables.add(() -> assertEquals(gateway.getTenantId(), record.getValueByKey("tenant_id")));
		executables.add(() -> assertEquals(gateway.getGatewayId(), record.getValueByKey("gateway_id")));
		executables.add(() -> assertEquals(urn, record.getValueByKey("urn"), "urn"));
		executables.add(() -> assertEquals(deviceType, record.getValueByKey("type"), "type"));
		executables.add(() -> assertEquals(deviceId, record.getValueByKey("device_id"), "device_id"));
		executables.add(() -> assertEquals(sensor, record.getValueByKey("sensor"), "sensor"));
		executables.add(() -> assertEquals(timestamp, record.getTime(), "timestamp"));
		executables.add(() -> assertEquals(value, record.getValue(), "value"));
		for (var tag : tags.entrySet()) {
			assertEquals(tag.getValue(), record.getValueByKey(tag.getKey()), "ext." + tag.getKey());
		}
		return executables;
	}
}
