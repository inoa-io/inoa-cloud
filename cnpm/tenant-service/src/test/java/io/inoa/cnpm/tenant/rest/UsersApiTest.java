package io.inoa.cnpm.tenant.rest;

import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert204;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert401;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert403;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert404;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.cnpm.tenant.AbstractTest;
import io.inoa.cnpm.tenant.domain.User;
import io.inoa.cnpm.tenant.messaging.CloudEventSubjectVO;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import jakarta.inject.Inject;

/**
 * Test for {@link UsersApi}.
 */
@DisplayName("api: users")
public class UsersApiTest extends AbstractTest implements UsersApiTestSpec {

	@Inject
	UsersApiTestClient client;

	@DisplayName("findUsers(200): as user")
	@Test
	@Override
	public void findUsers200() {
		findUsers200(user -> auth(user));
	}

	@DisplayName("findUsers(200): as service")
	@Test
	public void findUsers200Service() {
		findUsers200(user -> authService());
	}

	private void findUsers200(Function<User, String> auth) {

		// create users

		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		data.user("e@example.org", tenant);
		data.user("a@example.org", tenant);
		var user = data.user("t@example.org", tenant);
		var otherUser = data.user();

		// sorting - with default sort

		var users = assert200(() -> client.findUsers(auth.apply(user), tenantId, null, null, null, null));
		assertTrue(users.getContent().stream().noneMatch(u -> u.getUserId().equals(otherUser.getUserId())));
		assertSorted(users.getContent(), UserVO::getEmail, Comparator.comparing(UserVO::getEmail));

		// sorting - with email sort desc

		users = assert200(() -> client.findUsers(auth.apply(user), tenantId, null, null, List.of("email,desc"), null));
		assertSorted(users.getContent(), UserVO::getEmail, Comparator.comparing(UserVO::getEmail).reversed());

		// filtering - with email sort desc

		users = assert200(() -> client.findUsers(auth.apply(user), tenantId, null, null, null, "t"));
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

	@DisplayName("findUser(200): as user")
	@Test
	@Override
	public void findUser200() {
		findUser200(user -> auth(user));
	}

	@DisplayName("findUser(200): as service")
	@Test
	public void findUser200Service() {
		findUser200(user -> authService());
	}

	private void findUser200(Function<User, String> auth) {
		var tenant = data.tenant();
		var user = data.user();
		var assignment = data.assignment(tenant, user, UserRoleVO.EDITOR);
		var actual = assert200(() -> client.findUser(auth.apply(user), tenant.getTenantId(), user.getUserId()));
		assertEquals(user.getUserId(), actual.getUserId(), "tenantId");
		assertEquals(user.getEmail(), actual.getEmail(), "email");
		assertEquals(assignment.getRole(), actual.getRole(), "role");
		assertEquals(assignment.getCreated(), actual.getCreated(), "created");
		assertEquals(assignment.getUpdated(), actual.getUpdated(), "updated");
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
		var vo = new UserCreateVO().setEmail(data.userEmail()).setRole(UserRoleVO.EDITOR);
		var created = assert201(() -> client.createUser(auth(user), tenant.getTenantId(), vo));
		assertNotNull(created.getUserId(), "userId");
		assertEquals(vo.getEmail(), created.getEmail(), "email");
		assertEquals(vo.getRole(), created.getRole(), "role");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findUser(auth(user), tenant.getTenantId(), created.getUserId())));
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		var assignment = data.assertAssignment(tenant, data.findUser(vo.getEmail()).get(), vo.getRole());
		messaging.assertAssignment(assignment, CloudEventSubjectVO.CREATE);
	}

	@DisplayName("createUser(201): with existing user")
	@Test
	public void createUser201existing() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var otherTenant = data.tenant();
		var otherUser = data.user(otherTenant);
		var vo = new UserCreateVO().setEmail(otherUser.getEmail()).setRole(UserRoleVO.VIEWER);
		var created = assert201(() -> client.createUser(auth(user), tenant.getTenantId(), vo));
		assertNotNull(created.getUserId(), "userId");
		assertEquals(otherUser.getUserId(), created.getUserId(), "userId");
		assertEquals(vo.getEmail(), created.getEmail(), "email");
		assertEquals(vo.getRole(), created.getRole(), "role");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findUser(auth(user), tenant.getTenantId(), created.getUserId())));
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		data.assertAssignment(otherTenant, otherUser, UserRoleVO.ADMIN);
		var assignment = data.assertAssignment(tenant, otherUser, vo.getRole());
		messaging.assertAssignment(assignment, CloudEventSubjectVO.CREATE);
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
		var vo = new UserCreateVO().setEmail(data.userEmail()).setRole(UserRoleVO.EDITOR);
		assert401(() -> client.createUser(null, data.tenantId(), vo));
	}

	@DisplayName("createUser(403): forbidden")
	@Test
	@Override
	public void createUser403() {
		var tenant = data.tenant();
		var vo = new UserCreateVO().setEmail(data.userEmail()).setRole(UserRoleVO.EDITOR);
		assert403(() -> client.createUser(auth(tenant, UserRoleVO.VIEWER), tenant.getTenantId(), vo));
		assert403(() -> client.createUser(auth(tenant, UserRoleVO.EDITOR), tenant.getTenantId(), vo));
	}

	@DisplayName("createUser(404): tenant not exists")
	@Override
	@Test
	public void createUser404() {
		var vo = new UserCreateVO().setEmail(data.userEmail()).setRole(UserRoleVO.EDITOR);
		assert404(() -> client.createUser(auth(), data.tenantId(), vo));
	}

	@DisplayName("createUser(404): user not assigned to same tenant")
	@Test
	public void createUser404TenantNotAssigned() {
		var vo = new UserCreateVO().setEmail(data.userEmail()).setRole(UserRoleVO.EDITOR);
		assert404(() -> client.createUser(auth(), data.tenant().getTenantId(), vo));
	}

	@DisplayName("createUser(404): tenant deleted")
	@Test
	public void createUser404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		var vo = new UserCreateVO().setEmail(data.userEmail()).setRole(UserRoleVO.EDITOR);
		assert404(() -> client.createUser(auth(user), tenant.getTenantId(), vo));
	}

	@DisplayName("createUser(409): email already exists")
	@Test
	@Override
	public void createUser409() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new UserCreateVO().setEmail(user.getEmail()).setRole(UserRoleVO.EDITOR);
		assert409(() -> client.createUser(auth(user), tenant.getTenantId(), vo));
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
	}

	@DisplayName("updateUser(200): update nothing")
	@Test
	@Override
	public void updateUser200() {

		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var user = data.user(tenant);
		var assignement = data.assignment(tenant, data.user(), UserRoleVO.VIEWER);

		var vo = new UserUpdateVO().setRole(null);
		var updated = assert200(() -> client.updateUser(auth(user), tenantId, assignement.getUser().getUserId(), vo));
		assertEquals(assignement.getUser().getUserId(), updated.getUserId(), "userId");
		assertEquals(assignement.getUser().getEmail(), updated.getEmail(), "email");
		assertEquals(assignement.getRole(), updated.getRole(), "enabled");
		assertEquals(assignement.getCreated(), updated.getCreated(), "created");
		assertEquals(assignement.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findUser(auth(user), tenantId, updated.getUserId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		data.assertAssignment(tenant, assignement.getUser(), UserRoleVO.VIEWER);
	}

	@DisplayName("updateUser(200): update role only")
	@Test
	public void updateUser200Name() {

		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var user = data.user(tenant);
		var assignement = data.assignment(tenant, data.user(), UserRoleVO.VIEWER);

		var vo = new UserUpdateVO().setRole(UserRoleVO.EDITOR);
		var updated = assert200(() -> client.updateUser(auth(user), tenantId, assignement.getUser().getUserId(), vo));
		assertEquals(assignement.getUser().getUserId(), updated.getUserId(), "userId");
		assertEquals(assignement.getUser().getEmail(), updated.getEmail(), "email");
		assertEquals(vo.getRole(), updated.getRole(), "enabled");
		assertEquals(assignement.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(assignement.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findUser(auth(user), tenantId, updated.getUserId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		var assignment = data.assertAssignment(tenant, assignement.getUser(), vo.getRole());
		messaging.assertAssignment(assignment, CloudEventSubjectVO.UPDATE);
	}

	@DisplayName("updateUser(200): update unchanged")
	@Test
	public void updateUser200Unchanged() {

		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var user = data.user(tenant);
		var assignement = data.assignment(tenant, data.user(), UserRoleVO.VIEWER);

		var vo = new UserUpdateVO().setRole(UserRoleVO.VIEWER);
		var updated = assert200(() -> client.updateUser(auth(user), tenantId, assignement.getUser().getUserId(), vo));
		assertEquals(assignement.getUser().getUserId(), updated.getUserId(), "userId");
		assertEquals(assignement.getUser().getEmail(), updated.getEmail(), "email");
		assertEquals(assignement.getRole(), updated.getRole(), "enabled");
		assertEquals(assignement.getCreated(), updated.getCreated(), "created");
		assertEquals(assignement.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findUser(auth(user), tenantId, updated.getUserId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		data.assertAssignment(tenant, assignement.getUser(), UserRoleVO.VIEWER);
	}

	@DisplayName("updateUser(200): update all")
	@Test
	public void updateUser200All() {

		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var user = data.user(tenant);
		var assignement = data.assignment(tenant, data.user(), UserRoleVO.VIEWER);

		var vo = new UserUpdateVO().setRole(UserRoleVO.EDITOR);
		var updated = assert200(() -> client.updateUser(auth(user), tenantId, assignement.getUser().getUserId(), vo));
		assertEquals(assignement.getUser().getUserId(), updated.getUserId(), "userId");
		assertEquals(assignement.getUser().getEmail(), updated.getEmail(), "email");
		assertEquals(vo.getRole(), updated.getRole(), "enabled");
		assertEquals(assignement.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(assignement.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findUser(auth(user), tenantId, updated.getUserId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		var assignment = data.assertAssignment(tenant, assignement.getUser(), vo.getRole());
		messaging.assertAssignment(assignment, CloudEventSubjectVO.UPDATE);
	}

	@DisplayName("updateUser(400): is beanvalidation active")
	@Test
	@Override
	public void updateUser400() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var request = HttpRequest
				.PATCH("/tenants/" + tenant.getTenantId() + "/users/" + user.getUserId(), Map.of("role", "nope"))
				.header(HttpHeaders.AUTHORIZATION, auth(user))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		assert400(() -> rawClient.toBlocking().exchange(request));
	}

	@DisplayName("updateUser(401): no token")
	@Test
	@Override
	public void updateUser401() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert401(() -> client.updateUser(null, tenant.getTenantId(), user.getUserId(), new UserUpdateVO()));
	}

	@DisplayName("updateUser(403): forbidden")
	@Test
	@Override
	public void updateUser403() {
		var tenant = data.tenant();
		var vo = new UserUpdateVO();
		assert403(() -> client.updateUser(auth(tenant, UserRoleVO.VIEWER), tenant.getTenantId(), data.userId(), vo));
		assert403(() -> client.updateUser(auth(tenant, UserRoleVO.EDITOR), tenant.getTenantId(), data.userId(), vo));
	}

	@DisplayName("updateUser(404): user not exists")
	@Test
	@Override
	public void updateUser404() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert404(() -> client.updateUser(auth(user), tenant.getTenantId(), data.userId(), new UserUpdateVO()));
	}

	@DisplayName("updateUser(404): tenant not exists")
	@Test
	public void updateUser404TenantNotExists() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert404(() -> client.updateUser(auth(user), data.tenantId(), user.getUserId(), new UserUpdateVO()));
	}

	@DisplayName("updateUser(404): user not assigned to same tenant")
	@Test
	public void updateUser404TenantNotAssigned() {
		var otherTenant = data.tenant();
		var otherUser = data.user(otherTenant);
		var user = data.user();
		assert404(() -> client.updateUser(auth(user), otherTenant.getTenantId(), otherUser.getUserId(),
				new UserUpdateVO().setRole(UserRoleVO.EDITOR)));
		data.assertAssignment(otherTenant, otherUser, UserRoleVO.ADMIN);
	}

	@DisplayName("updateUser(404): tenant deleted")
	@Test
	public void updateUser404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		var otherUser = data.user(tenant);
		assert404(() -> client.updateUser(auth(user), tenant.getTenantId(), otherUser.getUserId(),
				new UserUpdateVO().setRole(UserRoleVO.EDITOR)));
	}

	@DisplayName("deleteUser(204): success")
	@Test
	@Override
	public void deleteUser204() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var otherTenant = data.tenant();
		var otherUser = data.user(otherTenant, tenant);
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		data.assertAssignment(otherTenant, otherUser, UserRoleVO.ADMIN);
		var assignment = data.assertAssignment(tenant, otherUser, UserRoleVO.ADMIN);
		assert204(() -> client.deleteUser(auth(user), tenant.getTenantId(), otherUser.getUserId()));
		data.assertNoAssignment(tenant, otherUser);
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		data.assertAssignment(otherTenant, otherUser, UserRoleVO.ADMIN);
		messaging.assertAssignment(assignment, CloudEventSubjectVO.DELETE);
	}

	@DisplayName("deleteUser(401): no token")
	@Test
	@Override
	public void deleteUser401() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert401(() -> client.deleteUser(null, tenant.getTenantId(), user.getUserId()));
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
	}

	@DisplayName("deleteUser(403): forbidden")
	@Test
	@Override
	public void deleteUser403() {
		var tenant = data.tenant();
		assert403(() -> client.deleteUser(auth(tenant, UserRoleVO.VIEWER), tenant.getTenantId(), data.userId()));
		assert403(() -> client.deleteUser(auth(tenant, UserRoleVO.EDITOR), tenant.getTenantId(), data.userId()));
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
		data.assertAssignment(otherTenant, otherUser, UserRoleVO.ADMIN);
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
