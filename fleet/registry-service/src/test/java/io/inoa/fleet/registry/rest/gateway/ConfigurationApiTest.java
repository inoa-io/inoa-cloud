package io.inoa.fleet.registry.rest.gateway;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.AbstractTest;
import io.inoa.fleet.registry.rest.management.ConfigurationTypeVO;
import jakarta.inject.Inject;

/**
 * Test for {@link ConfigurationApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("gateway: configuration")
public class ConfigurationApiTest extends AbstractTest implements ConfigurationApiTestSpec {

	@Inject
	ConfigurationApiTestClient client;

	@DisplayName("getConfiguration(200): without configuration")
	@Test
	@Override
	public void getConfiguration200() {

		// create config for another tenant

		var otherTenant = data.tenant();
		var otherDefinition = data.definition(otherTenant, "other", ConfigurationTypeVO.STRING);
		data.configuration(otherDefinition, "meh");

		// create gateway and get config

		var auth = bearer(data.gateway());
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

		var auth = bearer(gateway);
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
}