package io.inoa.fleet.registry.rest.management;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.AbstractTest;
import jakarta.inject.Inject;

/**
 * Test for {@link GatewaysApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: gateways")
public class GatewaysApiTest extends AbstractTest implements GatewaysApiTestSpec {

	@Inject
	GatewaysApiTestClient client;

	@DisplayName("findGateways(200): without parameters")
	@Test
	@Override
	public void findGateways200() {
		var tenant = data.tenant();
		data.gateway(tenant, "abc2");
		data.gateway(tenant, "abc1");
		var otherTenant = data.tenant();
		var otherGateway = data.gateway(otherTenant, "abc");
		var page = assert200(() -> client.findGateways(auth(tenant), null, null, null, null));
		assertEquals(2, page.getTotalSize(), "totalSize");
		assertEquals(2, page.getContent().size(), "size");
		assertTrue(page.getContent().stream()
				.noneMatch(gateway -> gateway.getGatewayId().equals(otherGateway.getGatewayId())), "tenantId");
	}

	@DisplayName("findGateways(200): with pagination")
	@Test
	public void findGateways200WithPagination() {
		var tenant = data.tenant();
		IntStream.range(0, 5).forEach(i -> data.gateway(tenant, "gateway" + i));
		var page = assert200(() -> client.findGateways(auth(tenant), 1, 3, null, null));
		assertEquals(5, page.getTotalSize(), "totalSize");
		assertEquals(2, page.getContent().size(), "contentSize");
	}

	@DisplayName("findGateways(200): with sort by single property")
	@Test
	public void findGateways200WithSortSingleProperty() {
		var tenant = data.tenant();
		IntStream.range(0, 5).forEach(i -> data.gateway(tenant, "gateway" + i));
		var sort = List.of(GatewayVO.JSON_PROPERTY_NAME + ",Asc");
		var comparator = Comparator.comparing(GatewayVO::getName);
		var page = assert200(() -> client.findGateways(auth(tenant), null, null, sort, null));
		assertSorted(page.getContent(), GatewayVO::getName, comparator);
	}

	@DisplayName("findGateways(200): with sort by multiple properties")
	@Test
	public void findGateways200WithSortMultipleProperty() {
		var tenant = data.tenant();
		IntStream.range(0, 5).forEach(i -> data.gateway(tenant, "gateway" + i, i % 2 == 0, List.of()));
		var sort = List.copyOf(GatewaysController.SORT_ORDER_PROPERTIES);
		assert200(() -> client.findGateways(auth(tenant), null, null, sort, null));
	}

	@DisplayName("findGateways(200): with sort by all properties")
	@Test
	public void findGateways200WithSortAllProperty() {
		var tenant = data.tenant();
		IntStream.range(0, 5).forEach(i -> data.gateway(tenant, "gateway" + i, i % 2 == 0, List.of()));
		var sort = List.of(
				GatewayVO.JSON_PROPERTY_ENABLED,
				GatewayVO.JSON_PROPERTY_NAME + ",DESC");
		var comparator = Comparator
				.comparing(GatewayVO::getEnabled).reversed()
				.thenComparing(GatewayVO::getName).reversed();
		var page = assert200(() -> client.findGateways(auth(tenant), null, null, sort, null));
		assertSorted(page.getContent(), GatewayVO::getName, comparator);
	}

	@DisplayName("findGateways(400): invalid sort")
	@Override
	public void findGateways400() {
		var tenant = data.tenant();
		assert200(() -> client.findGateways(auth(tenant), null, null, List.of("nope"), null));
	}

	@DisplayName("findGateways(401): no token")
	@Test
	@Override
	public void findGateways401() {
		assert401(() -> client.findGateways(null, null, null, null, null));
	}

	@DisplayName("findGateway(200): without group/properties")
	@Test
	@Override
	public void findGateway200() {
		var tenant = data.tenant();
		var expected = data.gateway(tenant);
		var actual = assert200(() -> client.findGateway(auth(tenant), expected.getGatewayId()));
		assertEquals(expected.getGatewayId(), actual.getGatewayId(), "gatewayId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(Set.of(), actual.getGroupIds(), "groupIds");
		assertEquals(Map.of(), actual.getProperties(), "properties");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findGateway(200): with group/properties")
	@Test
	public void findGateway200WithGroup() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var expected = data.gateway(tenant, List.of(group), expectedProperties);
		var actual = assert200(() -> client.findGateway(auth(tenant), expected.getGatewayId()));
		assertEquals(expected.getGatewayId(), actual.getGatewayId(), "gatewayId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(Set.of(group.getGroupId()), actual.getGroupIds(), "groupIds");
		assertEquals(expectedProperties, actual.getProperties(), "properties");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findGateway(401): no token")
	@Test
	@Override
	public void findGateway401() {
		assert401(() -> client.findGateway(null, UUID.randomUUID()));
	}

	@Override
	@DisplayName("findGateway(404): not found")
	@Test
	public void findGateway404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert404("Gateway not found.", () -> client.findGateway(auth(tenant), UUID.randomUUID()));
		assert404("Gateway not found.", () -> client.findGateway(auth(data.tenant()), gateway.getGatewayId()));
	}

	@DisplayName("createGateway(201): with mandatory properties")
	@Test
	@Override
	public void createGateway201() {
		var tenant = data.tenant();
		var vo = new GatewayCreateVO()
				.name(data.gatewayName())
				.enabled(null)
				.groupIds(null);
		var auth = auth(tenant);
		var created = assert201(() -> client.createGateway(auth, vo));
		assertNotNull(created.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(true, created.getEnabled(), "enabled");
		assertEquals(Set.of(), created.getGroupIds(), "groupIds");
		assertEquals(Map.of(), created.getProperties(), "properties");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findGateway(auth, created.getGatewayId())), "vo");
	}

	@DisplayName("createGateway(201): with optional properties")
	@Test
	public void createGateway201All() {
		var tenant = data.tenant();
		var group1 = data.group(tenant);
		var group2 = data.group(tenant);
		var vo = new GatewayCreateVO()
				.name(data.gatewayName())
				.enabled(false)
				.groupIds(Set.of(group1.getGroupId(), group2.getGroupId()));
		var auth = auth(tenant);
		var created = assert201(() -> client.createGateway(auth, vo));
		assertNotNull(created.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertEquals(Set.of(group1.getGroupId(), group2.getGroupId()), created.getGroupIds(), "groupIds");
		assertEquals(Map.of(), created.getProperties(), "properties");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findGateway(auth, created.getGatewayId())), "vo");
	}

	@DisplayName("createGateway(400): is beanvalidation active")
	@Test
	@Override
	public void createGateway400() {
		var vo = new GatewayCreateVO().name("");
		var tenant = data.tenant();
		assert400(() -> client.createGateway(auth(tenant), vo));
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(400): group not exists")
	@Test
	public void createGateway400GroupNotExists() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var notExistingGroupUuid = UUID.randomUUID();
		var vo = new GatewayCreateVO()
				.name(data.gatewayName())
				.groupIds(Set.of(group.getGroupId(), notExistingGroupUuid));
		var error = assert400(() -> client.createGateway(auth(tenant), vo));
		assertEquals("Group " + notExistingGroupUuid + " not found.", error.getMessage());
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(400): group from other tenant")
	@Test
	public void createGateway400GroupFromOtherTenant() {
		var groupUuidFromOtherTenant = data.group(data.tenant()).getGroupId();
		var tenant = data.tenant();
		var vo = new GatewayCreateVO()
				.name(data.gatewayName())
				.groupIds(Set.of(groupUuidFromOtherTenant));
		var error = assert400(() -> client.createGateway(auth(tenant), vo));
		assertEquals("Group " + groupUuidFromOtherTenant + " not found.", error.getMessage());
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(401): no token")
	@Test
	@Override
	public void createGateway401() {
		var vo = new GatewayCreateVO().name(data.gatewayName());
		assert401(() -> client.createGateway(null, vo));
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(409): name exists")
	@Test
	@Override
	public void createGateway409() {
		var tenant = data.tenant();
		var existing = data.gateway(tenant);
		var vo = new GatewayCreateVO().name(existing.getName());
		assert409(() -> client.createGateway(auth(tenant), vo));
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
		var vo = new GatewayUpdateVO().name(null).enabled(null).groupIds(null);
		var auth = auth(tenant);
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo));
		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertEquals(gateway.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId())), "vo");
	}

	@DisplayName("updateGateway(200): update unchanged")
	@Test
	public void updateGateway200Unchanged() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group), expectedProperties);
		var vo = new GatewayUpdateVO()
				.name(gateway.getName())
				.enabled(gateway.getEnabled())
				.groupIds(Set.of(group.getGroupId()));
		var auth = auth(tenant);
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo));
		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertEquals(gateway.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId())), "vo");
	}

	@DisplayName("updateGateway(200): update name")
	@Test
	public void updateGateway200Name() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group), expectedProperties);
		var vo = new GatewayUpdateVO().name(data.gatewayName()).enabled(null).groupIds(null);
		var auth = auth(tenant);
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo));
		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId())), "vo");
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
				.name(null)
				.enabled(null)
				.groupIds(Set.of(group2.getGroupId(), group3.getGroupId()));
		var auth = auth(tenant);
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group2.getGroupId(), group3.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId())), "vo");
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
				.name(data.gatewayName())
				.enabled(false)
				.groupIds(Set.of(group2.getGroupId()));
		var auth = auth(tenant);
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group2.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId())), "vo");
	}

	@DisplayName("updateGateway(400): is beanvalidation active")
	@Test
	@Override
	public void updateGateway400() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var vo = new GatewayUpdateVO().name("");
		assert400(() -> client.updateGateway(auth(tenant), gateway.getGatewayId(), vo));
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(400): group not exists")
	@Test
	public void updateGateway400GroupNotExists() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var notExistingGroupUuid = UUID.randomUUID();
		var vo = new GatewayUpdateVO().groupIds(Set.of(notExistingGroupUuid));
		var error = assert400(() -> client.updateGateway(auth(tenant), gateway.getGatewayId(), vo));
		assertEquals("Group " + notExistingGroupUuid + " not found.", error.getMessage());
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(400): group from other tenant")
	@Test
	public void updateGateway400GroupFromOtherTenant() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var groupUuidFromOtherTenant = data.group(data.tenant()).getGroupId();
		var vo = new GatewayUpdateVO().groupIds(Set.of(groupUuidFromOtherTenant));
		var error = assert400(() -> client.updateGateway(auth(tenant), gateway.getGatewayId(), vo));
		assertEquals("Group " + groupUuidFromOtherTenant + " not found.", error.getMessage());
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(401): no token")
	@Test
	@Override
	public void updateGateway401() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var vo = new GatewayUpdateVO().name(data.gatewayName());
		assert401(() -> client.updateGateway(null, gateway.getGatewayId(), vo));
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(404): not found")
	@Test
	@Override
	public void updateGateway404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var vo = new GatewayUpdateVO();
		assert404("Gateway not found.", () -> client.updateGateway(auth(tenant), UUID.randomUUID(), vo));
		assert404("Gateway not found.", () -> client.updateGateway(auth(data.tenant()), gateway.getGatewayId(), vo));
	}

	@DisplayName("updateGateway(409): name exists")
	@Test
	@Override
	public void updateGateway409() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var otherGateway = data.gateway(tenant);
		var vo = new GatewayUpdateVO().name(otherGateway.getName());
		assert409(() -> client.updateGateway(auth(tenant), gateway.getGatewayId(), vo));
		assertEquals(gateway, data.find(gateway), "gateway changed");
		assertEquals(otherGateway, data.find(otherGateway), "other changed");
	}

	@DisplayName("deleteGateway(204): without related objects")
	@Test
	@Override
	public void deleteGateway204() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert204(() -> client.deleteGateway(auth(tenant), gateway.getGatewayId()));
		assertEquals(0, data.countGateways(), "gateway not deleted");
	}

	@DisplayName("deleteGateway(204): with group")
	@Test
	public void deleteGateway204WithGroup() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var gateway = data.gateway(group);
		assert204(() -> client.deleteGateway(auth(tenant), gateway.getGatewayId()));
		assertEquals(0, data.countGateways(), "gateway not deleted");
		assertEquals(1, data.countGroups(), "group deleted");
	}

	@DisplayName("deleteGateway(204): with credential")
	@Test
	public void deleteGateway204WithCredential() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		data.credentialPSK(gateway);

		assert204(() -> client.deleteGateway(auth(tenant), gateway.getGatewayId()));
		assertEquals(0, data.countGateways(), "gateway not deleted");
		assertEquals(0, data.countCredentials(gateway), "credential not deleted");
	}

	@DisplayName("deleteGateway(401): no token")
	@Test
	@Override
	public void deleteGateway401() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert401(() -> client.deleteGateway(null, gateway.getGatewayId()));
		assertEquals(1, data.countGateways(), "deleted");
	}

	@DisplayName("deleteGateway(404): not found")
	@Test
	@Override
	public void deleteGateway404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert404("Gateway not found.", () -> client.deleteGateway(auth(tenant), UUID.randomUUID()));
		assert404("Gateway not found.", () -> client.deleteGateway(auth(data.tenant()), gateway.getGatewayId()));
		assertEquals(1, data.countGateways(), "deleted");
	}
}
