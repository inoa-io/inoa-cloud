package io.inoa.measurement.translator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.measurement.telemetry.TelemetryVO;
import io.inoa.measurement.translator.AbstractTest;
import io.inoa.measurement.translator.messaging.MessagingSink;
import jakarta.inject.Inject;

public class TranslateListenerTest extends AbstractTest {

	@Inject
	MessagingSink messaging;
	@Inject
	TranslateListener listener;
	@Inject
	TranslateMetrics metrics;

	@DisplayName("send message to translate")
	@Test
	void success() {

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var deviceType = "example";
		var deviceId = UUID.randomUUID().toString();
		var sensor = "number";
		var urn = "urn:" + deviceType + ":" + deviceId + ":" + sensor;
		var timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		var counter = metrics.counterSuccess(tenantId, deviceType, sensor);
		var countBefore = counter.count();

		// send message

		send(tenantId, gatewayId,
				"{\"urn\":\"" + urn + "\",\"timestamp\":" + timestamp.toEpochMilli() + ",\"value\":\"Mg==\"}");

		// validate translated message

		var actual = messaging.getRecords().get(0).value();
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

	@DisplayName("fail: invalid tenant id")
	@Test
	void failTenantId() {

		var tenantId = "abc";
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailTenantId(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"urn:example:0815:number\",\"timestamp\":0,\"value\":\"Mg==\"}");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	@DisplayName("fail: invalid gateway id")
	@Test
	void failGatewayId() {

		var tenantId = "inoa";
		var gatewayId = "nope";
		var counter = metrics.counterFailGatewayId(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"urn:example:0815:number\",\"timestamp\":0,\"value\":\"Mg==\"}");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	@DisplayName("fail: message read")
	@Test
	void failMessageRead() {

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailMessageRead(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "i am not a valid json");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	@DisplayName("fail: message validate")
	@Test
	void failMessageValidate() {

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailMessageValidate(tenantId);
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"abc\",\"timestamp\":123,\"value\":\"Mg==\"}");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	@DisplayName("fail: no converter")
	@Test
	void failConverter() {

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailConverter(tenantId, "example", "nope");
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"urn:example:0815:nope\",\"timestamp\":0,\"value\":\"Mg==\"}");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	@DisplayName("fail: invalid value")
	@Test
	void failValue() {

		var tenantId = "inoa";
		var gatewayId = UUID.randomUUID();
		var counter = metrics.counterFailValue(tenantId, "example", "number");
		var countBefore = counter.count();

		send(tenantId, gatewayId, "{\"urn\":\"urn:example:0815:number\",\"timestamp\":0,\"value\":\"bm9wZQo=\"}");
		assertEquals(countBefore + 1, counter.count(), "metrics");
	}

	private void send(String tenantId, Object gatewayId, String payload) {
		listener
				.receive(new ConsumerRecord<>("hono.telemetry." + tenantId, 0, 0, gatewayId.toString(), payload));
	}
}