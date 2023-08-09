package io.inoa.client;

import static io.inoa.junit.HttpAssertions.assert200;

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

	public GatewayDetailVO findGateway(String gatewayId) {
		return assert200(() -> gatewaysClient.findGateway(keycloak.admin(), gatewayId), "faild to get gateway");
	}

	public GatewayDetailVO update(String gatewayId, GatewayUpdateVO vo) {
		return assert200(() -> gatewaysClient.updateGateway(keycloak.admin(), gatewayId, vo),
				"faild to update gateway");
	}
}
