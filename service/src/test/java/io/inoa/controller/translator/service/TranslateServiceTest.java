package io.inoa.controller.translator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.controller.translator.AbstractTranslatorTest;
import io.inoa.messaging.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micronaut.cache.CacheManager;
import jakarta.inject.Inject;

/**
 * Test for {@link TranslateService}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("translator: service")
public class TranslateServiceTest extends AbstractTranslatorTest {

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
		var raw = TelemetryRawVO.of(urn, now.plusSeconds(60), 100);
		assertEquals(List.of(init), service.translate(tenantId, gatewayId, raw), "init inoa vo");
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
		raw = TelemetryRawVO.of(urn, now.plusSeconds(120), 200);
		assertEquals(List.of(valid), service.translate(tenantId, gatewayId, raw), "valid inoa vo");
		assertEquals(valid, cache.get(urn, TelemetryVO.class).orElse(null), "valid cache vo");

		// with filled cache and outdated value

		raw = TelemetryRawVO.of(urn, now.plusSeconds(110), 190);
		assertEquals(List.of(), service.translate(tenantId, gatewayId, raw), "outdated inoa vo");
		assertEquals(valid, cache.get(urn, TelemetryVO.class).orElse(null), "outdated cache vo");

		// with filled cache and outdated value

		raw = TelemetryRawVO.of(urn, now.plusSeconds(130), 190);
		assertEquals(List.of(), service.translate(tenantId, gatewayId, raw), "negative inoa vo");
		assertEquals(valid, cache.get(urn, TelemetryVO.class).orElse(null), "negative cache vo");

		// with filled cache and very large plus value

		raw = TelemetryRawVO.of(urn, now.plusSeconds(3720), 100201);
		assertEquals(List.of(), service.translate(tenantId, gatewayId, raw), "threshold inoa vo");
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
		assertEquals(List.of(valid1),
				service.translate(tenantId, gatewayId, TelemetryRawVO.of(urn, now.plusSeconds(180), 300)),
				"valid inoa vo");
		assertEquals(valid1, cache.get(urn, TelemetryVO.class).orElse(null), "valid cache vo");
	}

	@DisplayName("Example - base64 to single value")
	@Test
	void typeExampleSingleValue() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = TelemetryRawVO.of("urn:example:0815:number", "1234");
		var actual = service.translate(tenantId, gatewayId, raw);
		var expected = List.of(new TelemetryVO()
				.tenantId(tenantId)
				.gatewayId(gatewayId)
				.urn(raw.urn())
				.deviceType("example")
				.deviceId("0815")
				.sensor("number")
				.timestamp(raw.timestamp())
				.value(1234D));
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("Example - base64 to multiple values")
	@Test
	void typeExampleMultipleValues() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = TelemetryRawVO
				.of("urn:example:0815:json",
						"{\"string\":\"sdf\",\"int\":4,\"double\":34.01,\"bool\":true,\"obj\":{}}");
		var actual = service.translate(tenantId, gatewayId, raw);
		var expected = List.of(
				new TelemetryVO()
						.tenantId(tenantId)
						.gatewayId(gatewayId)
						.urn(raw.urn() + ".int")
						.deviceType("example")
						.deviceId("0815")
						.sensor("json.int")
						.timestamp(raw.timestamp())
						.value(4D),
				new TelemetryVO()
						.tenantId(tenantId)
						.gatewayId(gatewayId)
						.urn(raw.urn() + ".double")
						.deviceType("example")
						.deviceId("0815")
						.sensor("json.double")
						.timestamp(raw.timestamp())
						.value(34.01D));
		assertEquals(expected, actual, "inoa message");
	}

	@DisplayName("fail: value not convertable")
	@Test
	void failNoConvertable() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = TelemetryRawVO.of("urn:example:0815:number", "NAN");
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}

	@DisplayName("fail: no converter")
	@Test
	void failNoConverter() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = TelemetryRawVO.of("urn:example:0815:nope", "1234");
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}

	@DisplayName("fail: unsupported urn")
	@Test
	void failUnsupportedUrn() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var raw = TelemetryRawVO.of("NOPE", "1234");
		assertTrue(service.translate(tenantId, gatewayId, raw).isEmpty(), "inoa");
	}
}
