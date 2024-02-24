package io.inoa.fleet.registry.rest.management;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert204;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert403;
import static io.inoa.test.HttpAssertions.assert404;
import static io.inoa.test.HttpAssertions.assert409;
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
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import io.inoa.rest.GatewayCreateVO;
import io.inoa.rest.GatewayUpdateVO;
import io.inoa.rest.GatewayVO;
import io.inoa.rest.GatewaysApiTestClient;
import io.inoa.rest.GatewaysApiTestSpec;
import io.inoa.rest.MoveGatewayRequestVO;
import io.inoa.test.AbstractUnitTest;
import jakarta.inject.Inject;

/**
 * Test for {@link GatewaysController}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: gateways")
public class GatewaysApiTest extends AbstractUnitTest implements GatewaysApiTestSpec {

	@Inject GatewaysApiTestClient client;

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
		var sort = List.of(GatewayVO.JSON_PROPERTY_ENABLED, GatewayVO.JSON_PROPERTY_NAME + ",DESC");
		var comparator = Comparator.comparing(GatewayVO::getEnabled).reversed().thenComparing(GatewayVO::getName)
				.reversed();
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

	@Override
	public void moveGateway200() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var vo = new GatewayCreateVO().gatewayId(data.gatewayId()).name("Test").enabled(true).groupIds(null);
		var auth = auth(tenant1, tenant2);
		assert201(() -> client.createGateway(auth, vo, tenant1.getTenantId()));
		assert200(() -> client.moveGateway(auth, new MoveGatewayRequestVO().gatewayId(vo.getGatewayId())
				.sourceTenantId(tenant1.getTenantId()).targetTenantId(tenant2.getTenantId())));
		assert200(() -> client.findGateway(auth, vo.getGatewayId(), tenant2.getTenantId()));
	}

	@Override
	public void moveGateway401() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var vo = new GatewayCreateVO().gatewayId(data.gatewayId()).name("Test").enabled(true).groupIds(null);
		var auth = auth(tenant1, tenant2);
		assert201(() -> client.createGateway(auth, vo, tenant1.getTenantId()));
		assert401(() -> client.moveGateway(null, new MoveGatewayRequestVO().gatewayId(vo.getGatewayId())
				.sourceTenantId(tenant1.getTenantId()).targetTenantId(tenant2.getTenantId())));
	}

	@Override
	public void moveGateway403() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var tenant3 = data.tenant("darfichni");
		var vo = new GatewayCreateVO().gatewayId(data.gatewayId()).name("Test").enabled(true).groupIds(null);
		var auth = auth(tenant1, tenant2);
		assert201(() -> client.createGateway(auth, vo, tenant1.getTenantId()));
		assert403(() -> client.moveGateway(null, new MoveGatewayRequestVO().gatewayId(vo.getGatewayId())
				.sourceTenantId(tenant1.getTenantId()).targetTenantId(tenant3.getTenantId())));
	}

	@Override
	public void moveGateway404() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		assert401(() -> client.moveGateway(null, new MoveGatewayRequestVO().gatewayId("gibtsni")
				.sourceTenantId(tenant1.getTenantId()).targetTenantId(tenant2.getTenantId())));
	}

	@DisplayName("findGateway(200): without group/properties")
	@Test
	@Override
	public void findGateway200() {
		var tenant = data.tenant();
		var expected = data.gateway(tenant);
		var actual = assert200(() -> client.findGateway(auth(tenant), expected.getGatewayId(), null));
		assertEquals(expected.getGatewayId(), actual.getGatewayId(), "gatewayId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(Set.of(), actual.getGroupIds(), "groupIds");
		assertEquals(Map.of(), actual.getProperties(), "properties");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findGateway(200): with specific tenant")
	@Test
	void findGateway200WithSpecificTenant() {
		var tenant = data.tenant();
		var expected = data.gateway(tenant);
		var actual = assert200(() -> client.findGateway(auth(tenant), expected.getGatewayId(), tenant.getTenantId()));
		assertEquals(expected.getGatewayId(), actual.getGatewayId(), "gatewayId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(Set.of(), actual.getGroupIds(), "groupIds");
		assertEquals(Map.of(), actual.getProperties(), "properties");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findGateway(200): with multiple tenants")
	@Test
	void findGateway200WithMultipleTenants() {
		var tenant = data.tenant();
		var tenant2 = data.tenant("anderer-tenant");
		var expected = data.gateway(tenant);
		var actual = assert200(() -> client.findGateway(auth(tenant, tenant2), expected.getGatewayId(),
				tenant.getTenantId()));
		assertEquals(expected.getGatewayId(), actual.getGatewayId(), "gatewayId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(Set.of(), actual.getGroupIds(), "groupIds");
		assertEquals(Map.of(), actual.getProperties(), "properties");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findGateway(400): ambiguous tenants")
	@Test
	@Override
	public void findGateway400() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var expected = data.gateway(tenant1);
		assert400(() -> client.findGateway(auth(tenant1, tenant2), expected.getGatewayId(), null));
	}

	@DisplayName("findGateway(200): with group/properties")
	@Test
	public void findGateway200WithGroup() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var expected = data.gateway(tenant, List.of(group), expectedProperties);
		var actual = assert200(() -> client.findGateway(auth(tenant), expected.getGatewayId(), null));
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
		assert401(() -> client.findGateway(null, data.gatewayId(), null));
	}

	@Override
	@DisplayName("findGateway(404): not found")
	@Test
	public void findGateway404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert404("Gateway not found.", () -> client.findGateway(auth(tenant), data.gatewayId(), null));
		assert404("Gateway not found.", () -> client.findGateway(auth(data.tenant()), gateway.getGatewayId(), null));
	}

	@DisplayName("createGateway(201): with mandatory properties")
	@Test
	@Override
	public void createGateway201() {
		var tenant = data.tenant();
		var vo = new GatewayCreateVO().gatewayId(data.gatewayId()).name(null).enabled(null).groupIds(null);
		var auth = auth(tenant);
		var created = assert201(() -> client.createGateway(auth, vo, null));
		assertNotNull(created.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(true, created.getEnabled(), "enabled");
		assertEquals(Set.of(), created.getGroupIds(), "groupIds");
		assertEquals(Map.of(), created.getProperties(), "properties");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findGateway(auth, created.getGatewayId(), null)), "vo");
	}

	@DisplayName("createGateway(201): with optional properties")
	@Test
	public void createGateway201All() {
		var tenant = data.tenant();
		var group1 = data.group(tenant);
		var group2 = data.group(tenant);
		var vo = new GatewayCreateVO().gatewayId(data.gatewayId()).name(data.gatewayName()).enabled(false)
				.groupIds(Set.of(group1.getGroupId(), group2.getGroupId()));
		var auth = auth(tenant);
		var created = assert201(() -> client.createGateway(auth, vo, null));
		assertNotNull(created.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertEquals(Set.of(group1.getGroupId(), group2.getGroupId()), created.getGroupIds(), "groupIds");
		assertEquals(Map.of(), created.getProperties(), "properties");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findGateway(auth, created.getGatewayId(), null)), "vo");
	}

	@DisplayName("createGateway(400): is beanvalidation active")
	@Test
	@Override
	public void createGateway400() {
		var vo = new GatewayCreateVO().name("");
		var tenant = data.tenant();
		assert400(() -> client.createGateway(auth(tenant), vo, null));
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(400): gatewayId is invalid")
	@Test
	public void createGateway400GatewayIdInvalid() {
		var vo = new GatewayCreateVO().gatewayId("NOPE").name(data.gatewayName());
		var tenant = data.tenant();
		var error = assert400(() -> client.createGateway(auth(tenant), vo, null));
		assertEquals("GatewayId must match " + tenant.getGatewayIdPattern() + ".", error.getMessage());
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(400): group not exists")
	@Test
	public void createGateway400GroupNotExists() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var notExistingGroupUuid = UUID.randomUUID();
		var vo = new GatewayCreateVO().gatewayId(data.gatewayId()).name(data.gatewayName())
				.groupIds(Set.of(group.getGroupId(), notExistingGroupUuid));
		var error = assert400(() -> client.createGateway(auth(tenant), vo, null));
		assertEquals("Group " + notExistingGroupUuid + " not found.", error.getMessage());
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(400): group from other tenant")
	@Test
	public void createGateway400GroupFromOtherTenant() {
		var groupUuidFromOtherTenant = data.group(data.tenant()).getGroupId();
		var tenant = data.tenant();
		var vo = new GatewayCreateVO().gatewayId(data.gatewayId()).name(data.gatewayName())
				.groupIds(Set.of(groupUuidFromOtherTenant));
		var error = assert400(() -> client.createGateway(auth(tenant), vo, null));
		assertEquals("Group " + groupUuidFromOtherTenant + " not found.", error.getMessage());
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(401): no token")
	@Test
	@Override
	public void createGateway401() {
		var vo = new GatewayCreateVO().gatewayId(data.gatewayId()).name(data.gatewayName());
		assert401(() -> client.createGateway(null, vo, null));
		assertEquals(0, data.countGateways(), "created");
	}

	@DisplayName("createGateway(409): id exists")
	@Test
	@Override
	public void createGateway409() {
		var tenant = data.tenant();
		var existing = data.gateway(tenant);
		var vo = new GatewayCreateVO().gatewayId(existing.getGatewayId());
		assert409(() -> client.createGateway(auth(tenant), vo, null));
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
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo, null));
		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertEquals(gateway.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId(), null)), "vo");
	}

	@DisplayName("updateGateway(200): update unchanged")
	@Test
	public void updateGateway200Unchanged() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group), expectedProperties);
		var vo = new GatewayUpdateVO().name(gateway.getName()).enabled(gateway.getEnabled())
				.groupIds(Set.of(group.getGroupId()));
		var auth = auth(tenant);
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo, null));
		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertEquals(gateway.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId(), null)), "vo");
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
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo, null));
		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId(), null)), "vo");
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
		var vo = new GatewayUpdateVO().name(null).enabled(null)
				.groupIds(Set.of(group2.getGroupId(), group3.getGroupId()));
		var auth = auth(tenant);
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo, null));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(gateway.getName(), updated.getName(), "name");
		assertEquals(gateway.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group2.getGroupId(), group3.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId(), null)), "vo");
	}

	@DisplayName("updateGateway(200): update all")
	@Test
	public void updateGateway200All() {

		var tenant = data.tenant();
		var group1 = data.group(tenant);
		var group2 = data.group(tenant);
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(tenant, List.of(group1), expectedProperties);
		var vo = new GatewayUpdateVO().name(data.gatewayName()).enabled(false).groupIds(Set.of(group2.getGroupId()));
		var auth = auth(tenant);
		var updated = assert200(() -> client.updateGateway(auth, gateway.getGatewayId(), vo, null));

		assertEquals(gateway.getGatewayId(), updated.getGatewayId(), "gatewayId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(expectedProperties, updated.getProperties(), "properties");
		assertEquals(Set.of(group2.getGroupId()), updated.getGroupIds(), "groupIds");
		assertEquals(gateway.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(gateway.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findGateway(auth, gateway.getGatewayId(), null)), "vo");
	}

	@DisplayName("updateGateway(400): is beanvalidation active")
	@Test
	@Override
	public void updateGateway400() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var vo = new GatewayUpdateVO().name(RandomStringUtils.randomAlphabetic(101));
		assert400(() -> client.updateGateway(auth(tenant), gateway.getGatewayId(), vo, null));
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(400): group not exists")
	@Test
	public void updateGateway400GroupNotExists() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var notExistingGroupUuid = UUID.randomUUID();
		var vo = new GatewayUpdateVO().groupIds(Set.of(notExistingGroupUuid));
		var error = assert400(() -> client.updateGateway(auth(tenant), gateway.getGatewayId(), vo, null));
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
		var error = assert400(() -> client.updateGateway(auth(tenant), gateway.getGatewayId(), vo, null));
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
		assert401(() -> client.updateGateway(null, gateway.getGatewayId(), vo, null));
		assertEquals(gateway, data.find(gateway), "entity changed");
	}

	@DisplayName("updateGateway(404): not found")
	@Test
	@Override
	public void updateGateway404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		var vo = new GatewayUpdateVO();
		assert404("Gateway not found.", () -> client.updateGateway(auth(tenant), data.gatewayId(), vo, null));
		assert404("Gateway not found.",
				() -> client.updateGateway(auth(data.tenant()), gateway.getGatewayId(), vo, null));
	}

	@DisplayName("deleteGateway(204): without related objects")
	@Test
	@Override
	public void deleteGateway204() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert204(() -> client.deleteGateway(auth(tenant), gateway.getGatewayId(), null));
		assertEquals(0, data.countGateways(), "gateway not deleted");
	}

	@DisplayName("deleteGateway(400): ambiguous tenants")
	@Test
	@Override
	public void deleteGateway400() throws Exception {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var gateway = data.gateway(tenant1);
		assert400(() -> client.deleteGateway(auth(tenant1, tenant2), gateway.getGatewayId(), null));
	}

	@DisplayName("deleteGateway(204): with group")
	@Test
	public void deleteGateway204WithGroup() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var gateway = data.gateway(group);
		assert204(() -> client.deleteGateway(auth(tenant), gateway.getGatewayId(), null));
		assertEquals(0, data.countGateways(), "gateway not deleted");
		assertEquals(1, data.countGroups(), "group deleted");
	}

	@DisplayName("deleteGateway(204): with credential")
	@Test
	public void deleteGateway204WithCredential() {

		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		data.credentialPSK(gateway);

		assert204(() -> client.deleteGateway(auth(tenant), gateway.getGatewayId(), null));
		assertEquals(0, data.countGateways(), "gateway not deleted");
		assertEquals(0, data.countCredentials(gateway), "credential not deleted");
	}

	@DisplayName("deleteGateway(401): no token")
	@Test
	@Override
	public void deleteGateway401() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert401(() -> client.deleteGateway(null, gateway.getGatewayId(), null));
		assertEquals(1, data.countGateways(), "deleted");
	}

	@DisplayName("deleteGateway(404): not found")
	@Test
	@Override
	public void deleteGateway404() {
		var tenant = data.tenant();
		var gateway = data.gateway(tenant);
		assert404("Gateway not found.", () -> client.deleteGateway(auth(tenant), data.gatewayId(), null));
		assert404("Gateway not found.", () -> client.deleteGateway(auth(data.tenant()), gateway.getGatewayId(), null));
		assertEquals(1, data.countGateways(), "deleted");
	}
}
