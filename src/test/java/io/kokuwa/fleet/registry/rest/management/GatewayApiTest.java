package io.kokuwa.fleet.registry.rest.management;

import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert404;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.AbstractTest;
import io.kokuwa.fleet.registry.domain.Gateway;

/**
 * Test for {@link GatewayApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: gateway")
public class GatewayApiTest extends AbstractTest implements GatewayApiTestSpec {

	@Inject
	GatewayApiTestClient client;

	@DisplayName("getGateways(200): success")
	@Test
	@Override
	public void getGateways200() {

		// create gateways

		var tenant1 = data.tenant();
		var tenant2 = data.tenant();
		var gateway1 = data.gateway(tenant1, "abc2");
		var gateway2 = data.gateway(tenant1, "abc1");
		var gateway3 = data.gateway(tenant2, "aaa");

		// test helper

		var bearer = bearer();
		BiFunction<UUID, UUID, List<UUID>> filter = (t, g) -> assert200(() -> client.getGateways(bearer, t, g)).stream()
				.map(GatewayVO::getGatewayId)
				.collect(Collectors.toList());
		Function<List<Gateway>, List<UUID>> uuids = gateways -> gateways.stream()
				.map(Gateway::getGatewayId)
				.collect(Collectors.toList());

		// execute

		assertEquals(uuids.apply(List.of(gateway2, gateway1)), filter.apply(tenant1.getTenantId(), null), "tenant1");
		assertEquals(uuids.apply(List.of(gateway3)), filter.apply(tenant2.getTenantId(), null), "tenant2");
	}

	@DisplayName("getGateways(401): no token")
	@Test
	@Override
	public void getGateways401() {
		assert401(() -> client.getGateways(null, UUID.randomUUID(), null));
	}

	@DisplayName("getGateways(404): tenant not found")
	@Test
	@Override
	public void getGateways404() {
		assert404(() -> client.getGateways(bearer(), UUID.randomUUID(), UUID.randomUUID()));
	}

	@DisplayName("getGateway(200): without group/properties")
	@Test
	@Override
	public void getGateway200() {

		var tenant = data.tenant();
		var expected = data.gateway(tenant);
		var actual = assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), expected.getGatewayId()));

		assertEquals(expected.getGatewayId(), actual.getGatewayId(), "gatewayId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(Set.of(), actual.getGroupIds(), "groupIds");
		assertEquals(Map.of(), actual.getProperties(), "properties");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("getGateway(200): with group/properties")
	@Test
	public void getGateway200WithGroup() {

		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var expected = data.gateway(tenant, List.of(group), expectedProperties);
		var actual = assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), expected.getGatewayId()));

		assertEquals(expected.getGatewayId(), actual.getGatewayId(), "gatewayId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(Set.of(group.getGroupId()), actual.getGroupIds(), "groupIds");
		assertEquals(expectedProperties, actual.getProperties(), "properties");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("getGateway(401): no token")
	@Test
	@Override
	public void getGateway401() {
		assert401(() -> client.getGateway(null, UUID.randomUUID(), UUID.randomUUID()));
	}

	@DisplayName("getGateway(404): not found")
	@Test
	@Override
	public void getGateway404() {
		assert404(() -> client.getGateway(bearer(), UUID.randomUUID(), UUID.randomUUID()));
	}

	@DisplayName("createGateway(201): with mandatory properties")
	@Test
	@Override
	public void createGateway201() {

		var tenant = data.tenant();
		var vo = new GatewayCreateVO()
				.setName(data.gatewayName())
				.setEnabled(null)
				.setGroupIds(null);
		var created = assert201(() -> client.createGateway(bearer(), tenant.getTenantId(), vo));

		assertNotNull(created.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(true, created.getEnabled(), "enabled");
		assertEquals(Set.of(), created.getGroupIds(), "groupIds");
		assertEquals(Map.of(), created.getProperties(), "properties");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created,
				assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), created.getGatewayId())), "vo");
	}

	@DisplayName("createGateway(201): with optional properties")
	@Test
	public void createGateway201All() {

		var tenant = data.tenant();
		var group1 = data.group(tenant);
		var group2 = data.group(tenant);
		var vo = new GatewayCreateVO()
				.setName(data.gatewayName())
				.setEnabled(false)
				.setGroupIds(Set.of(group1.getGroupId(), group2.getGroupId()));
		var created = assert201(() -> client.createGateway(bearer(), tenant.getTenantId(), vo));

		assertNotNull(created.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertEquals(Set.of(group1.getGroupId(), group2.getGroupId()), created.getGroupIds(), "groupIds");
		assertEquals(Map.of(), created.getProperties(), "properties");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created,
				assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), created.getGatewayId())), "vo");
	}

	@DisplayName("createGateway(400): check bean validation")
	@Test
	@Override
	public void createGateway400() {
		var vo = new GatewayCreateVO().setName("");
		assert400(() -> client.createGateway(bearer(), UUID.randomUUID(), vo));
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(400): group not exists")
	@Test
	public void createGateway400GroupNotExists() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var notExistingGroupUuid = UUID.randomUUID();
		var vo = new GatewayCreateVO()
				.setName(data.tenantName())
				.setGroupIds(Set.of(group.getGroupId(), notExistingGroupUuid));
		var error = assert400(() -> client.createGateway(bearer(), tenant.getTenantId(), vo));
		assertEquals("Group " + notExistingGroupUuid + " not found.", error.getMessage());
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(400): group from other tenant")
	@Test
	public void createGateway400GroupFromOtherTenant() {
		var groupUuidFromOtherTenant = data.group(data.tenant()).getGroupId();
		var tenant = data.tenant();
		var vo = new GatewayCreateVO()
				.setName(data.gatewayName())
				.setGroupIds(Set.of(groupUuidFromOtherTenant));
		var error = assert400(() -> client.createGateway(bearer(), tenant.getTenantId(), vo));
		assertEquals("Group " + groupUuidFromOtherTenant + " not found.", error.getMessage());
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(401): no token")
	@Test
	@Override
	public void createGateway401() {
		var vo = new GatewayCreateVO().setName(data.gatewayName());
		assert401(() -> client.createGateway(null, UUID.randomUUID(), vo));
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(404): tenant not exists")
	@Test
	@Override
	public void createGateway404() {
		var vo = new GatewayCreateVO().setName(data.tenantName());
		assert404(() -> client.createGateway(bearer(), UUID.randomUUID(), vo));
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(409): name exists")
	@Test
	@Override
	public void createGateway409() {
		var tenant = data.tenant();
		var existing = data.gateway(tenant);
		var vo = new GatewayCreateVO().setName(existing.getName());
		assert409(() -> client.createGateway(bearer(), tenant.getTenantId(), vo));
		assertEquals(1, data.countGateways(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateGateway(200): update nothing")
	@Test
	@Override
	public void updateGateway200() {

		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group), expectedProperties);
		var vo = new GatewayUpdateVO().setName(null).setEnabled(null).setGroupIds(null);
		var updated = assert200(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertEquals(gateway.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated,
				assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId())), "vo");
	}

	@DisplayName("updateGateway(200): update unchanged")
	@Test
	public void updateGateway200Unchanged() {

		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group), expectedProperties);
		var vo = new GatewayUpdateVO()
				.setName(gateway.getName())
				.setEnabled(gateway.getEnabled())
				.setGroupIds(Set.of(group.getGroupId()));
		var updated = assert200(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertEquals(gateway.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated,
				assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId())), "vo");
	}

	@DisplayName("updateGateway(200): update name")
	@Test
	public void updateGateway200Name() {

		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group), expectedProperties);
		var vo = new GatewayUpdateVO().setName(data.gatewayName()).setEnabled(null).setGroupIds(null);
		var updated = assert200(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated,
				assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId())), "vo");
	}

	@DisplayName("updateGateway(200): update groups")
	@Test
	public void updateGateway200Groups() {

		var tenant = data.tenant();
		var group1 = data.group(tenant);
		var group2 = data.group(tenant);
		var group3 = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group1, group2), expectedProperties);
		var vo = new GatewayUpdateVO()
				.setName(null)
				.setEnabled(null)
				.setGroupIds(Set.of(group2.getGroupId(), group3.getGroupId()));
		var updated = assert200(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group2.getGroupId(), group3.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated,
				assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId())), "vo");
	}

	@DisplayName("updateGateway(200): update all")
	@Test
	public void updateGateway200All() {

		var tenant = data.tenant();
		var group1 = data.group(tenant);
		var group2 = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group1), expectedProperties);
		var vo = new GatewayUpdateVO()
				.setName(data.gatewayName())
				.setEnabled(false)
				.setGroupIds(Set.of(group2.getGroupId()));
		var updated = assert200(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group2.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated,
				assert200(() -> client.getGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId())), "vo");
	}

	@DisplayName("updateGateway(400): check bean validation")
	@Test
	@Override
	public void updateGateway400() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var vo = new GatewayUpdateVO().setName("");
		assert400(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(400): group not exists")
	@Test
	public void updateGateway400GroupNotExists() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var notExistingGroupUuid = UUID.randomUUID();
		var vo = new GatewayUpdateVO().setGroupIds(Set.of(notExistingGroupUuid));
		var error = assert400(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));
		assertEquals("Group " + notExistingGroupUuid + " not found.", error.getMessage());
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(400): group from other tenant")
	@Test
	public void updateGateway400GroupFromOtherTenant() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var groupUuidFromOtherTenant = data.group(data.tenant()).getGroupId();
		var vo = new GatewayUpdateVO().setGroupIds(Set.of(groupUuidFromOtherTenant));
		var error = assert400(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));
		assertEquals("Group " + groupUuidFromOtherTenant + " not found.", error.getMessage());
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(401): no token")
	@Test
	@Override
	public void updateGateway401() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var vo = new GatewayUpdateVO().setName(data.gatewayName());
		assert401(() -> client.updateGateway(null, tenant.getTenantId(), gateway.getGatewayId(), vo));
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(404): not found")
	@Test
	@Override
	public void updateGateway404() {
		assert404(() -> client.updateGateway(bearer(), UUID.randomUUID(), UUID.randomUUID(), new GatewayUpdateVO()));
	}

	@DisplayName("updateGateway(409): name exists")
	@Test
	@Override
	public void updateGateway409() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var other = data.gateway(tenant);
		var vo = new GatewayUpdateVO().setName(other.getName());
		assert409(() -> client.updateGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId(), vo));
		assertEquals(gateway, data.find(gateway), "gateway changed");
		assertEquals(other, data.find(other), "other changed");
	}

	@DisplayName("deleteGateway(204): without related objects")
	@Test
	@Override
	public void deleteGateway204() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert204(() -> client.deleteGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId()));
		assertEquals(0, data.countGateways(), "gateway not deleted");
	}

	@DisplayName("deleteGateway(204): with group")
	@Test
	public void deleteGateway204WithGroup() {

		var tenant = data.tenant();
		var group = data.group(tenant);
		var gateway = data.gateway(group);

		assert204(() -> client.deleteGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId()));
		assertEquals(0, data.countGateways(), "gateway not deleted");
		assertEquals(1, data.countGroups(), "group deleted");
	}

	@DisplayName("deleteGateway(204): with secret")
	@Test
	public void deleteGateway204WithSecret() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		data.secret(gateway);

		assert204(() -> client.deleteGateway(bearer(), tenant.getTenantId(), gateway.getGatewayId()));
		assertEquals(0, data.countGateways(), "gateway not deleted");
		assertEquals(0, data.countSecrets(gateway), "secret not deleted");
	}

	@DisplayName("deleteGateway(401): no token")
	@Test
	@Override
	public void deleteGateway401() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert401(() -> client.deleteGateway(null, tenant.getTenantId(), gateway.getGatewayId()));
		assertEquals(1, data.countGateways(), "deleted");
	}

	@DisplayName("deleteGateway(404): gateway not found")
	@Test
	@Override
	public void deleteGateway404() {
		assert404(() -> client.deleteGateway(bearer(), UUID.randomUUID(), UUID.randomUUID()));
	}
}
