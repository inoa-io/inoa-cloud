package io.inoa.fleet.registry.rest.management;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.AbstractTest;
import io.inoa.fleet.registry.domain.ConfigurationDefinition;

/**
 * Test for {@link ConfigurationApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: configuration")
public class ConfigurationApiTest extends AbstractTest implements ConfigurationApiTestSpec {

	@Inject
	ConfigurationApiTestClient client;

	@DisplayName("findConfigurationDefinitions(200): success")
	@Test
	@Override
	public void findConfigurationDefinitions200() {
		var tenant = data.tenant();
		var otherTenant = data.tenant();
		for (var type : ConfigurationTypeVO.values()) {
			data.definition(tenant, type.toString(), type, d -> d.setDescription("1"));
			data.definition(otherTenant, type.toString(), type, d -> d.setDescription("2"));
		}
		var definitsions = assert200(() -> client.findConfigurationDefinitions(auth(tenant)));
		assertEquals(4, definitsions.size(), "size");
		assertTrue(definitsions.stream().allMatch(d -> d.getDescription().equals("1")), "tenant");
		assertEquals(ConfigurationTypeVO.BOOLEAN, definitsions.get(0).getType(), "order");
		assertEquals(ConfigurationTypeVO.INTEGER, definitsions.get(1).getType(), "order");
		assertEquals(ConfigurationTypeVO.STRING, definitsions.get(2).getType(), "order");
		assertEquals(ConfigurationTypeVO.URL, definitsions.get(3).getType(), "order");
	}

	@DisplayName("findConfigurationDefinitions(401): no token")
	@Test
	@Override
	public void findConfigurationDefinitions401() {
		assert401(() -> client.findConfigurationDefinitions(null));
	}

	@DisplayName("createConfigurationDefinition(201): url")
	@Test
	@Override
	public void createConfigurationDefinition201() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionUrlVO().setKey(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): boolean")
	@Test
	public void createConfigurationDefinition201Boolean() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionBooleanVO().setKey(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): string with mandatory properties")
	@Test
	public void createConfigurationDefinition201StringWithMandatoryProperties() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().setKey(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): string with optional properties")
	@Test
	public void createConfigurationDefinition201StringWithOptionalProperties() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO()
				.setMinLength(10)
				.setMaxLength(100)
				.setPattern("^a*$")
				.setKey(UUID.randomUUID().toString())
				.setDescription(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): integer with mandatory properties")
	@Test
	public void createConfigurationDefinition201IntegerWithMandatoryProperties() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionIntegerVO().setKey(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): integer with optional properties")
	@Test
	public void createConfigurationDefinition201IntegerWithOptionalProperties() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionIntegerVO()
				.setMinimum(-56000)
				.setMaximum(-14)
				.setKey(UUID.randomUUID().toString())
				.setDescription(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(400) is beanvalidation active")
	@Test
	@Override
	public void createConfigurationDefinition400() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().setMinLength(-1).setKey(UUID.randomUUID().toString());
		assert400(() -> client.createConfigurationDefinition(auth(tenant), vo.getKey(), vo));
		assertEquals(0, data.countDefinitions(tenant), "created");
	}

	@DisplayName("createConfigurationDefinition(400): is custom validator active")
	@Test
	public void createProject400CustomValidator() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().setPattern("{a}").setKey(UUID.randomUUID().toString());
		assert400(() -> client.createConfigurationDefinition(auth(tenant), vo.getKey(), vo));
		assertEquals(0, data.countDefinitions(tenant), "created");
	}

	@DisplayName("createConfigurationDefinition(400): key in path is different from model")
	@Test
	public void createConfigurationDefinition400Key() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().setKey(UUID.randomUUID().toString());
		assert400(() -> client.createConfigurationDefinition(auth(tenant), UUID.randomUUID().toString(), vo));
		assertEquals(0, data.countDefinitions(tenant), "created");
	}

	@DisplayName("createConfigurationDefinition(401): no token")
	@Test
	@Override
	public void createConfigurationDefinition401() {
		assert401(() -> client.createConfigurationDefinition(null, null));
	}

	@DisplayName("createConfigurationDefinition(409): key already in use")
	@Test
	@Override
	public void createConfigurationDefinition409() {
		var tenant = data.tenant();
		var key = UUID.randomUUID().toString();
		var vo = new ConfigurationDefinitionStringVO().setKey(key);
		data.definition(tenant, key, ConfigurationTypeVO.STRING);
		assert409(() -> client.createConfigurationDefinition(auth(tenant), key, vo));
		assertEquals(1, data.countDefinitions(tenant), "created");
	}

	@DisplayName("deleteConfigurationDefinition(204) without values")
	@Test
	@Override
	public void deleteConfigurationDefinition204() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		assert204(() -> client.deleteConfigurationDefinition(auth(tenant), definition.getKey()));
		assertEquals(0, data.countDefinitions(tenant), "not deleted");
	}

	@DisplayName("deleteConfigurationDefinition(204) with values")
	@Test
	public void deleteConfigurationDefinition204Values() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var gateway = data.gateway(group);
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(definition, UUID.randomUUID().toString());
		data.configuration(group, definition, UUID.randomUUID().toString());
		data.configuration(gateway, definition, UUID.randomUUID().toString());
		assert204(() -> client.deleteConfigurationDefinition(auth(tenant), definition.getKey()));
		assertEquals(0, data.countDefinitions(tenant), "not deleted");
	}

	@DisplayName("deleteConfigurationDefinition(401): no token")
	@Test
	@Override
	public void deleteConfigurationDefinition401() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		assert401(() -> client.deleteConfigurationDefinition(null, definition.getKey()));
		assertEquals(1, data.countDefinitions(tenant), "deleted");
	}

	@DisplayName("deleteConfigurationDefinition(404): not found")
	@Test
	@Override
	public void deleteConfigurationDefinition404() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var message = "Definition not found.";
		assert404(message, () -> client.deleteConfigurationDefinition(auth(tenant), UUID.randomUUID().toString()));
		assert404(message, () -> client.deleteConfigurationDefinition(auth(data.tenant()), definition.getKey()));
		assertEquals(1, data.countDefinitions(tenant), "deleted");
	}

	@DisplayName("findConfigurations(200): success")
	@Test
	@Override
	public void findConfigurations200() {
		var tenant = data.tenant();
		var string = data.configuration(data.definition(tenant, "string", ConfigurationTypeVO.STRING), "abcd");
		var integer = data.configuration(data.definition(tenant, "integer", ConfigurationTypeVO.INTEGER), "5");
		var bool = data.configuration(data.definition(tenant, "boolean", ConfigurationTypeVO.BOOLEAN), "true");
		var url = data.configuration(data.definition(tenant, "url", ConfigurationTypeVO.URL), "http://host");
		var configurations = assert200(() -> client.findConfigurations(auth(tenant)));
		assertEquals(4, configurations.size(), "size");
		assertSorted(configurations);
		assetValue("abcd", string.getDefinition(), configurations);
		assetValue(5, integer.getDefinition(), configurations);
		assetValue(true, bool.getDefinition(), configurations);
		assetValue("http://host", url.getDefinition(), configurations);
	}

	@DisplayName("findConfigurations(401): no token")
	@Test
	@Override
	public void findConfigurations401() {
		assert401(() -> client.findConfigurations(null));
	}

	@DisplayName("setConfiguration(200): insert new value")
	@Test
	@Override
	public void setConfiguration204() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assert204(() -> client.setConfiguration(auth, definition.getKey(), vo));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurations(auth)));
	}

	@DisplayName("setConfiguration(204): update existing value")
	@Test
	public void setConfiguration204Update() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.INTEGER);
		data.configuration(definition, UUID.randomUUID().toString());
		var vo = new ConfigurationSetVO().setValue(1234526);
		var auth = auth(tenant);
		assert204(() -> client.setConfiguration(auth, definition.getKey(), vo));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurations(auth)));
	}

	@DisplayName("setConfiguration(400): is beanvalidation active")
	@Test
	@Override
	public void setConfiguration400() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(null);
		var auth = auth(tenant);
		assert400(() -> client.setConfiguration(auth, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurations(auth)).isEmpty());
	}

	@DisplayName("setConfiguration(400): is custom validator active")
	@Test
	public void setConfiguration400Custom() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(1234526);
		var auth = auth(tenant);
		assert400(() -> client.setConfiguration(auth, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurations(auth)).isEmpty());
	}

	@DisplayName("setConfiguration(401): no token")
	@Test
	@Override
	public void setConfiguration401() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		assert401(() -> client.setConfiguration(null, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurations(auth(tenant))).isEmpty());
	}

	@DisplayName("setConfiguration(404): not found")
	@Test
	@Override
	public void setConfiguration404() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		var message = "Definition not found.";
		assert404(message, () -> client.setConfiguration(auth(tenant), UUID.randomUUID().toString(), vo));
		assert404(message, () -> client.setConfiguration(auth(data.tenant()), definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurations(auth(tenant))).isEmpty());
	}

	@DisplayName("resetConfiguration(204): success")
	@Test
	@Override
	public void resetConfiguration204() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(definition, UUID.randomUUID().toString());
		assert204(() -> client.resetConfiguration(auth(tenant), definition.getKey()));
		assertTrue(assert200(() -> client.findConfigurations(auth(tenant))).isEmpty());
	}

	@DisplayName("resetConfiguration(401): no token")
	@Test
	@Override
	public void resetConfiguration401() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(definition, UUID.randomUUID().toString());
		assert401(() -> client.resetConfiguration(null, definition.getKey()));
		assertFalse(assert200(() -> client.findConfigurations(auth(tenant))).isEmpty());
	}

	@DisplayName("findConfigurationsByGateway(200): success")
	@Test
	@Override
	public void findConfigurationsByGateway200() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var string = data.configuration(gateway, data.definition(tenant, "string", ConfigurationTypeVO.STRING), "abcd");
		var integer = data.configuration(gateway, data.definition(tenant, "integer", ConfigurationTypeVO.INTEGER), "5");
		var bool = data.configuration(gateway, data.definition(tenant, "boolean", ConfigurationTypeVO.BOOLEAN), "true");
		var url = data.configuration(gateway, data.definition(tenant, "url", ConfigurationTypeVO.URL), "http://host");
		var configurations = assert200(() -> client.findConfigurationsByGateway(auth(tenant), gateway.getGatewayId()));
		assertEquals(4, configurations.size(), "size");
		assertSorted(configurations);
		assetValue("abcd", string.getDefinition(), configurations);
		assetValue(5, integer.getDefinition(), configurations);
		assetValue(true, bool.getDefinition(), configurations);
		assetValue("http://host", url.getDefinition(), configurations);
	}

	@DisplayName("findConfigurationsByGateway(401): success")
	@Test
	@Override
	public void findConfigurationsByGateway401() {
		assert401(() -> client.findConfigurationsByGateway(null, UUID.randomUUID()));
	}

	@DisplayName("findConfigurationsByGateway(404): not found")
	@Test
	@Override
	public void findConfigurationsByGateway404() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		assert404("Gateway not found.", () -> client.findConfigurationsByGateway(auth(tenant), UUID.randomUUID()));
		assert404("Gateway not found.", () -> client.findConfigurationsByGateway(auth(data.tenant()), gatewayId));
	}

	@DisplayName("findConfigurationsByGroup(200): success")
	@Test
	@Override
	public void findConfigurationsByGroup200() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var string = data.configuration(group, data.definition(tenant, "string", ConfigurationTypeVO.STRING), "abcd");
		var integer = data.configuration(group, data.definition(tenant, "integer", ConfigurationTypeVO.INTEGER), "5");
		var bool = data.configuration(group, data.definition(tenant, "boolean", ConfigurationTypeVO.BOOLEAN), "true");
		var url = data.configuration(group, data.definition(tenant, "url", ConfigurationTypeVO.URL), "http://host");
		var configurations = assert200(() -> client.findConfigurationsByGroup(auth(tenant), group.getGroupId()));
		assertEquals(4, configurations.size(), "size");
		assertSorted(configurations);
		assetValue("abcd", string.getDefinition(), configurations);
		assetValue(5, integer.getDefinition(), configurations);
		assetValue(true, bool.getDefinition(), configurations);
		assetValue("http://host", url.getDefinition(), configurations);
	}

	@DisplayName("findConfigurationsByGroup(401): no token")
	@Test
	@Override
	public void findConfigurationsByGroup401() {
		assert401(() -> client.findConfigurationsByGroup(null, UUID.randomUUID()));
	}

	@DisplayName("findConfigurationsByGroup(404): not found")
	@Test
	@Override
	public void findConfigurationsByGroup404() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		assert404("Group not found.", () -> client.findConfigurationsByGroup(auth(tenant), UUID.randomUUID()));
		assert404("Group not found.", () -> client.findConfigurationsByGroup(auth(data.tenant()), groupId));
	}

	@DisplayName("resetConfigurationByGateway(204): success")
	@Test
	@Override
	public void resetConfigurationByGateway204() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(gateway, definition, UUID.randomUUID().toString());
		var auth = auth(tenant);
		assert204(() -> client.resetConfigurationByGateway(auth, gateway.getGatewayId(), definition.getKey()));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth, gateway.getGatewayId())).isEmpty());
	}

	@DisplayName("resetConfigurationByGateway(401): no token")
	@Test
	@Override
	public void resetConfigurationByGateway401() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var gatewayId = gateway.getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(gateway, definition, UUID.randomUUID().toString());
		assert401(() -> client.resetConfigurationByGateway(null, gatewayId, definition.getKey()));
		assertFalse(assert200(() -> client.findConfigurationsByGateway(auth(tenant), gatewayId)).isEmpty());
	}

	@DisplayName("resetConfigurationByGroup(204): success")
	@Test
	@Override
	public void resetConfigurationByGroup204() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(group, definition, UUID.randomUUID().toString());
		assert204(() -> client.resetConfigurationByGroup(auth(tenant), group.getGroupId(), definition.getKey()));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth(tenant), group.getGroupId())).isEmpty());
	}

	@DisplayName("resetConfigurationByGroup(401): no token")
	@Test
	@Override
	public void resetConfigurationByGroup401() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(group, definition, UUID.randomUUID().toString());
		assert401(() -> client.resetConfigurationByGroup(null, group.getGroupId(), definition.getKey()));
		assertFalse(assert200(() -> client.findConfigurationsByGroup(auth(tenant), group.getGroupId())).isEmpty());
	}

	@DisplayName("setConfigurationByGateway(204): insert new value")
	@Test
	@Override
	public void setConfigurationByGateway204() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.BOOLEAN);
		var vo = new ConfigurationSetVO().setValue(true);
		var auth = auth(tenant);
		assert204(() -> client.setConfigurationByGateway(auth, gatewayId, definition.getKey(), vo));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurationsByGateway(auth, gatewayId)));
	}

	@DisplayName("setConfigurationByGateway(204): update existing value")
	@Test
	public void setConfigurationByGateway204Update() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var gatewayId = gateway.getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(gateway, definition, UUID.randomUUID().toString());
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assert204(() -> client.setConfigurationByGateway(auth, gatewayId, definition.getKey(), vo));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurationsByGateway(auth, gatewayId)));
	}

	@DisplayName("setConfigurationByGateway(400): is beanvalidation active")
	@Test
	@Override
	public void setConfigurationByGateway400() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(null);
		var auth = auth(tenant);
		assert400(() -> client.setConfigurationByGateway(auth, gatewayId, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth, gatewayId)).isEmpty());
	}

	@DisplayName("setConfigurationByGateway(400): is custom validator active")
	@Test
	public void setConfigurationByGateway400Custom() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(1234526);
		var auth = auth(tenant);
		assert400(() -> client.setConfigurationByGateway(auth, gatewayId, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth, gatewayId)).isEmpty());
	}

	@DisplayName("setConfigurationByGateway(401): no token")
	@Test
	@Override
	public void setConfigurationByGateway401() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		assert401(() -> client.setConfigurationByGateway(null, gatewayId, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth(tenant), gatewayId)).isEmpty());
	}

	@DisplayName("setConfigurationByGateway(404): not found")
	@Test
	@Override
	public void setConfigurationByGateway404() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var key = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING).getKey();
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Definition not found.", () -> client.setConfigurationByGateway(auth, gatewayId, "nope", vo));
		assert404("Gateway not found.", () -> client.setConfigurationByGateway(auth, UUID.randomUUID(), key, vo));
		assert404("Gateway not found.", () -> client.setConfigurationByGateway(authOther, gatewayId, key, vo));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth(tenant), gatewayId)).isEmpty());
	}

	@DisplayName("setConfigurationByGroup(204): insert new value")
	@Test
	@Override
	public void setConfigurationByGroup204() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.BOOLEAN);
		var vo = new ConfigurationSetVO().setValue(true);
		var auth = auth(tenant);
		assert204(() -> client.setConfigurationByGroup(auth, groupId, definition.getKey(), vo));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurationsByGroup(auth, groupId)));
	}

	@DisplayName("setConfigurationByGroup(204): update existing value")
	@Test
	public void setConfigurationByGroup204Update() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var groupId = group.getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(group, definition, UUID.randomUUID().toString());
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assert204(() -> client.setConfigurationByGroup(auth, groupId, definition.getKey(), vo));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurationsByGroup(auth, groupId)));
	}

	@DisplayName("setConfigurationByGroup(400): is beanvalidation active")
	@Test
	@Override
	public void setConfigurationByGroup400() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(null);
		var auth = auth(tenant);
		assert400(() -> client.setConfigurationByGroup(auth, groupId, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth, groupId)).isEmpty());
	}

	@DisplayName("setConfigurationByGroup(400): is custom validator active")
	@Test
	public void setConfigurationByGroup00Custom() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(1234526);
		var auth = auth(tenant);
		assert400(() -> client.setConfigurationByGroup(auth, groupId, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth, groupId)).isEmpty());
	}

	@DisplayName("setConfigurationByGroup(401): no token")
	@Test
	@Override
	public void setConfigurationByGroup401() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		assert401(() -> client.setConfigurationByGroup(null, groupId, definition.getKey(), vo));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth(tenant), groupId)).isEmpty());
	}

	@DisplayName("setConfigurationByGroup(404): not found")
	@Test
	@Override
	public void setConfigurationByGroup404() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var key = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING).getKey();
		var vo = new ConfigurationSetVO().setValue(UUID.randomUUID().toString());
		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Definition not found.", () -> client.setConfigurationByGroup(auth, groupId, "nope", vo));
		assert404("Group not found.", () -> client.setConfigurationByGroup(auth, UUID.randomUUID(), key, vo));
		assert404("Group not found.", () -> client.setConfigurationByGroup(authOther, groupId, key, vo));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth(tenant), groupId)).isEmpty());
	}

	@DisplayName("resetConfiguration(404): not found")
	@Test
	@Override
	public void resetConfiguration404() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(definition, UUID.randomUUID().toString());
		assert404("Definition not found.", () -> client.resetConfiguration(auth(tenant), UUID.randomUUID().toString()));
		assert404("Definition not found.", () -> client.resetConfiguration(auth(data.tenant()), definition.getKey()));
		assertFalse(assert200(() -> client.findConfigurations(auth(tenant))).isEmpty());
	}

	@DisplayName("resetConfigurationByGroup(404): not found")
	@Test
	@Override
	public void resetConfigurationByGroup404() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var groupId = group.getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var key = definition.getKey();
		data.configuration(group, definition, UUID.randomUUID().toString());
		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Definition not found.", () -> client.resetConfigurationByGroup(auth, groupId, "nope"));
		assert404("Group not found.", () -> client.resetConfigurationByGroup(auth, UUID.randomUUID(), key));
		assert404("Group not found.", () -> client.resetConfigurationByGroup(authOther, groupId, key));
		assertFalse(assert200(() -> client.findConfigurationsByGroup(auth(tenant), groupId)).isEmpty());
	}

	@DisplayName("resetConfigurationByGateway(404): not found")
	@Test
	@Override
	public void resetConfigurationByGateway404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var gatewayId = gateway.getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var key = definition.getKey();
		data.configuration(gateway, definition, UUID.randomUUID().toString());
		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Definition not found.", () -> client.resetConfigurationByGateway(auth, gatewayId, "nope"));
		assert404("Gateway not found.", () -> client.resetConfigurationByGateway(auth, UUID.randomUUID(), key));
		assert404("Gateway not found.", () -> client.resetConfigurationByGateway(authOther, gatewayId, key));
		assertFalse(assert200(() -> client.findConfigurationsByGateway(auth(tenant), gatewayId)).isEmpty());
	}

	private static void assertSorted(List<ConfigurationVO> configurations) {
		assertSorted(configurations, c -> c.getDefinition().getKey(),
				Comparator.comparing(c -> c.getDefinition().getKey()));
	}

	private static void assetValue(
			Object expected,
			ConfigurationDefinition definition,
			List<ConfigurationVO> configurations) {
		assertEquals(expected, configurations.stream()
				.filter(i -> i.getDefinition().getKey().equals(definition.getKey()))
				.findAny().get().getValue(), "value");
	}
}