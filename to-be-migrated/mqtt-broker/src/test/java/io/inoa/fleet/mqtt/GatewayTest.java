package io.inoa.fleet.mqtt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GatewayTest {

	@DisplayName("parse valid username")
	@Test
	void success() {
		var tenantId = "inoa";
		var gatewayId = "GW-0001";
		var username = gatewayId + "@" + tenantId;
		var gateway = Gateway.of(username);
		assertNotNull(gateway, "failed to parse username: " + username);
		assertEquals(tenantId, gateway.tenantId(), "tenantId");
		assertEquals(gatewayId, gateway.gatewayId(), "gatewayId");
	}

	@DisplayName("fail on invalid gatewayId")
	@Test
	void fail() {
		var username = "nope";
		var gateway = Gateway.of(username);
		assertNull(gateway, "parsed username: " + username);
	}
}
