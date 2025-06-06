package io.inoa.measurement.translator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.measurement.translator.AbstractTranslatorTest;
import io.inoa.rest.TelemetryVO;

/**
 * Test for {@link TranslateListener}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("translator: kafka listener")
public class TranslateListenerTest extends AbstractTranslatorTest {

	@Inject
	TranslateListener listener;
	@Inject
	TranslateMetrics metrics;

	@DisplayName("send message to translate")
	@Test
	void success() {

		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var deviceType = "example";
		var deviceId = UUID.randomUUID().toString();
		var sensor = "number";
		var urn = "urn:" + deviceType + ":" + deviceId + ":" + sensor;
		var timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		var counter = metrics.counterSuccess(tenantId, deviceType, sensor);
		var countBefore = counter.count();

		// send message

		send(
				tenantId,
				gatewayId,
				"{\"urn\":\""
						+ urn
						+ "\",\"timestamp\":"
						+ timestamp.toEpochMilli()
						+ ",\"value\":\"Mg==\"}");

		// validate translated message

		var actual = kafka.getInoaTelemetry(tenantId, gatewayId).value();
		var expected = new TelemetryVO()
				.tenantId(tenantId)
				.gatewayId(gatewayId)
				.urn(urn)
				.deviceType(deviceType)
				.deviceId(deviceId)
				.sensor(sensor)
				.timestamp(timestamp)
				.value(2D);
		assertEquals(expected, actual, "inoa message");
		assertEquals(countBefore + 1, counter.count(), "increment");
	}

	@DisplayName("fail: message read")
	@Test
	void failMessageRead() {

		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var counter = metrics.counterFailMessageRead(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "i am not a valid json");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	@DisplayName("fail: message validate")
	@Test
	void failMessageValidate() {

		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var counter = metrics.counterFailMessageValidate(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"abc\",\"timestamp\":123,\"value\":\"Mg==\"}");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	@DisplayName("fail: no converter")
	@Test
	void failConverter() {

		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var counter = metrics.counterFailConverter(tenantId, "example", "nope");
		var countBefore = counter.count();

		send(
				tenantId,
				gatewayId,
				"{\"urn\":\"urn:example:0815:nope\",\"timestamp\":0,\"value\":\"Mg==\"}");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	@DisplayName("fail: invalid value")
	@Test
	void failValue() {

		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var counter = metrics.counterFailValue(tenantId, "example", "number");
		var countBefore = counter.count();

		send(
				tenantId,
				gatewayId,
				"{\"urn\":\"urn:example:0815:number\",\"timestamp\":0,\"value\":\"bm9wZQo=\"}");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	private void send(String tenantId, String gatewayId, String payload) {
		listener.receive(new ConsumerRecord<>("hono.telemetry." + tenantId, 0, 0, gatewayId, payload));
	}
}
