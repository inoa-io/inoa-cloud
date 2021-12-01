package io.inoa.fleet.registry.usecase;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert400;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.awaitility.Awaitility;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.infrastructure.ComposeTest;

/**
 * Tenant related tests.
 *
 * @author Stephan Schnabel
 */
@DisplayName("tenant")
public class TenantTest extends ComposeTest {

	@DisplayName("tenant is disabled")
	@Test
	void tenantDisabled() {

		// create tenant & gateway

		var tenantId = tenantService.create().getTenantId();
		var gateway = gatewayRegistry.waitForTenant(tenantId).createGateway();

		// gateway shoud be able to fetch registry token and connect via mqtt

		gateway.fetchRegistryToken();
		gateway.mqtt().connect();
		gateway.mqtt().close();

		// disable tenant

		tenantService.update(tenantId, null, false);

		// gateway should not be able to communicate

		Awaitility.await("tenant disabeld in gateway regsitry").until(() -> gatewayRegistry
				.findTenant(tenantId)
				.filter(t -> !t.getEnabled())
				.isPresent());
		var errorJson = assert400(() -> gateway.getRegistryTokenResponse()).getBody(Map.class).get();
		assertEquals("invalid_grant", errorJson.get("error"), "error");
		assertEquals("tenant " + tenantId + " disabled", errorJson.get("error_description"), "description");
		assertThrows(MqttException.class, () -> gateway.mqtt().connect(), "mqtt should not be able to connect");

		// enable tenant again

		tenantService.update(tenantId, null, true);

		// gateway shoud be able to fetch registry token and connect via mqtt

		gateway.fetchRegistryToken();
		gateway.mqtt().connect();
	}
}
