package io.inoa.test;

import static io.inoa.junit.HttpAssertions.assert204;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.crypto.RSASSAVerifier;

import io.inoa.client.GatewayClientFactory.GatewayClient;
import io.inoa.fleet.registry.gateway.GatewayApiClient;
import io.inoa.fleet.registry.gateway.RegisterVO;
import io.inoa.fleet.registry.management.GatewayUpdateVO;
import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.junit.AbstractTest;

public class GatewayIT extends AbstractTest {

	private static final UUID gatewayId = UUID.randomUUID();
	private static final byte[] preSharedKey = UUID.randomUUID().toString().getBytes();
	private static GatewayClient gatewayClient;

	@DisplayName("1. self register gateway")
	@Test
	void register(GatewayApiClient gatewayApiClient) {

		var gatewayName = "junit-" + UUID.randomUUID().toString().substring(0, 10);
		assert204(() -> gatewayApiClient.register(new RegisterVO()
				.gatewayId(gatewayId)
				.gatewayName(gatewayName)
				.credentialType(io.inoa.fleet.registry.gateway.CredentialTypeVO.PSK)
				.credentialValue(preSharedKey)), "failed to register gateway");
		gatewayClient = gatewayClientFactory.get(gatewayId, preSharedKey);

		var gateway = registry.findGateway(gatewayId);
		assertEquals(gatewayId, gateway.getGatewayId(), "gatewayId");
		assertEquals(gatewayName, gateway.getName(), "name");
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

	@DisplayName("3. get token from registry")
	@Test
	void getToken() {
		var jwt = gatewayClient.getSignedJWT();
		var keyId = jwt.getHeader().getKeyID();
		var key = registry.getJwkSet().getKeyByKeyId(keyId);
		assertNotNull(key, "key unknown");
		var valid = assertDoesNotThrow(() -> new RSASSAVerifier(key.toRSAKey()).verify(
				jwt.getHeader(),
				jwt.getSigningInput(),
				jwt.getSignature()), "signature validation failed");
		assertTrue(valid, "signature is not valid");
	}

	@DisplayName("4. read configuration")
	@Test
	void getConfiguration() {
		var expected = Map.of(
				"mqtt.insecure", true,
				"mqtt.url", "ssl://127.0.0.1:8883",
				"ntp.host", "pool.ntp.org");
		var actual = gatewayClient.getConfiguration();
		assertEquals(expected, actual, "got invalid configuration");
	}

	@DisplayName("5. handle properties")
	@Test
	void setProperties() {

		var properties = Map.of("aa", "1", "bb", "2");
		assertEquals(properties, gatewayClient.setProperties(properties), "failed to set properties");
		assertEquals(properties, gatewayClient.getProperties(), "invalid properties read from gateway endpoint");
		assertEquals(properties, registry.findGateway(gatewayId).getProperties(),
				"invalid properties read from registry management endpoint");
	}

	@DisplayName("6. send telemetry")
	@Test
	void sendTelemetry() throws JsonProcessingException {

		var timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
		var value = 4_000_000D;
		var urn = "urn:satellite:" + gatewayId + ":memory_heap";
		var payload = new ObjectMapper().writeValueAsBytes(new TelemetryRawVO()
				.urn(urn)
				.timestamp(timestamp.toEpochMilli())
				.value(String.valueOf(value).getBytes()));
		assertDoesNotThrow(() -> gatewayClient.mqtt().trustAllCertificates().connect(), "mqtt connect failed");
		assertDoesNotThrow(() -> gatewayClient.mqtt().publishTelemetry(payload), "mqtt publish failed");
		assertDoesNotThrow(() -> gatewayClient.mqtt().disconnect(), "mqtt disconnect failed");

		var tables = influxdb.awaitTables(gatewayClient, 1);
		var record = influxdb.filterByDeviceTypeAndSensor(tables, "satellite", "memory_heap");
		assertAll(influxdb.asserts(record, gatewayClient, urn, timestamp, value));
	}
}
