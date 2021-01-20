package io.kokuwa.fleet.registry.rest.management;

import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert404;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.AbstractTest;
import io.kokuwa.fleet.registry.domain.Group;

/**
 * Test for {@link GroupApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: group")
public class GroupApiTest extends AbstractTest implements GroupApiTestSpec {

	@Inject
	GroupApiTestClient client;

	@DisplayName("getGroups(200): success")
	@Test
	@Override
	public void getGroups200() {

		// create groups

		var tenant1 = data.tenant();
		var tenant2 = data.tenant();
		var group1 = data.group(tenant1, "abc2");
		var group2 = data.group(tenant1, "abc1");
		var group3 = data.group(tenant2, "aaa");

		// test helper

		var bearer = bearerAdmin();
		Function<UUID, List<UUID>> filter = tenant -> assert200(() -> client.getGroups(bearer, tenant)).stream()
				.map(GroupVO::getId).collect(Collectors.toList());
		Function<List<Group>, List<UUID>> ids = groups -> groups.stream()
				.map(Group::getId).collect(Collectors.toList());

		// execute

		assertEquals(ids.apply(List.of(group3, group2, group1)), filter.apply(null), "empty");
		assertEquals(ids.apply(List.of(group2, group1)), filter.apply(tenant1.getId()), "tenant1");
		assertEquals(ids.apply(List.of(group3)), filter.apply(tenant2.getId()), "tenant2");
	}

	@DisplayName("getGroups(401): no token")
	@Test
	@Override
	public void getGroups401() {
		assert401(() -> client.getGroups(null));
	}

	@DisplayName("getGroup(200): success")
	@Test
	@Override
	public void getGroup200() {
		var expected = data.group();
		var actual = assert200(() -> client.getGroup(bearerAdmin(), expected.getId()));
		assertEquals(expected.getId(), actual.getId(), "id");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("getGroup(401): no token")
	@Test
	@Override
	public void getGroup401() {
		assert401(() -> client.getGroup(null, UUID.randomUUID()));
	}

	@DisplayName("getGroup(404): not found")
	@Test
	@Override
	public void getGroup404() {
		assert404(() -> client.getGroup(bearerAdmin(), UUID.randomUUID()));
	}

	@DisplayName("createGroup(201): success")
	@Test
	@Override
	public void createGroup201() {
		var tenant = data.tenant();
		var vo = new GroupCreateVO().setTenant(tenant.getId()).setName(data.groupName());
		var created = assert201(() -> client.createGroup(bearerAdmin(), vo));
		assertEquals(tenant.getId(), created.getTenant(), "tenant");
		assertNotNull(created.getId(), "id");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.getGroup(bearerAdmin(), created.getId())), "vo");
	}

	@DisplayName("createGroup(400): check bean validation")
	@Test
	@Override
	public void createGroup400() {
		var vo = new GroupCreateVO().setTenant(data.tenant().getId()).setName("");
		assert400(() -> client.createGroup(bearerAdmin(), vo));
		assertEquals(0, data.countGroups(), "created");
	}

	@DisplayName("createGroup(400): tenant not exists")
	@Test
	public void createGroup400TenantNotExists() {
		var vo = new GroupCreateVO().setTenant(UUID.randomUUID()).setName(data.tenantName());
		var error = assert400(() -> client.createGroup(bearerAdmin(), vo));
		assertEquals("Tenant not found.", error.getMessage());
		assertEquals(0, data.countGroups(), "created");
	}

	@DisplayName("createGroup(401): no token")
	@Test
	@Override
	public void createGroup401() {
		var vo = new GroupCreateVO().setTenant(data.tenant().getId()).setName(data.groupName());
		assert401(() -> client.createGroup(null, vo));
		assertEquals(0, data.countGroups(), "created");
	}

	@DisplayName("createGroup(409): name exists")
	@Test
	@Override
	public void createGroup409() {
		var existing = data.group();
		var vo = new GroupCreateVO().setTenant(existing.getTenant().getId()).setName(existing.getName());
		assert409(() -> client.createGroup(bearerAdmin(), vo));
		assertEquals(1, data.countGroups(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateGroup(200): update nothing")
	@Test
	@Override
	public void updateGroup200() {
		var existing = data.group();
		var vo = new GroupUpdateVO().setName(null);
		var updated = assert200(() -> client.updateGroup(bearerAdmin(), existing.getId(), vo));
		assertEquals(existing.getTenant().getId(), updated.getTenant(), "tenant");
		assertEquals(existing.getId(), updated.getId(), "id");
		assertEquals(existing.getName(), updated.getName(), "name");
		assertEquals(existing.getCreated(), updated.getCreated(), "created");
		assertEquals(existing.getUpdated(), updated.getUpdated(), "updated");
	}

	@DisplayName("updateGroup(200): update name")
	@Test
	public void updateGroup200Name() {
		var existing = data.group();
		var vo = new GroupUpdateVO().setName(data.groupName());
		var updated = assert200(() -> client.updateGroup(bearerAdmin(), existing.getId(), vo));
		assertEquals(existing.getTenant().getId(), updated.getTenant(), "tenant");
		assertEquals(existing.getId(), updated.getId(), "id");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(existing.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(existing.getUpdated()), "updated");
	}

	@DisplayName("updateGroup(200): update unchanged")
	@Test
	public void updateGroup200Unchanged() {
		var existing = data.group();
		var vo = new GroupUpdateVO().setName(existing.getName());
		var updated = assert200(() -> client.updateGroup(bearerAdmin(), existing.getId(), vo));
		assertEquals(existing.getTenant().getId(), updated.getTenant(), "tenant");
		assertEquals(existing.getId(), updated.getId(), "id");
		assertEquals(existing.getName(), updated.getName(), "name");
		assertEquals(existing.getCreated(), updated.getCreated(), "created");
		assertEquals(existing.getUpdated(), updated.getUpdated(), "updated");
	}

	@DisplayName("updateGroup(400): check bean validation")
	@Test
	@Override
	public void updateGroup400() {
		var existing = data.group();
		assert400(() -> client.updateGroup(bearerAdmin(), existing.getId(), new GroupUpdateVO().setName("")));
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateGroup(401): no token")
	@Test
	@Override
	public void updateGroup401() {
		var existing = data.group();
		assert401(() -> client.updateGroup(null, existing.getId(), new GroupUpdateVO().setName(data.groupName())));
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateGroup(404): not found")
	@Test
	@Override
	public void updateGroup404() {
		assert404(() -> client.updateGroup(bearerAdmin(), UUID.randomUUID(), new GroupUpdateVO()));
	}

	@DisplayName("updateGroup(409): name exists")
	@Test
	@Override
	public void updateGroup409() {
		var group = data.group();
		var other = data.group(group.getTenant());
		var vo = new GroupUpdateVO().setName(other.getName());
		assert409(() -> client.updateGroup(bearerAdmin(), group.getId(), vo));
		assertEquals(group, data.find(group), "group changed");
		assertEquals(other, data.find(other), "other changed");
	}

	@DisplayName("deleteGroup(204): without gateway")
	@Test
	@Override
	public void deleteGroup204() {
		var group = data.group();
		assert204(() -> client.deleteGroup(bearerAdmin(), group.getId()));
		assertEquals(0, data.countGroups(), "not deleted");
	}

	@DisplayName("deleteGroup(204): with gateway")
	@Test
	public void deleteGroup204WithGateway() {
		var group = data.group();
		data.gateway(group);
		assert204(() -> client.deleteGroup(bearerAdmin(), group.getId()));
		assertEquals(0, data.countGroups(), "not deleted");
	}

	@DisplayName("deleteGroup(401): no token")
	@Test
	@Override
	public void deleteGroup401() {
		var group = data.group();
		assert401(() -> client.deleteGroup(null, group.getId()));
		assertEquals(1, data.countGroups(), "deleted");
	}

	@DisplayName("deleteGroup(404): group not found")
	@Test
	@Override
	public void deleteGroup404() {
		assert404(() -> client.deleteGroup(bearerAdmin(), UUID.randomUUID()));
	}
}
