package io.inoa.client;

import static io.inoa.junit.HttpAssertions.assert200;

import io.inoa.fleet.api.GatewaysApiClient;
import io.inoa.fleet.model.GatewayDetailVO;
import io.inoa.fleet.model.GatewayUpdateVO;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class RegistryClient {

	private final KeycloakClient keycloak;

	private final GatewaysApiClient gatewaysClient;

	public GatewayDetailVO findGateway(String gatewayId, String tenantId) {
		return assert200(() -> gatewaysClient.findGateway(keycloak.admin(), gatewayId, tenantId),
				"faild to get gateway");
	}

	public GatewayDetailVO update(String gatewayId, GatewayUpdateVO gatewayUpdateVO, String tenantId) {
		return assert200(() -> gatewaysClient.updateGateway(keycloak.admin(), gatewayId, gatewayUpdateVO, tenantId),
				"faild to update gateway");
	}
}
