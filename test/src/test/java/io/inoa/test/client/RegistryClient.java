package io.inoa.test.client;

import static io.inoa.test.HttpAssertions.assert200;

import io.inoa.rest.GatewayDetailVO;
import io.inoa.rest.GatewayUpdateVO;
import io.inoa.rest.GatewaysApiTestClient;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class RegistryClient {

	private final KeycloakClient keycloak;
	private final GatewaysApiTestClient gatewaysClient;

	public GatewayDetailVO findGateway(String gatewayId) {
		return assert200(() -> gatewaysClient.findGateway(keycloak.admin(), gatewayId, null),
				"faild to get gateway");
	}

	public GatewayDetailVO update(String gatewayId, GatewayUpdateVO gatewayUpdateVO) {
		return assert200(() -> gatewaysClient.updateGateway(keycloak.admin(), gatewayId, gatewayUpdateVO, null),
				"faild to update gateway");
	}
}
