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
		var tenant3 = data.tenant();
		var group1 = data.group(tenant1, "abc2");
		var group2 = data.group(tenant1, "abc1");
		var group3 = data.group(tenant2, "aaa");

		// test helper

		var bearer = bearer();
		Function<UUID, List<UUID>> filter = tenant -> assert200(() -> client.getGroups(bearer, tenant)).stream()
				.map(GroupVO::getGroupId).collect(Collectors.toList());
		Function<List<Group>, List<UUID>> ids = groupIds -> groupIds.stream()
				.map(Group::getGroupId).collect(Collectors.toList());

		// execute

		assertEquals(ids.apply(List.of(group2, group1)), filter.apply(tenant1.getTenantId()), "tenant1");
		assertEquals(ids.apply(List.of(group3)), filter.apply(tenant2.getTenantId()), "tenant2");
		assertEquals(List.of(), filter.apply(tenant3.getTenantId()), "tenant3");
	}

	@DisplayName("getGroups(401): no token")
	@Test
	@Override
	public void getGroups401() {
		assert401(() -> client.getGroups(null));
	}

	@DisplayName("getGroups(404): tenant not found")
	@Test
	@Override
	public void getGroups404() {
		assert404(() -> client.getGroups(bearer(), UUID.randomUUID()));
	}

	@DisplayName("getGroup(200): success")
	@Test
	@Override
	public void getGroup200() {
		var tenant = data.tenant();
		var expected = data.group(tenant);
		var actual = assert200(() -> client.getGroup(bearer(), tenant.getTenantId(), expected.getGroupId()));
		assertEquals(expected.getGroupId(), actual.getGroupId(), "groupId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("getGroup(401): no token")
	@Test
	@Override
	public void getGroup401() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert401(() -> client.getGroup(null, tenant.getTenantId(), group.getGroupId()));
	}

	@DisplayName("getGroup(404): not found")
	@Test
	@Override
	public void getGroup404() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert404(() -> client.getGroup(bearer(), tenant.getTenantId(), UUID.randomUUID()));
		assert404(() -> client.getGroup(bearer(), UUID.randomUUID(), group.getGroupId()));
	}

	@DisplayName("createGroup(201): with mandatory properties")
	@Test
	@Override
	public void createGroup201() {
		var tenant = data.tenant();
		var vo = new GroupCreateVO().setName(data.groupName());
		var created = assert201(() -> client.createGroup(bearer(), tenant.getTenantId(), vo));
		assertNotNull(created.getGroupId(), "groupId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		var group = assert200(() -> client.getGroup(bearer(), tenant.getTenantId(), created.getGroupId()));
		assertEquals(created, group, "vo");
	}

	@DisplayName("createGroup(201): with optional properties")
	@Test
	public void createGroup201All() {
		var tenant = data.tenant();
		var vo = new GroupCreateVO().setName(data.groupName()).setGroupId(UUID.randomUUID());
		var created = assert201(() -> client.createGroup(bearer(), tenant.getTenantId(), vo));
		assertEquals(vo.getGroupId(), created.getGroupId(), "groupId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		var group = assert200(() -> client.getGroup(bearer(), tenant.getTenantId(), created.getGroupId()));
		assertEquals(created, group, "vo");
	}

	@DisplayName("createGroup(201): with id from other tenant")
	@Test
	public void createGroup201WithIdFromOtherTenant() {
		var otherGroup = data.group(data.tenant());
		var tenant = data.tenant();
		var vo = new GroupCreateVO().setName(data.groupName()).setGroupId(otherGroup.getGroupId());
		var created = assert201(() -> client.createGroup(bearer(), tenant.getTenantId(), vo));
		assertEquals(vo.getGroupId(), created.getGroupId(), "groupId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		var group = assert200(() -> client.getGroup(bearer(), tenant.getTenantId(), created.getGroupId()));
		assertEquals(created, group, "vo");
	}

	@DisplayName("createGroup(201): with name from other tenant")
	@Test
	public void createGroup201WithNameFromOtherTenant() {
		var otherGroup = data.group(data.tenant());
		var tenant = data.tenant();
		var vo = new GroupCreateVO().setName(otherGroup.getName());
		var created = assert201(() -> client.createGroup(bearer(), tenant.getTenantId(), vo));
		assertNotNull(created.getGroupId(), "groupId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		var group = assert200(() -> client.getGroup(bearer(), tenant.getTenantId(), created.getGroupId()));
		assertEquals(created, group, "vo");
	}

	@DisplayName("createGroup(400): check bean validation")
	@Test
	@Override
	public void createGroup400() {
		var tenant = data.tenant();
		var vo = new GroupCreateVO().setName("");
		assert400(() -> client.createGroup(bearer(), tenant.getTenantId(), vo));
		assertEquals(0, data.countGroups(), "created");
	}

	@DisplayName("createGroup(401): no token")
	@Test
	@Override
	public void createGroup401() {
		var tenant = data.tenant();
		var vo = new GroupCreateVO().setName(data.groupName());
		assert401(() -> client.createGroup(null, tenant.getTenantId(), vo));
		assertEquals(0, data.countGroups(), "created");
	}

	@DisplayName("createGroup(404): tenant not found")
	@Test
	@Override
	public void createGroup404() {
		var vo = new GroupCreateVO().setName(data.groupName());
		assert404(() -> client.createGroup(bearer(), UUID.randomUUID(), vo));
		assertEquals(0, data.countGroups(), "created");
	}

	@DisplayName("createGroup(409): id exists")
	@Test
	@Override
	public void createGroup409() {
		var tenant = data.tenant();
		var existing = data.group(tenant);
		var vo = new GroupCreateVO().setName(data.groupName()).setGroupId(existing.getGroupId());
		assert409(() -> client.createGroup(bearer(), tenant.getTenantId(), vo));
		assertEquals(1, data.countGroups(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("createGroup(409): name exists")
	@Test
	public void createGroup409Name() {
		var tenant = data.tenant();
		var existing = data.group(tenant);
		var vo = new GroupCreateVO().setName(existing.getName());
		assert409(() -> client.createGroup(bearer(), tenant.getTenantId(), vo));
		assertEquals(1, data.countGroups(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateGroup(200): update nothing")
	@Test
	@Override
	public void updateGroup200() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO().setName(null);
		var updated = assert200(() -> client.updateGroup(bearer(), tenant.getTenantId(), group.getGroupId(), vo));
		assertEquals(group.getGroupId(), updated.getGroupId(), "groupId");
		assertEquals(group.getName(), updated.getName(), "name");
		assertEquals(group.getCreated(), updated.getCreated(), "created");
		assertEquals(group.getUpdated(), updated.getUpdated(), "updated");
	}

	@DisplayName("updateGroup(200): update name")
	@Test
	public void updateGroup200Name() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO().setName(data.groupName());
		var updated = assert200(() -> client.updateGroup(bearer(), tenant.getTenantId(), group.getGroupId(), vo));
		assertEquals(group.getGroupId(), updated.getGroupId(), "groupId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(group.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(group.getUpdated()), "updated");
	}

	@DisplayName("updateGroup(200): update unchanged")
	@Test
	public void updateGroup200Unchanged() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO().setName(group.getName());
		var updated = assert200(() -> client.updateGroup(bearer(), tenant.getTenantId(), group.getGroupId(), vo));
		assertEquals(group.getGroupId(), updated.getGroupId(), "groupId");
		assertEquals(group.getName(), updated.getName(), "name");
		assertEquals(group.getCreated(), updated.getCreated(), "created");
		assertEquals(group.getUpdated(), updated.getUpdated(), "updated");
	}

	@DisplayName("updateGroup(400): check bean validation")
	@Test
	@Override
	public void updateGroup400() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO().setName("");
		assert400(() -> client.updateGroup(bearer(), tenant.getTenantId(), group.getGroupId(), vo));
		assertEquals(group, data.find(group), "entity changed");
	}

	@DisplayName("updateGroup(401): no token")
	@Test
	@Override
	public void updateGroup401() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO().setName(data.groupName());
		assert401(() -> client.updateGroup(null, tenant.getTenantId(), group.getGroupId(), vo));
		assertEquals(group, data.find(group), "entity changed");
	}

	@DisplayName("updateGroup(404): not found")
	@Test
	@Override
	public void updateGroup404() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO();
		assert404(() -> client.updateGroup(bearer(), tenant.getTenantId(), UUID.randomUUID(), vo));
		assert404(() -> client.updateGroup(bearer(), UUID.randomUUID(), group.getGroupId(), vo));
		assertEquals(group, data.find(group), "group changed");
	}

	@DisplayName("updateGroup(409): name exists")
	@Test
	@Override
	public void updateGroup409() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var other = data.group(tenant);
		var vo = new GroupUpdateVO().setName(other.getName());
		assert409(() -> client.updateGroup(bearer(), tenant.getTenantId(), group.getGroupId(), vo));
		assertEquals(group, data.find(group), "group changed");
		assertEquals(other, data.find(other), "other changed");
	}

	@DisplayName("deleteGroup(204): without related objects")
	@Test
	@Override
	public void deleteGroup204() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert204(() -> client.deleteGroup(bearer(), tenant.getTenantId(), group.getGroupId()));
		assertEquals(0, data.countGroups(), "group not deleted");
	}

	@DisplayName("deleteGroup(204): with gateway")
	@Test
	public void deleteGroup204WithGateway() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		data.gateway(group);
		assert204(() -> client.deleteGroup(bearer(), tenant.getTenantId(), group.getGroupId()));
		assertEquals(0, data.countGroups(), "group not deleted");
		assertEquals(1, data.countGateways(), "gateway deleted");
	}

	@DisplayName("deleteGroup(401): no token")
	@Test
	@Override
	public void deleteGroup401() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert401(() -> client.deleteGroup(null, tenant.getTenantId(), group.getGroupId()));
		assertEquals(1, data.countGroups(), "group deleted");
	}

	@DisplayName("deleteGroup(404): group not found")
	@Test
	@Override
	public void deleteGroup404() {
		var tenant = data.tenant();
		var otherTenant = data.tenant();
		var group = data.group(tenant);
		assert404(() -> client.deleteGroup(bearer(), tenant.getTenantId(), UUID.randomUUID()));
		assert404(() -> client.deleteGroup(bearer(), otherTenant.getTenantId(), group.getGroupId()));
		assert404(() -> client.deleteGroup(bearer(), UUID.randomUUID(), group.getGroupId()));
		assertEquals(1, data.countGroups(), "group deleted");
	}
}
