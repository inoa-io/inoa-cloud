package io.kokuwa.fleet.registry.rest.management;

import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.AbstractTest;

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

	@DisplayName("deleteConfigurationDefinition(204) with values at tenant")
	@Disabled
	@Test
	public void deleteConfigurationDefinition204Tenant() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		assert204(() -> client.deleteConfigurationDefinition(auth(tenant), definition.getKey()));
		assertEquals(0, data.countDefinitions(tenant), "not deleted");
	}

	@DisplayName("deleteConfigurationDefinition(204): with values at group")
	@Disabled
	@Test
	public void deleteConfigurationDefinition204Group() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
		assert204(() -> client.deleteConfigurationDefinition(auth(tenant), definition.getKey()));
		assertEquals(0, data.countDefinitions(tenant), "not deleted");
	}

	@DisplayName("deleteConfigurationDefinition(204): with values at gateway")
	@Disabled
	@Test
	public void deleteConfigurationDefinition204Gateway() {
		var tenant = data.tenant();
		var definition = data.definition(tenant, UUID.randomUUID().toString(), ConfigurationTypeVO.STRING);
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
}
