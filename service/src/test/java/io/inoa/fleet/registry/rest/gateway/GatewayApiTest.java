package io.inoa.fleet.registry.rest.gateway;

import static io.inoa.HttpAssertions.assert200;
import static io.inoa.HttpAssertions.assert204;
import static io.inoa.HttpAssertions.assert400;
import static io.inoa.HttpAssertions.assert401;
import static io.inoa.HttpAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.hawkbit.api.TargetsApiClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.AbstractUnitTest;
import io.inoa.fleet.registry.auth.GatewayTokenHelper;
import io.inoa.rest.ConfigurationTypeVO;
import io.inoa.rest.CredentialTypeVO;
import io.inoa.rest.CredentialsApiTestClient;
import io.inoa.rest.GatewayApi;
import io.inoa.rest.GatewayApiTestClient;
import io.inoa.rest.GatewayApiTestSpec;
import io.inoa.rest.GatewaysApiTestClient;
import io.inoa.rest.RegisterVO;
import jakarta.inject.Inject;

/**
 * Test for {@link GatewayApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("gateway: configuration")
class GatewayApiTest extends AbstractUnitTest implements GatewayApiTestSpec {

	@Inject
	GatewayApiTestClient client;
	@Inject
	GatewaysApiTestClient gatewaysClient;
	@Inject
	CredentialsApiTestClient credentialsClient;
	@Inject
	TargetsApiClient targetsApiClient;
	@Inject
	GatewayTokenHelper gatewayToken;

	@DisplayName("getConfiguration(200): without configuration")
	@Test
	@Override
	public void getConfiguration200() {

		// create config for another tenant

		var otherTenant = data.tenant();
		var otherDefinition = data.definition(otherTenant, "other", ConfigurationTypeVO.STRING);
		data.configuration(otherDefinition, "meh");

		// create gateway and get config

		var auth = gatewayToken.bearer(data.gateway());
		var actual = assert200(() -> client.getConfiguration(auth));
		var expected = Map.of();
		assertEquals(expected, actual, "configuration");
	}

	@DisplayName("getConfiguration(200): with configuration")
	@Test
	public void getConfiguration200WithConfiguratiuon() {

		// create gateway

		var tenant = data.tenant();
		var group1 = data.group(tenant);
		var group2 = data.group(tenant);
		var gateway = data.gateway(group1);

		// create config

		var definitionBoolean = data.definition(tenant, "boolean", ConfigurationTypeVO.BOOLEAN);
		var definitionString = data.definition(tenant, "string", ConfigurationTypeVO.STRING);
		var definitionInteger = data.definition(tenant, "integer", ConfigurationTypeVO.INTEGER);
		var definitionUrl = data.definition(tenant, "url", ConfigurationTypeVO.URL);
		data.configuration(definitionString, "meh");
		data.configuration(group1, definitionString, "muh");
		data.configuration(group2, definitionUrl, "http://host");
		data.configuration(gateway, definitionString, "mäh");
		data.configuration(gateway, definitionBoolean, "false");
		data.configuration(gateway, definitionInteger, "12345");

		// get config

		var auth = gatewayToken.bearer(gateway);
		var actual = assert200(() -> client.getConfiguration(auth));
		var expected = Map.of("boolean", false, "integer", 12345, "string", "mäh");
		assertEquals(actual, expected, "configuration");
	}

	@DisplayName("getConfiguration(401): no token")
	@Test
	@Override
	public void getConfiguration401() {
		assert401(() -> client.getConfiguration(null));
	}

	@DisplayName("register(200): with psk")
	@Test
	@Override
	public void register204() {

		var tenant = data.tenant("inoa");
		var gatewayId = data.gatewayId();
		var gatewayPsk = UUID.randomUUID().toString();

		var auth = auth(tenant);

		var vo = new RegisterVO().gatewayId(gatewayId).credentialType(CredentialTypeVO.PSK)
				.credentialValue(gatewayPsk.getBytes());
		assert204(() -> client.register(vo));

		var foundGateway = assert200(() -> gatewaysClient.findGateway(auth, vo.getGatewayId(), null));
		assertEquals(vo.getGatewayId(), foundGateway.getGatewayId(), "gatewayId");
		assertNull(foundGateway.getName(), "name");
		assertEquals(false, foundGateway.getEnabled(), "enabled");
		assertEquals(Set.of(), foundGateway.getGroupIds(), "groupIds");
		assertEquals(Map.of(), foundGateway.getProperties(), "properties");
		assertNotNull(foundGateway.getCreated(), "created");
		assertNotNull(foundGateway.getUpdated(), "updated");

		var credentials = assert200(() -> credentialsClient.findCredentials(auth, vo.getGatewayId(), null));
		assertEquals(1, credentials.size(), "credentials: " + credentials);
		assertNotNull(credentials.get(0).getCredentialId(), "credentialId");
		assertNotNull(credentials.get(0).getName(), "name");
		assertEquals(true, credentials.get(0).getEnabled(), "enabled");
		assertEquals(CredentialTypeVO.PSK.name(), credentials.get(0).getType().name(), "type");
		assertArrayEquals(vo.getCredentialValue(), credentials.get(0).getValue(), "value");
		assertNotNull(credentials.get(0).getCreated(), "created");
		assertNotNull(credentials.get(0).getUpdated(), "updated");

		var target = assert200(() -> targetsApiClient.getTarget(gatewayId));
		assertNotNull(target);
		assertEquals(gatewayId, target.getControllerId());
		assertEquals(gatewayId, target.getName());
		assertEquals(gatewayPsk, target.getSecurityToken());
	}

	@DisplayName("register(200): with rsa")
	@Test
	public void register204RSA() {

		var tenant = data.tenant("inoa");
		var auth = auth(tenant);

		var vo = new RegisterVO().gatewayId(data.gatewayId()).credentialType(CredentialTypeVO.RSA)
				.credentialValue(UUID.randomUUID().toString().getBytes());
		assert204(() -> client.register(vo));

		var gateway = assert200(() -> gatewaysClient.findGateway(auth, vo.getGatewayId(), null));
		assertEquals(vo.getGatewayId(), gateway.getGatewayId(), "gatewayId");
		assertNull(gateway.getName(), "name");
		assertEquals(false, gateway.getEnabled(), "enabled");
		assertEquals(Set.of(), gateway.getGroupIds(), "groupIds");
		assertEquals(Map.of(), gateway.getProperties(), "properties");
		assertNotNull(gateway.getCreated(), "created");
		assertNotNull(gateway.getUpdated(), "updated");

		var credentials = assert200(() -> credentialsClient.findCredentials(auth, vo.getGatewayId(), null));
		assertEquals(1, credentials.size(), "credentials: " + credentials);
		assertNotNull(credentials.get(0).getCredentialId(), "credentialId");
		assertNotNull(credentials.get(0).getName(), "name");
		assertEquals(true, credentials.get(0).getEnabled(), "enabled");
		assertEquals(CredentialTypeVO.RSA.name(), credentials.get(0).getType().name(), "type");
		assertArrayEquals(vo.getCredentialValue(), credentials.get(0).getValue(), "value");
		assertNotNull(credentials.get(0).getCreated(), "created");
		assertNotNull(credentials.get(0).getUpdated(), "updated");
	}

	@DisplayName("register(400): is beanvalidation active")
	@Test
	@Override
	public void register400() {
		assert400(() -> client.register(new RegisterVO()));
	}

	@DisplayName("register(400): gatewayId is invalid")
	@Test
	public void register400GatewayIdInvalid() {
		var vo = new RegisterVO().gatewayId("NOPE").credentialType(CredentialTypeVO.PSK)
				.credentialValue(UUID.randomUUID().toString().getBytes());
		var tenant = data.tenant("inoa");
		var error = assert400(() -> client.register(vo));
		assertEquals("GatewayId must match " + tenant.getGatewayIdPattern() + ".", error.getMessage());
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("register(400): rsa key invalid")
	@Disabled("NYI")
	@Test
	public void register400KeyInvalid() {}

	@DisplayName("register(409): gatewayId already registered")
	@Test
	@Override
	public void register409() {
		data.tenant("inoa");
		var vo = new RegisterVO().gatewayId(data.gateway().getGatewayId()).credentialType(CredentialTypeVO.PSK)
				.credentialValue(UUID.randomUUID().toString().getBytes());
		assert409(() -> client.register(vo));
	}
}
