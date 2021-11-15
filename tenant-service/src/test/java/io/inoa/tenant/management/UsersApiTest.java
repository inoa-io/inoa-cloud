package io.inoa.tenant.management;

import static io.inoa.tenant.HttpResponseAssertions.assert204;
import static io.inoa.tenant.HttpResponseAssertions.assert401;
import static io.inoa.tenant.HttpResponseAssertions.assert404;
import static io.inoa.tenant.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.tenant.AbstractTest;
import jakarta.inject.Inject;

/**
 * Test for {@link UsersApi}.
 *
 * @author Stephan Schnabel
 */
public class UsersApiTest extends AbstractTest implements UsersApiTestSpec {

	@Inject
	UsersApiTestClient client;

	@DisplayName("findUsers(200): success")
	@Test
	@Override
	public void findUsers200() {

		// create users

		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		data.user("e@example.org", tenant);
		data.user("a@example.org", tenant);
		var user = data.user("t@example.org", tenant);
		var otherUser = data.user();

		// sorting - with default sort

		var users = assert200(() -> client.findUsers(auth(user), tenantId, null, null, null, null));
		assertTrue(users.getContent().stream().noneMatch(u -> u.getUserId().equals(otherUser.getUserId())));
		assertSorted(users.getContent(), UserVO::getEmail, Comparator.comparing(UserVO::getEmail));

		// sorting - with email sort desc

		users = assert200(() -> client.findUsers(auth(user), tenantId, null, null, List.of("email,desc"), null));
		assertSorted(users.getContent(), UserVO::getEmail, Comparator.comparing(UserVO::getEmail).reversed());

		// filtering - with email sort desc

		users = assert200(() -> client.findUsers(auth(user), tenantId, null, null, null, "t"));
		assertTrue(users.getContent().stream().allMatch(u -> u.getUserId().equals(user.getUserId())));
	}

	@DisplayName("findUsers(401): no token")
	@Test
	@Override
	public void findUsers401() {
		assert401(() -> client.findUsers(null, data.tenantId(), null, null, null, null));
	}

	@DisplayName("findUsers(404): tenant not exists")
	@Test
	@Override
	public void findUsers404() {
		assert404(() -> client.findUsers(auth(), data.tenantId(), null, null, null, null));
	}

	@DisplayName("findUsers(404): tenant not assigned")
	@Test
	public void findUsers404TenantNotAssigned() {
		assert404(() -> client.findUsers(auth(), data.tenant().getTenantId(), null, null, null, null));
	}

	@DisplayName("findUser(404): tenant deleted")
	@Test
	public void findUsers404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		assert404(() -> client.findUsers(auth(user), tenant.getTenantId(), null, null, null, null));
	}

	@DisplayName("findUser(200): success")
	@Test
	@Override
	public void findUser200() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var actual = assert200(() -> client.findUser(auth(user), tenant.getTenantId(), user.getUserId()));
		assertEquals(user.getUserId(), actual.getUserId(), "tenantId");
		assertEquals(user.getEmail(), actual.getEmail(), "email");
		assertEquals(user.getCreated(), actual.getCreated(), "created");
		assertEquals(user.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findUser(401): no token")
	@Test
	@Override
	public void findUser401() {
		assert401(() -> client.findUser(null, data.tenantId(), data.userId()));
	}

	@DisplayName("findUser(404): user not exists")
	@Test
	@Override
	public void findUser404() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert404(() -> client.findUser(auth(user), tenant.getTenantId(), data.userId()));
	}

	@DisplayName("findUser(404): tenant not exists")
	@Test
	public void findUser404TenantNotExists() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert404(() -> client.findUser(auth(user), data.tenantId(), user.getUserId()));
	}

	@DisplayName("findUser(404): tenant deleted")
	@Test
	public void findUser404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		assert404(() -> client.findUser(auth(user), tenant.getTenantId(), user.getUserId()));
	}

	@DisplayName("findUser(404): user not assigned to same tenant")
	@Test
	public void findUser404TenantNotAssigned() {
		var otherTenant = data.tenant();
		var otherUser = data.user(otherTenant);
		var user = data.user();
		assert404(() -> client.findUser(auth(user), otherTenant.getTenantId(), otherUser.getUserId()));
	}

	@DisplayName("createUser(201): with new user")
	@Test
	@Override
	public void createUser201() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new UserCreateVO().setEmail(data.userEmail());
		var created = assert201(() -> client.createUser(auth(user), tenant.getTenantId(), vo));
		assertNotNull(created.getUserId(), "userId");
		assertEquals(vo.getEmail(), created.getEmail(), "email");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findUser(auth(user), tenant.getTenantId(), created.getUserId())));
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
		data.assertAssignment(vo.getEmail(), tenant.getTenantId());
	}

	@DisplayName("createUser(201): with existing user")
	@Test
	public void createUser201existing() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var otherTenant = data.tenant();
		var otherUser = data.user(otherTenant);
		var vo = new UserCreateVO().setEmail(otherUser.getEmail());
		var created = assert201(() -> client.createUser(auth(user), tenant.getTenantId(), vo));
		assertNotNull(created.getUserId(), "userId");
		assertEquals(otherUser.getUserId(), created.getUserId(), "userId");
		assertEquals(vo.getEmail(), created.getEmail(), "email");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findUser(auth(user), tenant.getTenantId(), created.getUserId())));
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
		data.assertAssignment(otherUser.getEmail(), otherTenant.getTenantId(), tenant.getTenantId());
	}

	@DisplayName("createUser(400): is beanvalidation active")
	@Test
	@Override
	public void createUser400() {
		assert400(() -> client.createUser(auth(), data.tenantId(), new UserCreateVO()));
	}

	@DisplayName("createUser(401): no token")
	@Test
	@Override
	public void createUser401() {
		assert401(() -> client.createUser(null, data.tenantId(), new UserCreateVO().setEmail(data.userEmail())));
	}

	@DisplayName("createUser(404): tenant not exists")
	@Override
	@Test
	public void createUser404() {
		assert404(() -> client.createUser(auth(), data.tenantId(), new UserCreateVO().setEmail(data.userEmail())));
	}

	@DisplayName("createUser(404): user not assigned to same tenant")
	@Test
	public void createUser404TenantNotAssigned() {
		assert404(() -> client.createUser(auth(), data.tenant().getTenantId(),
				new UserCreateVO().setEmail(data.userEmail())));
	}

	@DisplayName("createUser(404): tenant deleted")
	@Test
	public void createUser404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		assert404(() -> client.createUser(auth(user), tenant.getTenantId(),
				new UserCreateVO().setEmail(data.userEmail())));
	}

	@DisplayName("createUser(409): email already exists")
	@Test
	@Override
	public void createUser409() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert409(() -> client.createUser(auth(user), tenant.getTenantId(),
				new UserCreateVO().setEmail(user.getEmail())));
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}

	@DisplayName("deleteUser(204): success")
	@Test
	@Override
	public void deleteUser204() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var otherTenant = data.tenant();
		var otherUser = data.user(otherTenant, tenant);
		assert204(() -> client.deleteUser(auth(user), tenant.getTenantId(), otherUser.getUserId()));
		data.assertAssignment(otherUser.getEmail(), otherTenant.getTenantId());
	}

	@DisplayName("deleteUser(401): no token")
	@Test
	@Override
	public void deleteUser401() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert401(() -> client.deleteUser(null, tenant.getTenantId(), user.getUserId()));
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}

	@DisplayName("deleteUser(404): user not exists")
	@Test
	@Override
	public void deleteUser404() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert404(() -> client.deleteUser(auth(user), tenant.getTenantId(), data.userId()));
	}

	@DisplayName("deleteUser(404): tenant not exists")
	@Test
	public void deleteUser404TenantNotExists() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert404(() -> client.deleteUser(auth(user), data.tenantId(), user.getUserId()));
	}

	@DisplayName("deleteUser(404): user not assigned to same tenant")
	@Test
	public void deleteUser404TenantNotAssigned() {
		var otherTenant = data.tenant();
		var otherUser = data.user(otherTenant);
		var user = data.user();
		assert404(() -> client.deleteUser(auth(user), otherTenant.getTenantId(), otherUser.getUserId()));
		data.assertAssignment(otherUser.getEmail(), otherTenant.getTenantId());
	}

	@DisplayName("deleteUser(404): tenant deleted")
	@Test
	public void deleteUser404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		var otherUser = data.user(tenant);
		assert404(() -> client.deleteUser(auth(user), tenant.getTenantId(), otherUser.getUserId()));
	}
}
