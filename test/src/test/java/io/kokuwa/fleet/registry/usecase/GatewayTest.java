package io.kokuwa.fleet.registry.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.infrastructure.ComposeTest;
import io.kokuwa.fleet.registry.rest.gateway.AuthApiTestClient;
import io.kokuwa.fleet.registry.rest.gateway.PropertiesApiTestClient;
import io.kokuwa.fleet.registry.rest.management.CredentialCreateVO;
import io.kokuwa.fleet.registry.rest.management.CredentialTypeVO;
import io.kokuwa.fleet.registry.rest.management.CredentialsApiTestClient;
import io.kokuwa.fleet.registry.rest.management.GatewayCreateVO;
import io.kokuwa.fleet.registry.rest.management.GatewaysApiTestClient;
import io.kokuwa.fleet.registry.rest.management.SecretCreatePSKVO;
import io.kokuwa.fleet.registry.rest.management.TenantCreateVO;
import io.kokuwa.fleet.registry.rest.management.TenantsApiTestClient;
import io.micronaut.http.HttpHeaderValues;

/**
 * Test usecase: create tenant with gateway and token handling.
 *
 * @author Stephan Schnabel
 */
@DisplayName("usecase")
public class GatewayTest extends ComposeTest {

	@Inject
	TenantsApiTestClient tenantsClient;
	@Inject
	GatewaysApiTestClient gatewaysClient;
	@Inject
	CredentialsApiTestClient credentialsClient;
	@Inject
	AuthApiTestClient authClient;
	@Inject
	PropertiesApiTestClient propertiesClient;

	@DisplayName("create tenant/gateway, authenticate, set properties")
	@Test
	void gateway() {

		// create tenant with gateway

		var tenantId = assert201(() -> tenantsClient.createTenant(auth(), new TenantCreateVO()
				.setName(UUID.randomUUID().toString().substring(0, 30)))).getTenantId();
		var gatewayId = assert201(() -> gatewaysClient.createGateway(auth(tenantId), new GatewayCreateVO()
				.setName("device-1"))).getGatewayId();
		var secret = UUID.randomUUID().toString();
		assert201(() -> credentialsClient.createCredential(auth(tenantId), gatewayId, new CredentialCreateVO()
				.setAuthId("registry")
				.setType(CredentialTypeVO.PSK)
				.setSecrets(List.of(new SecretCreatePSKVO().setSecret(secret.getBytes())))));

		// get token

		var response = assert200(() -> authClient.getToken(GRANT_TYPE, gatewayToken(gatewayId, secret)));
		var gatewayToken = HttpHeaderValues.AUTHORIZATION_PREFIX_BEARER + " " + response.getAccessToken();

		// set properties

		var gatewayProperties = Map.of("aa", "1", "bb", "2");
		assert200(() -> propertiesClient.setProperties(gatewayToken, gatewayProperties));

		// get properties

		var gateway = assert200(() -> gatewaysClient.findGateway(auth(tenantId), gatewayId));
		assertEquals(gatewayProperties, gateway.getProperties());
	}
}
