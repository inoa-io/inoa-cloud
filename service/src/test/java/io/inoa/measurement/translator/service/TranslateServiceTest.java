package io.inoa.measurement.translator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.rest.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.inoa.test.AbstractUnitTest;
import io.micronaut.cache.CacheManager;
import jakarta.inject.Inject;

public class TranslateServiceTest extends AbstractUnitTest {

	@Inject TranslateService service;

	@DisplayName("Watthour")
	@Test
	void wattHour(CacheManager<?> cacheManager) {

		var cache = cacheManager.getCache("watthour");
		cache.invalidateAll();

		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var urn = "urn:example:0815:watthour";
		var now = Instant.ofEpochSecond(0);

		// with empty cache

		var init = new TelemetryVO()
				.tenantId(tenantId)
				.gatewayId(gatewayId)
				.urn(urn)
				.deviceType("example")
				.deviceId("0815")
				.sensor("watthour")
				.timestamp(now.plusSeconds(60))
				.value(100D)
				.ext(Map.of("unit", "watthour"));
		assertEquals(List.of(init), service.translate(tenantId, gatewayId, new TelemetryRawVO()
				.urn(urn)
				.timestamp(now.plusSeconds(60).toEpochMilli())
				.value("100".getBytes())), "init inoa vo");
		assertEquals(init, cache.get(urn, TelemetryVO.class).orElse(null), "init cache vo");

		// with filled cache and valid value

		var valid = new TelemetryVO()
				.tenantId(tenantId)
				.gatewayId(gatewayId)
				.urn(urn)
				.deviceType("example")
				.deviceId("0815")
				.sensor("watthour")
				.timestamp(now.plusSeconds(120))
				.value(200D)
				.ext(Map.of("unit", "watthour"));
		assertEquals(List.of(valid), service.translate(tenantId, gatewayId, new TelemetryRawVO()
				.urn(urn)
				.timestamp(now.plusSeconds(120).toEpochMilli())
				.value("200".getBytes())), "valid inoa vo");
		assertEquals(valid, cache.get(urn, TelemetryVO.class).orElse(null), "valid cache vo");

		// with filled cache and outdated value

		assertEquals(List.of(), service.translate(tenantId, gatewayId, new TelemetryRawVO()
				.urn(urn)
				.timestamp(now.plusSeconds(110).toEpochMilli())
				.value("190".getBytes())), "outdated inoa vo");
		assertEquals(valid, cache.get(urn, TelemetryVO.class).orElse(null), "outdated cache vo");

		// with filled cache and outdated value

		assertEquals(List.of(), service.translate(tenantId, gatewayId, new TelemetryRawVO()
				.urn(urn)
				.timestamp(now.plusSeconds(130).toEpochMilli())
				.value("190".getBytes())), "negative inoa vo");
		assertEquals(valid, cache.get(urn, TelemetryVO.class).orElse(null), "negative cache vo");

		// with filled cache and very large plus value

		assertEquals(List.of(), service.translate(tenantId, gatewayId, new TelemetryRawVO()
				.urn(urn)
				.timestamp(now.plusSeconds(3720).toEpochMilli())
				.value("100201".getBytes())), "threshold inoa vo");
		assertEquals(valid, cache.get(urn, TelemetryVO.class).orElse(null), "threshold cache vo");

		// with filled cache and valid value again

		var valid1 = new TelemetryVO()
				.tenantId(tenantId)
				.gatewayId(gatewayId)
				.urn(urn)
				.deviceType("example")
				.deviceId("0815")
				.sensor("watthour")
				.timestamp(now.plusSeconds(180))
				.value(300D)
				.ext(Map.of("unit", "watthour"));
		assertEquals(List.of(valid1), service.translate(tenantId, gatewayId, new TelemetryRawVO()
				.urn(urn)
				.timestamp(now.plusSeconds(180).toEpochMilli())
				.value("300".getBytes())), "valid inoa vo");
		assertEquals(valid1, cache.get(urn, TelemetryVO.class).orElse(null), "valid cache vo");
	}

	@DisplayName("Example - base64 to single value")
	@Test
	void typeExampleSingleValue() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:number")
				.timestamp(Instant.now().toEpochMilli())
				.value("1234".getBytes());
		var actual = service.translate(tenantId, gatewayId, raw);
		var expected = List.of(new TelemetryVO()
				.tenantId(tenantId)
				.gatewayId(gatewayId)
				.urn(raw.getUrn())
				.deviceType("example")
				.deviceId("0815")
				.sensor("number")
				.timestamp(Instant.ofEpochMilli(raw.getTimestamp()))
				.value(1234D));
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("Example - base64 to multiple values")
	@Test
	void typeExampleMultipleValues() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:json")
				.timestamp(Instant.now().toEpochMilli())
				.value("{\"string\":\"sdf\",\"int\":4,\"double\":34.01,\"bool\":true,\"obj\":{}}".getBytes());
		var actual = service.translate(tenantId, gatewayId, raw);
		var expected = List.of(
				new TelemetryVO()
						.tenantId(tenantId)
						.gatewayId(gatewayId)
						.urn(raw.getUrn() + ".int")
						.deviceType("example")
						.deviceId("0815")
						.sensor("json.int")
						.timestamp(Instant.ofEpochMilli(raw.getTimestamp()))
						.value(4D),
				new TelemetryVO()
						.tenantId(tenantId)
						.gatewayId(gatewayId)
						.urn(raw.getUrn() + ".double")
						.deviceType("example")
						.deviceId("0815")
						.sensor("json.double")
						.timestamp(Instant.ofEpochMilli(raw.getTimestamp()))
						.value(34.01D));
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:number")
				.timestamp(Instant.now().toEpochMilli())
				.value("NAN".getBytes());
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}

	@DisplayName("fail: no converter")
	@Test
	void failNoConverter() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = new TelemetryRawVO()
				.urn("urn:example:0815:nope")
				.timestamp(Instant.now().toEpochMilli())
				.value("1234".getBytes());
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}

	@DisplayName("fail: unsupported urn")
	@Test
	void failUnsupportedUrn() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = new TelemetryRawVO()
				.urn("NOPE")
				.timestamp(Instant.now().toEpochMilli())
				.value("1234".getBytes());
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}
}
