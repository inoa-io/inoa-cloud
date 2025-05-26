package io.inoa.fleet.registry.rest.management;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert204;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert404;
import static io.inoa.test.HttpAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.rest.GroupCreateVO;
import io.inoa.rest.GroupUpdateVO;
import io.inoa.rest.GroupsApi;
import io.inoa.rest.GroupsApiTestClient;
import io.inoa.rest.GroupsApiTestSpec;
import io.inoa.test.AbstractUnitTest;

/**
 * Test for {@link GroupsApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: groups")
public class GroupsApiTest extends AbstractUnitTest implements GroupsApiTestSpec {

	@Inject
	GroupsApiTestClient client;

	@DisplayName("findGroups(200): success")
	@Test
	@Override
	public void findGroups200() {
		var tenant = data.tenant();
		data.group(tenant, "abc2");
		data.group(tenant, "abc1");
		var otherTenant = data.tenant();
		var otherGroup = data.group(otherTenant, "abc");
		var groups = assert200(() -> client.findGroups(auth(tenant)));
		assertEquals(2, groups.size(), "groups");
		assertTrue(
				groups.stream().noneMatch(group -> group.getGroupId().equals(otherGroup.getGroupId())),
				"tenant");
	}

	@DisplayName("findGroups(401): no token")
	@Test
	@Override
	public void findGroups401() {
		assert401(() -> client.findGroups(null));
	}

	@DisplayName("findGroup(200): success")
	@Test
	@Override
	public void findGroup200() {
		var tenant = data.tenant();
		var expected = data.group(tenant);
		var actual = assert200(() -> client.findGroup(auth(tenant), expected.getGroupId(), null));
		assertEquals(expected.getGroupId(), actual.getGroupId(), "groupId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findGroup(400): ambiguous tenants")
	@Test
	@Override
	public void findGroup400() {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var expected = data.group(tenant1);
		assert400(() -> client.findGroup(auth(tenant1, tenant2), expected.getGroupId(), null));
	}

	@DisplayName("findGroup(401): no token")
	@Test
	@Override
	public void findGroup401() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert401(() -> client.findGroup(null, group.getGroupId(), null));
	}

	@DisplayName("findGroup(404): not found")
	@Test
	@Override
	public void findGroup404() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert404("Group not found.", () -> client.findGroup(auth(tenant), UUID.randomUUID(), null));
		assert404(
				"Group not found.", () -> client.findGroup(auth(data.tenant()), group.getGroupId(), null));
	}

	@DisplayName("createGroup(201): with mandatory properties")
	@Test
	@Override
	public void createGroup201() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var vo = new GroupCreateVO().name(data.groupName());
		var created = assert201(() -> client.createGroup(auth, vo, null));
		assertNotNull(created.getGroupId(), "groupId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(
				created, assert200(() -> client.findGroup(auth, created.getGroupId(), null)), "vo");
	}

	@DisplayName("createGroup(201): with optional properties")
	@Test
	public void createGroup201All() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var vo = new GroupCreateVO().name(data.groupName()).groupId(UUID.randomUUID());
		var created = assert201(() -> client.createGroup(auth, vo, null));
		assertEquals(vo.getGroupId(), created.getGroupId(), "groupId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(
				created, assert200(() -> client.findGroup(auth, created.getGroupId(), null)), "vo");
	}

	@DisplayName("createGroup(201): with id from other tenant")
	@Test
	public void createGroup201WithIdFromOtherTenant() {
		var otherTenant = data.tenant();
		var otherGroup = data.group(otherTenant);
		var tenant = data.tenant();
		var auth = auth(tenant);
		var vo = new GroupCreateVO().name(data.groupName()).groupId(otherGroup.getGroupId());
		var created = assert201(() -> client.createGroup(auth, vo, null));
		assertEquals(vo.getGroupId(), created.getGroupId(), "groupId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(
				created, assert200(() -> client.findGroup(auth, created.getGroupId(), null)), "vo");
	}

	@DisplayName("createGroup(201): with name from other tenant")
	@Test
	public void createGroup201WithNameFromOtherTenant() {
		var otherTenant = data.tenant();
		var otherGroup = data.group(otherTenant);
		var tenant = data.tenant();
		var auth = auth(tenant);
		var vo = new GroupCreateVO().name(otherGroup.getName());
		var created = assert201(() -> client.createGroup(auth, vo, null));
		assertNotNull(created.getGroupId(), "groupId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(
				created, assert200(() -> client.findGroup(auth, created.getGroupId(), null)), "vo");
	}

	@DisplayName("createGroup(400): is beanvalidation active")
	@Test
	@Override
	public void createGroup400() {
		var tenant = data.tenant();
		var vo = new GroupCreateVO().name("");
		assert400(() -> client.createGroup(auth(tenant), vo, null));
		assertEquals(0, data.countGroups(), "created");
	}

	@DisplayName("createGroup(401): no token")
	@Test
	@Override
	public void createGroup401() {
		var vo = new GroupCreateVO().name(data.groupName());
		assert401(() -> client.createGroup(null, vo, null));
		assertEquals(0, data.countGroups(), "created");
	}

	@DisplayName("createGroup(409): id exists")
	@Test
	@Override
	public void createGroup409() {
		var tenant = data.tenant();
		var existing = data.group(tenant);
		var vo = new GroupCreateVO().name(data.groupName()).groupId(existing.getGroupId());
		assert409(() -> client.createGroup(auth(tenant), vo, null));
		assertEquals(1, data.countGroups(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("createGroup(409): name exists")
	@Test
	public void createGroup409Name() {
		var tenant = data.tenant();
		var existing = data.group(tenant);
		var vo = new GroupCreateVO().name(existing.getName());
		assert409(() -> client.createGroup(auth(tenant), vo, null));
		assertEquals(1, data.countGroups(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateGroup(200): update nothing")
	@Test
	@Override
	public void updateGroup200() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var auth = auth(tenant);
		var vo = new GroupUpdateVO();
		var updated = assert200(() -> client.updateGroup(auth, group.getGroupId(), vo, null));
		assertEquals(group.getGroupId(), updated.getGroupId(), "groupId");
		assertEquals(group.getName(), updated.getName(), "name");
		assertEquals(group.getCreated(), updated.getCreated(), "created");
		assertEquals(group.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(
				updated, assert200(() -> client.findGroup(auth, updated.getGroupId(), null)), "vo");
	}

	@DisplayName("updateGroup(200): update unchanged")
	@Test
	public void updateGroup200Unchanged() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var auth = auth(tenant);
		var vo = new GroupUpdateVO().name(group.getName());
		var updated = assert200(() -> client.updateGroup(auth, group.getGroupId(), vo, null));
		assertEquals(group.getGroupId(), updated.getGroupId(), "groupId");
		assertEquals(group.getName(), updated.getName(), "name");
		assertEquals(group.getCreated(), updated.getCreated(), "created");
		assertEquals(group.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(
				updated, assert200(() -> client.findGroup(auth, updated.getGroupId(), null)), "vo");
	}

	@DisplayName("updateGroup(200): update name")
	@Test
	public void updateGroup200Name() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var auth = auth(tenant);
		var vo = new GroupUpdateVO().name(data.groupName());
		var updated = assert200(() -> client.updateGroup(auth, group.getGroupId(), vo, null));
		assertEquals(group.getGroupId(), updated.getGroupId(), "groupId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(group.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(group.getUpdated()), "updated");
		assertEquals(
				updated, assert200(() -> client.findGroup(auth, updated.getGroupId(), null)), "vo");
	}

	@DisplayName("updateGroup(400): is beanvalidation active")
	@Test
	@Override
	public void updateGroup400() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO().name("");
		assert400(() -> client.updateGroup(auth(tenant), group.getGroupId(), vo, null));
		assertEquals(group, data.find(group), "entity changed");
	}

	@DisplayName("updateGroup(401): no token")
	@Test
	@Override
	public void updateGroup401() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO().name(data.groupName());
		assert401(() -> client.updateGroup(null, group.getGroupId(), vo, null));
		assertEquals(group, data.find(group), "entity changed");
	}

	@DisplayName("updateGroup(404): not found")
	@Test
	@Override
	public void updateGroup404() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var vo = new GroupUpdateVO();
		assert404(
				"Group not found.", () -> client.updateGroup(auth(tenant), UUID.randomUUID(), vo, null));
		assert404(
				"Group not found.",
				() -> client.updateGroup(auth(data.tenant()), group.getGroupId(), vo, null));
	}

	@DisplayName("updateGroup(409): name exists")
	@Test
	@Override
	public void updateGroup409() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		var other = data.group(tenant);
		var vo = new GroupUpdateVO().name(other.getName());
		assert409(() -> client.updateGroup(auth(tenant), group.getGroupId(), vo, null));
		assertEquals(group, data.find(group), "group changed");
		assertEquals(other, data.find(other), "other changed");
	}

	@DisplayName("deleteGroup(204): without related objects")
	@Test
	@Override
	public void deleteGroup204() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert204(() -> client.deleteGroup(auth(tenant), group.getGroupId(), null));
		assertEquals(0, data.countGroups(), "group not deleted");
	}

	@DisplayName("deleteGroup(400): ambiguous tenants")
	@Test
	@Override
	public void deleteGroup400() throws Exception {
		var tenant1 = data.tenant();
		var tenant2 = data.tenant("inoa");
		var group = data.group(tenant1);
		assert400(() -> client.deleteGroup(auth(tenant1, tenant2), group.getGroupId(), null));
	}

	@DisplayName("deleteGroup(204): with gateway")
	@Test
	public void deleteGroup204WithGateway() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		data.gateway(group);
		assert204(() -> client.deleteGroup(auth(tenant), group.getGroupId(), null));
		assertEquals(0, data.countGroups(), "group not deleted");
		assertEquals(1, data.countGateways(), "gateway deleted");
	}

	@DisplayName("deleteGroup(401): no token")
	@Test
	@Override
	public void deleteGroup401() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert401(() -> client.deleteGroup(null, group.getGroupId(), null));
		assertEquals(1, data.countGroups(), "group deleted");
	}

	@DisplayName("deleteGroup(404): not found")
	@Test
	@Override
	public void deleteGroup404() {
		var tenant = data.tenant();
		var group = data.group(tenant);
		assert404("Group not found.", () -> client.deleteGroup(auth(tenant), UUID.randomUUID(), null));
		assert404(
				"Group not found.",
				() -> client.deleteGroup(auth(data.tenant()), group.getGroupId(), null));
	}
}
