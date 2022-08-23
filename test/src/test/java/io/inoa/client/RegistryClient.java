package io.inoa.client;

import static io.inoa.junit.HttpAssertions.assert200;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Map;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;

import io.inoa.fleet.registry.gateway.AuthApiClient;
import io.inoa.fleet.registry.management.GatewayDetailVO;
import io.inoa.fleet.registry.management.GatewayUpdateVO;
import io.inoa.fleet.registry.management.GatewaysApiClient;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class RegistryClient {

	private final KeycloakClient keycloak;

	private final GatewaysApiClient gatewaysClient;
	private final AuthApiClient authClient;

	public JWKSet getJwkSet() {
		var body = (Map<String, Object>) assert200(() -> authClient.getKeys(), "failed to retrieve registry jwks");
		return assertDoesNotThrow(() -> JWKSet.parse(body), "failed to parse registry jwks: " + body);
	}

	public GatewayDetailVO findGateway(UUID gatewayId) {
		return assert200(() -> gatewaysClient.findGateway(keycloak.admin(), gatewayId), "faild to get gateway");
	}

	public GatewayDetailVO update(UUID gatewayId, GatewayUpdateVO vo) {
		return assert200(() -> gatewaysClient.updateGateway(keycloak.admin(), gatewayId, vo),
				"faild to update gateway");
	}
}