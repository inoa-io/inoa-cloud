package io.inoa.fleet.registry.usecase;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nimbusds.jose.crypto.RSASSAVerifier;

import io.inoa.fleet.registry.infrastructure.ComposeTest;
import io.inoa.fleet.registry.test.GatewayClient;
import lombok.SneakyThrows;

/**
 * Test usecase: create tenant with gateway and token handling.
 *
 * @author Stephan Schnabel
 */
@DisplayName("gateway")
public class GatewayTest extends ComposeTest {

	static Instant timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS);
	static String tenantId = UUID.randomUUID().toString().substring(0, 10);
	static GatewayClient gateway;

	@DisplayName("00. collect preconditions")
	@Test
	void preconditions() {
		monitoring.reset();
	}

	@DisplayName("01. create tenant in tenant service")
	@Test
	void createTenantInTenantService() {
		tenantService.create(tenantId, "Test", true);
	}

	@DisplayName("02. create tenant in gateway registry")
	@Test
	void createTenantInGatewayRegistry() {
		gatewayRegistry.waitForTenant(tenantId);
	}

	@DisplayName("11. create gateway with psk secret")
	@Test
	void createGateway() {
		gateway = gatewayRegistry.createGateway();
	}

	@DisplayName("12. fetch registry token")
	@Test
	void getRegistryToken() {
		gateway.fetchRegistryToken();
	}

	@DisplayName("13. validate registry token")
	@Test
	@SneakyThrows
	void validateRegistryToken() {
		var jwt = gateway.getRegistryToken();
		var key = gatewayRegistry.getJwkSet().getKeyByKeyId(jwt.getHeader().getKeyID());
		assertNotNull(key, "key unknown");
		var verifier = new RSASSAVerifier(key.toRSAKey());
		assertTrue(verifier.verify(jwt.getHeader(), jwt.getSigningInput(), jwt.getSignature()), "signature validation");
	}

	@DisplayName("14. set and get properties")
	@Test
	void gatewayProperties() {
		var properties = Map.of("aa", "1", "bb", "2");
		gateway.setProperties(properties);
		assertEquals(properties, gateway.getProperties(), "invalid by gateway");
		assertEquals(properties, gatewayRegistry.findGateway(gateway).getProperties(), "invalid by user");
	}

	@DisplayName("15. mqtt connect")
	@Test
	void mqttConnect() {
		gateway.mqtt().connect();
	}

	@DisplayName("16. mqtt send telemetry")
	@Test
	void mqttSendTelemetry() {
		gateway.mqtt().sendTelemetry("urn:example:0815:number", timestamp, "123.456".getBytes());
	}

	@DisplayName("17. wait for message translated to inoa message")
	@Test
	void waitForTranslator() {
		monitoring.awaitTranslatorSuccess("wait for message translated to inoa message", 1);
	}

	@DisplayName("18. wait for message in influx")
	@Test
	void waitForInfluxdb() {
		monitoring.awaitExporterKafkaRecords("wait for message stored in influxdb", 1);
	}

	@DisplayName("19. check message in influx")
	@Test
	void checkInfluxdb() {
		var tables = influxdb.findByGateway(gateway);
		var record = influxdb.filterByDeviceTypeAndSensor(tables, "example", "number");
		assertAll("influxdb", influxdb.asserts(record, gateway, "urn:example:0815:number", timestamp, 123.456D));
	}
}
