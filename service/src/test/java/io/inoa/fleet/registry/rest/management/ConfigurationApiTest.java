package io.inoa.fleet.registry.rest.management;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert204;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert404;
import static io.inoa.test.HttpAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.domain.ConfigurationDefinition;
import io.inoa.rest.ConfigurationApiTestClient;
import io.inoa.rest.ConfigurationApiTestSpec;
import io.inoa.rest.ConfigurationDefinitionBooleanVO;
import io.inoa.rest.ConfigurationDefinitionIntegerVO;
import io.inoa.rest.ConfigurationDefinitionStringVO;
import io.inoa.rest.ConfigurationDefinitionUrlVO;
import io.inoa.rest.ConfigurationSetVO;
import io.inoa.rest.ConfigurationTypeVO;
import io.inoa.rest.ConfigurationVO;
import io.inoa.test.AbstractUnitTest;
import jakarta.inject.Inject;

/**
 * Test for {@link ConfigurationController}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: configuration")
public class ConfigurationApiTest extends AbstractUnitTest implements ConfigurationApiTestSpec {

	@Inject ConfigurationApiTestClient client;

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
		var definitions = assert200(() -> client.findConfigurationDefinitions(auth(tenant)));
		assertEquals(4, definitions.size(), "size");
		assertTrue(definitions.stream().allMatch(d -> d.getDescription().equals("1")), "tenant");
		assertEquals(ConfigurationTypeVO.BOOLEAN, definitions.get(0).getType(), "order");
		assertEquals(ConfigurationTypeVO.INTEGER, definitions.get(1).getType(), "order");
		assertEquals(ConfigurationTypeVO.STRING, definitions.get(2).getType(), "order");
		assertEquals(ConfigurationTypeVO.URL, definitions.get(3).getType(), "order");
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
		var vo = new ConfigurationDefinitionUrlVO().key(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo, null)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): boolean")
	@Test
	public void createConfigurationDefinition201Boolean() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionBooleanVO().key(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo, null)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): string with mandatory properties")
	@Test
	public void createConfigurationDefinition201StringWithMandatoryProperties() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().key(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo, null)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): string with optional properties")
	@Test
	public void createConfigurationDefinition201StringWithOptionalProperties() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().minLength(10).maxLength(100).pattern("^a*$")
				.key(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo, null)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): integer with mandatory properties")
	@Test
	public void createConfigurationDefinition201IntegerWithMandatoryProperties() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionIntegerVO().key(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo, null)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(201): integer with optional properties")
	@Test
	public void createConfigurationDefinition201IntegerWithOptionalProperties() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionIntegerVO().minimum(-56000).maximum(-14).key(UUID.randomUUID().toString())
				.description(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assertEquals(vo, assert201(() -> client.createConfigurationDefinition(auth, vo.getKey(), vo, null)));
		assertEquals(List.of(vo), assert200(() -> client.findConfigurationDefinitions(auth)), "vo");
	}

	@DisplayName("createConfigurationDefinition(400) is beanvalidation active")
	@Test
	@Override
	public void createConfigurationDefinition400() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().minLength(-1).key(UUID.randomUUID().toString());
		assert400(() -> client.createConfigurationDefinition(auth(tenant), vo.getKey(), vo, null));
		assertEquals(0, data.countDefinitions(tenant), "created");
	}

	@DisplayName("createConfigurationDefinition(400): is custom validator active")
	@Test
	public void createProject400CustomValidator() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().pattern("{a}").key(UUID.randomUUID().toString());
		assert400(() -> client.createConfigurationDefinition(auth(tenant), vo.getKey(), vo, null));
		assertEquals(0, data.countDefinitions(tenant), "created");
	}

	@DisplayName("createConfigurationDefinition(400): key in path is different from model")
	@Test
	public void createConfigurationDefinition400Key() {
		var tenant = data.tenant();
		var vo = new ConfigurationDefinitionStringVO().key(UUID.randomUUID().toString());
		assert400(() -> client.createConfigurationDefinition(auth(tenant), UUID.randomUUID().toString(), vo, null));
		assertEquals(0, data.countDefinitions(tenant), "created");
	}

	@DisplayName("createConfigurationDefinition(401): no token")
	@Test
	@Override
	public void createConfigurationDefinition401() {
		var key = UUID.randomUUID().toString();
		var vo = new ConfigurationDefinitionStringVO().key(key);
		assert401(() -> client.createConfigurationDefinition(null, key, vo, null));
	}

	@DisplayName("createConfigurationDefinition(409): key already in use")
	@Test
	@Override
	public void createConfigurationDefinition409() {
		var tenant = data.tenant();
		var key = UUID.randomUUID().toString();
		var vo = new ConfigurationDefinitionStringVO().key(key);
		data.definition(tenant, key, ConfigurationTypeVO.STRING);
		assert409(() -> client.createConfigurationDefinition(auth(tenant), key, vo, null));
		assertEquals(1, data.countDefinitions(tenant), "created");
	}

	@DisplayName("deleteConfigurationDefinition(204) without values")
	@Test
	@Override
	public void deleteConfigurationDefinition204() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		assert204(() -> client.deleteConfigurationDefinition(auth(tenant), definition.getKey(), null));
		assertEquals(0, data.countDefinitions(tenant), "not deleted");
	}

	@DisplayName("deleteConfigurationDefinition(400) ambiguous tenant")
	@Test
	@Override
	public void deleteConfigurationDefinition400() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var definition = data.definition(tenant1, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		assert400(() -> client.deleteConfigurationDefinition(auth(tenant1, tenant2), definition.getKey(), null));
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
		assert204(() -> client.deleteConfigurationDefinition(auth(tenant), definition.getKey(), null));
		assertEquals(0, data.countDefinitions(tenant), "not deleted");
	}

	@DisplayName("deleteConfigurationDefinition(401): no token")
	@Test
	@Override
	public void deleteConfigurationDefinition401() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		assert401(() -> client.deleteConfigurationDefinition(null, definition.getKey(), null));
		assertEquals(1, data.countDefinitions(tenant), "deleted");
	}

	@DisplayName("deleteConfigurationDefinition(404): not found")
	@Test
	@Override
	public void deleteConfigurationDefinition404() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var message = "Definition not found.";
		assert404(message,
				() -> client.deleteConfigurationDefinition(auth(tenant), UUID.randomUUID().toString(), null));
		assert404(message, () -> client.deleteConfigurationDefinition(auth(data.tenant()), definition.getKey(), null));
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
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assert204(() -> client.setConfiguration(auth, definition.getKey(), vo, null));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurations(auth)));
	}

	@DisplayName("setConfiguration(204): update existing value")
	@Test
	public void setConfiguration204Update() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.INTEGER);
		data.configuration(definition, UUID.randomUUID().toString());
		var vo = new ConfigurationSetVO().value(1234526);
		var auth = auth(tenant);
		assert204(() -> client.setConfiguration(auth, definition.getKey(), vo, null));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurations(auth)));
	}

	@DisplayName("setConfiguration(400): is beanvalidation active")
	@Test
	@Override
	public void setConfiguration400() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(null);
		var auth = auth(tenant);
		assert400(() -> client.setConfiguration(auth, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurations(auth)).isEmpty());
	}

	@DisplayName("setConfiguration(400): is custom validator active")
	@Test
	public void setConfiguration400Custom() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(1234526);
		var auth = auth(tenant);
		assert400(() -> client.setConfiguration(auth, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurations(auth)).isEmpty());
	}

	@DisplayName("setConfiguration(401): no token")
	@Test
	@Override
	public void setConfiguration401() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		assert401(() -> client.setConfiguration(null, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurations(auth(tenant))).isEmpty());
	}

	@DisplayName("setConfiguration(404): not found")
	@Test
	@Override
	public void setConfiguration404() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		var message = "Definition not found.";
		assert404(message, () -> client.setConfiguration(auth(tenant), UUID.randomUUID().toString(), vo, null));
		assert404(message, () -> client.setConfiguration(auth(data.tenant()), definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurations(auth(tenant))).isEmpty());
	}

	@DisplayName("resetConfiguration(204): success")
	@Test
	@Override
	public void resetConfiguration204() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(definition, UUID.randomUUID().toString());
		assert204(() -> client.resetConfiguration(auth(tenant), definition.getKey(), null));
		assertTrue(assert200(() -> client.findConfigurations(auth(tenant))).isEmpty());
	}

	@DisplayName("resetConfiguration(400): ambiguous tenant")
	@Test
	@Override
	public void resetConfiguration400() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var definition = data.definition(tenant1, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(definition, UUID.randomUUID().toString());
		assert400(() -> client.resetConfiguration(auth(tenant1, tenant2), definition.getKey(), null));
	}

	@DisplayName("resetConfiguration(401): no token")
	@Test
	@Override
	public void resetConfiguration401() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(definition, UUID.randomUUID().toString());
		assert401(() -> client.resetConfiguration(null, definition.getKey(), null));
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
		assert401(() -> client.findConfigurationsByGateway(null, data.gatewayId()));
	}

	@DisplayName("findConfigurationsByGateway(404): not found")
	@Test
	@Override
	public void findConfigurationsByGateway404() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		assert404("Gateway not found.", () -> client.findConfigurationsByGateway(auth(tenant), data.gatewayId()));
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
		var configurations = assert200(() -> client.findConfigurationsByGroup(auth(tenant), group.getGroupId(), null));
		assertEquals(4, configurations.size(), "size");
		assertSorted(configurations);
		assetValue("abcd", string.getDefinition(), configurations);
		assetValue(5, integer.getDefinition(), configurations);
		assetValue(true, bool.getDefinition(), configurations);
		assetValue("http://host", url.getDefinition(), configurations);
	}

	@DisplayName("findConfigurationsByGroup(400): ambiguous tenant")
	@Test
	@Override
	public void findConfigurationsByGroup400() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var group = data.group(tenant1);
		assert400(() -> client.findConfigurationsByGroup(auth(tenant1, tenant2), group.getGroupId(), null));
	}

	@DisplayName("findConfigurationsByGroup(401): no token")
	@Test
	@Override
	public void findConfigurationsByGroup401() {
		assert401(() -> client.findConfigurationsByGroup(null, UUID.randomUUID(), null));
	}

	@DisplayName("findConfigurationsByGroup(404): not found")
	@Test
	@Override
	public void findConfigurationsByGroup404() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		assert404("Group not found.", () -> client.findConfigurationsByGroup(auth(tenant), UUID.randomUUID(), null));
		assert404("Group not found.", () -> client.findConfigurationsByGroup(auth(data.tenant()), groupId, null));
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
		assert204(() -> client.resetConfigurationByGroup(auth(tenant), group.getGroupId(), definition.getKey(), null));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth(tenant), group.getGroupId(), null)).isEmpty());
	}

	@DisplayName("resetConfigurationByGroup(400): ambiguous tenant")
	@Test
	@Override
	public void resetConfigurationByGroup400() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var group = data.group(tenant1);
		var definition = data.definition(tenant1, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(group, definition, UUID.randomUUID().toString());
		assert400(() -> client.resetConfigurationByGroup(auth(tenant1, tenant2), group.getGroupId(),
				definition.getKey(), null));
	}

	@DisplayName("resetConfigurationByGroup(401): no token")
	@Test
	@Override
	public void resetConfigurationByGroup401() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(group, definition, UUID.randomUUID().toString());
		assert401(() -> client.resetConfigurationByGroup(null, group.getGroupId(), definition.getKey(), null));
		assertFalse(
				assert200(() -> client.findConfigurationsByGroup(auth(tenant), group.getGroupId(), null)).isEmpty());
	}

	@DisplayName("setConfigurationByGateway(204): insert new value")
	@Test
	@Override
	public void setConfigurationByGateway204() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.BOOLEAN);
		var vo = new ConfigurationSetVO().value(true);
		var auth = auth(tenant);
		assert204(() -> client.setConfigurationByGateway(auth, gatewayId, definition.getKey(), vo, null));
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
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assert204(() -> client.setConfigurationByGateway(auth, gatewayId, definition.getKey(), vo, null));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurationsByGateway(auth, gatewayId)));
	}

	@DisplayName("setConfigurationByGateway(400): is beanvalidation active")
	@Test
	@Override
	public void setConfigurationByGateway400() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(null);
		var auth = auth(tenant);
		assert400(() -> client.setConfigurationByGateway(auth, gatewayId, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth, gatewayId)).isEmpty());
	}

	@DisplayName("setConfigurationByGateway(400): is custom validator active")
	@Test
	public void setConfigurationByGateway400Custom() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(1234526);
		var auth = auth(tenant);
		assert400(() -> client.setConfigurationByGateway(auth, gatewayId, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth, gatewayId)).isEmpty());
	}

	@DisplayName("setConfigurationByGateway(401): no token")
	@Test
	@Override
	public void setConfigurationByGateway401() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		assert401(() -> client.setConfigurationByGateway(null, gatewayId, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth(tenant), gatewayId)).isEmpty());
	}

	@DisplayName("setConfigurationByGateway(404): not found")
	@Test
	@Override
	public void setConfigurationByGateway404() {
		var tenant = data.tenant();
		var gatewayId = data.gateway(tenant).getGatewayId();
		var key = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING).getKey();
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Definition not found.", () -> client.setConfigurationByGateway(auth, gatewayId, "nope", vo, null));
		assert404("Gateway not found.", () -> client.setConfigurationByGateway(auth, data.gatewayId(), key, vo, null));
		assert404("Gateway not found.", () -> client.setConfigurationByGateway(authOther, gatewayId, key, vo, null));
		assertTrue(assert200(() -> client.findConfigurationsByGateway(auth(tenant), gatewayId)).isEmpty());
	}

	@DisplayName("setConfigurationByGroup(204): insert new value")
	@Test
	@Override
	public void setConfigurationByGroup204() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.BOOLEAN);
		var vo = new ConfigurationSetVO().value(true);
		var auth = auth(tenant);
		assert204(() -> client.setConfigurationByGroup(auth, groupId, definition.getKey(), vo, null));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurationsByGroup(auth, groupId, null)));
	}

	@DisplayName("setConfigurationByGroup(204): update existing value")
	@Test
	public void setConfigurationByGroup204Update() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var groupId = group.getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(group, definition, UUID.randomUUID().toString());
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		var auth = auth(tenant);
		assert204(() -> client.setConfigurationByGroup(auth, groupId, definition.getKey(), vo, null));
		assetValue(vo.getValue(), definition, assert200(() -> client.findConfigurationsByGroup(auth, groupId, null)));
	}

	@DisplayName("setConfigurationByGroup(400): is beanvalidation active")
	@Test
	@Override
	public void setConfigurationByGroup400() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(null);
		var auth = auth(tenant);
		assert400(() -> client.setConfigurationByGroup(auth, groupId, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth, groupId, null)).isEmpty());
	}

	@DisplayName("setConfigurationByGroup(400): is custom validator active")
	@Test
	public void setConfigurationByGroup00Custom() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(1234526);
		var auth = auth(tenant);
		assert400(() -> client.setConfigurationByGroup(auth, groupId, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth, groupId, null)).isEmpty());
	}

	@DisplayName("setConfigurationByGroup(401): no token")
	@Test
	@Override
	public void setConfigurationByGroup401() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		assert401(() -> client.setConfigurationByGroup(null, groupId, definition.getKey(), vo, null));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth(tenant), groupId, null)).isEmpty());
	}

	@DisplayName("setConfigurationByGroup(404): not found")
	@Test
	@Override
	public void setConfigurationByGroup404() {
		var tenant = data.tenant();
		var groupId = data.group(tenant).getGroupId();
		var key = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING).getKey();
		var vo = new ConfigurationSetVO().value(UUID.randomUUID().toString());
		var auth = auth(tenant);
		var authOther = auth(data.tenant());
		assert404("Definition not found.", () -> client.setConfigurationByGroup(auth, groupId, "nope", vo, null));
		assert404("Group not found.", () -> client.setConfigurationByGroup(auth, UUID.randomUUID(), key, vo, null));
		assert404("Group not found.", () -> client.setConfigurationByGroup(authOther, groupId, key, vo, null));
		assertTrue(assert200(() -> client.findConfigurationsByGroup(auth(tenant), groupId, null)).isEmpty());
	}

	@DisplayName("resetConfiguration(404): not found")
	@Test
	@Override
	public void resetConfiguration404() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		data.configuration(definition, UUID.randomUUID().toString());
		assert404("Definition not found.",
				() -> client.resetConfiguration(auth(tenant), UUID.randomUUID().toString(), null));
		assert404("Definition not found.",
				() -> client.resetConfiguration(auth(data.tenant()), definition.getKey(), null));
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
		assert404("Definition not found.", () -> client.resetConfigurationByGroup(auth, groupId, "nope", null));
		assert404("Group not found.", () -> client.resetConfigurationByGroup(auth, UUID.randomUUID(), key, null));
		assert404("Group not found.", () -> client.resetConfigurationByGroup(authOther, groupId, key, null));
		assertFalse(assert200(() -> client.findConfigurationsByGroup(auth(tenant), groupId, null)).isEmpty());
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
		assert404("Gateway not found.", () -> client.resetConfigurationByGateway(auth, data.gatewayId(), key));
		assert404("Gateway not found.", () -> client.resetConfigurationByGateway(authOther, gatewayId, key));
		assertFalse(assert200(() -> client.findConfigurationsByGateway(auth(tenant), gatewayId)).isEmpty());
	}

	private static void assertSorted(List<ConfigurationVO> configurations) {
		assertSorted(configurations, c -> c.getDefinition().getKey(),
				Comparator.comparing(c -> c.getDefinition().getKey()));
	}

	private static void assetValue(Object expected, ConfigurationDefinition definition,
			List<ConfigurationVO> configurations) {
		assertEquals(expected, configurations.stream()
				.filter(i -> i.getDefinition().getKey().equals(definition.getKey())).findAny().get().getValue(),
				"value");
	}
}
