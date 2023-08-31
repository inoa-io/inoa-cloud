package io.inoa.test.client;

import static io.inoa.test.junit.HttpAssertions.assert200;

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

	public GatewayDetailVO findGateway(String gatewayId) {
		return assert200(() -> gatewaysClient.findGateway(keycloak.admin(), gatewayId, null),
				"faild to get gateway");
	}

	public GatewayDetailVO update(String gatewayId, GatewayUpdateVO gatewayUpdateVO) {
		return assert200(() -> gatewaysClient.updateGateway(keycloak.admin(), gatewayId, gatewayUpdateVO, null),
				"faild to update gateway");
	}
}
