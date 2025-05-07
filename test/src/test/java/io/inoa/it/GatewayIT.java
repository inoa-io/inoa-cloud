package io.inoa.it;

import static io.inoa.test.HttpAssertions.assert204;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.messaging.TelemetryRawVO;
import io.inoa.rest.GatewayApiTestClient;
import io.inoa.rest.GatewayUpdateVO;
import io.inoa.rest.RegisterVO;
import io.inoa.test.AbstractIntegrationTest;

public class GatewayIT extends AbstractIntegrationTest {

	private static final String gatewayId = gatewayId();
	private static final byte[] preSharedKey = UUID.randomUUID().toString().getBytes();

	@DisplayName("1. self register gateway")
	@Test
	void register(GatewayApiTestClient gatewayApiClient) {
		assert204(
				() -> gatewayApiClient.register(
						new RegisterVO()
								.gatewayId(gatewayId)
								.credentialType(io.inoa.rest.CredentialTypeVO.PSK)
								.credentialValue(preSharedKey)),
				"failed to register gateway");

		var gateway = registry.findGateway(gatewayId);
		assertEquals(gatewayId, gateway.getGatewayId(), "gatewayId");
		assertNull(gateway.getName(), "name");
		assertFalse(gateway.getEnabled(), "enabled");
		assertTrue(gateway.getGroupIds().isEmpty(), "groupId");
		assertTrue(gateway.getProperties().isEmpty(), "properties");
	}

	@DisplayName("2. enable gateway")
	@Test
	void enableGateway() {
		assertFalse(registry.findGateway(gatewayId).getEnabled(), "gateway should be disabled");
		registry.update(gatewayId, new GatewayUpdateVO().enabled(true));
		assertTrue(registry.findGateway(gatewayId).getEnabled(), "enabling gateway failed");
	}

	@DisplayName("3. read configuration")
	@Test
	void getConfiguration() {
		var gatewayClient = gatewayClientFactory.get(gatewayId, preSharedKey);
		var expected = Map.of("mqtt.insecure", true, "mqtt.url", mqttUrl, "ntp.host", "pool.ntp.org");
		var actual = gatewayClient.getConfiguration();
		assertEquals(expected, actual, "got invalid configuration");
	}

	@DisplayName("4. handle properties")
	@Test
	void setProperties() {
		var gatewayClient = gatewayClientFactory.get(gatewayId, preSharedKey);
		var properties = Map.of("aa", "1", "bb", "2");
		assertEquals(properties, gatewayClient.setProperties(properties), "failed to set properties");
		assertEquals(
				properties, gatewayClient.getProperties(), "invalid properties read from gateway endpoint");
		assertEquals(
				properties,
				registry.findGateway(gatewayId).getProperties(),
				"invalid properties read from registry management endpoint");
	}

	@DisplayName("5. send telemetry")
	@Test
	@Disabled("Currently flaky in woodpecker. Re-Enable after stabilization")
	void sendTelemetry() throws JsonProcessingException {
		var gatewayClient = gatewayClientFactory.get(gatewayId, preSharedKey);
		var timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		var value = 4_000_000D;
		var urn = "urn:satellite:" + gatewayId + ":memory_heap";
		var payload = new ObjectMapper()
				.findAndRegisterModules()
				.writeValueAsBytes(
						new TelemetryRawVO(urn, timestamp, String.valueOf(value).getBytes(), null));
		assertDoesNotThrow(
				() -> gatewayClient.mqtt().trustAllCertificates().connect(), "mqtt connect failed");
		assertDoesNotThrow(() -> gatewayClient.mqtt().publishTelemetry(payload), "mqtt publish failed");
		assertDoesNotThrow(() -> gatewayClient.mqtt().disconnect(), "mqtt disconnect failed");

		var tables = influxdb.awaitTables(gatewayClient, 1);
		var record = influxdb.filterByDeviceTypeAndSensor(tables, "satellite", "memory_heap");
		assertAll(influxdb.asserts(record, gatewayClient, urn, timestamp, value));
	}
}
