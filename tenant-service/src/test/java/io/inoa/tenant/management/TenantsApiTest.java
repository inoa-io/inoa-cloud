package io.inoa.tenant.management;

import static io.inoa.tenant.HttpResponseAssertions.assert204;
import static io.inoa.tenant.HttpResponseAssertions.assert401;
import static io.inoa.tenant.HttpResponseAssertions.assert404;
import static io.inoa.tenant.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.tenant.AbstractTest;
import jakarta.inject.Inject;

/**
 * Test for {@link TenantsApi}.
 *
 * @author Stephan Schnabel
 */
public class TenantsApiTest extends AbstractTest implements TenantsApiTestSpec {

	@Inject
	TenantsApiTestClient client;

	@DisplayName("findTenants(200): tenants with order found")
	@Test
	@Override
	public void findTenants200() {

		// create tenants

		var tenant1 = data.tenant(data.tenantId(), "abc2", true);
		var tenant2 = data.tenant(data.tenantId(), "abc1", true);
		var tenant3 = data.tenant(data.tenantId(), "aaa3", true);
		var user = data.user(tenant1, tenant2, tenant3);
		data.tenant(data.tenantId(), "aaa5", true);

		// execute

		var expected = List.of(tenant3.getTenantId(), tenant2.getTenantId(), tenant1.getTenantId());
		var actual = assert200(() -> client.findTenants(auth(user)));
		assertEquals(expected, actual.stream().map(TenantVO::getTenantId).collect(Collectors.toList()), "ordering");
	}

	@DisplayName("findTenants(200): no tenant found")
	@Test
	public void findTenants200Empty() {
		assertEquals(List.of(), assert200(() -> client.findTenants(auth())));
	}

	@DisplayName("findTenants(401): no token")
	@Test
	@Override
	public void findTenants401() {
		assert401(() -> client.findTenants(null));
	}

	@DisplayName("findTenant(200): success")
	@Test
	@Override
	public void findTenant200() {
		var expected = data.tenant();
		var actual = assert200(() -> client.findTenant(auth(expected), expected.getTenantId()));
		assertEquals(expected.getTenantId(), actual.getTenantId(), "tenantId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findTenant(401): no token")
	@Test
	@Override
	public void findTenant401() {
		assert401(() -> client.findTenant(null, data.tenantId()));
		assert401(() -> client.findTenant(auth((String) null), data.tenantId()));
	}

	@DisplayName("findTenant(404): not exists")
	@Test
	@Override
	public void findTenant404() {
		assert404(() -> client.findTenant(auth(), data.tenantId()));
	}

	@DisplayName("findTenant(404): not assigned")
	@Test
	public void findTenant404NotAssigned() {
		assert404(() -> client.findTenant(auth(), data.tenant().getTenantId()));
	}

	@DisplayName("createTenant(201): with mandatory properties")
	@Test
	@Override
	public void createTenant201() {
		var email = data.userEmail();
		var tenantId = data.tenantId();
		var vo = new TenantCreateVO().setName(data.tenantName());
		var created = assert201(() -> client.createTenant(auth(email), tenantId, vo));
		assertEquals(tenantId, created.getTenantId(), "tenantId");
		assertEquals(true, created.getEnabled(), "enabled");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findTenant(auth(email), tenantId)), "vo");
		data.assertAssignment(email, tenantId);
	}

	@DisplayName("createTenant(201): with optional properties")
	@Test
	public void createTenant201All() {
		var email = data.userEmail();
		var tenantId = data.tenantId();
		var vo = new TenantCreateVO().setEnabled(false).setName(data.tenantName());
		var created = assert201(() -> client.createTenant(auth(email), tenantId, vo));
		assertEquals(tenantId, created.getTenantId(), "tenantId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findTenant(auth(email), tenantId)), "vo");
		data.assertAssignment(email, tenantId);
	}

	@DisplayName("createTenant(201): with existing user")
	@Test
	public void createTenant201ExistingUser() {
		var otherTenant = data.tenant();
		var user = data.user(otherTenant);
		var tenantId = data.tenantId();
		var vo = new TenantCreateVO().setName(data.tenantName());
		var created = assert201(() -> client.createTenant(auth(user), tenantId, vo));
		assertEquals(created, assert200(() -> client.findTenant(auth(user), tenantId)), "vo");
		data.assertAssignment(user.getEmail(), tenantId, otherTenant.getTenantId());
	}

	@DisplayName("createTenant(400): is beanvalidation active")
	@Test
	@Override
	public void createTenant400() {
		assert400(() -> client.createTenant(auth(), data.tenantId(), new TenantCreateVO()));
	}

	@DisplayName("createTenant(401): no token")
	@Test
	@Override
	public void createTenant401() {
		assert401(() -> client.createTenant(null, new TenantCreateVO().setName(data.tenantName())));
	}

	@DisplayName("createTenant(409): tenantId exists")
	@Test
	@Override
	public void createTenant409() {
		var tenantId = data.tenant().getTenantId();
		assert409(() -> client.createTenant(auth(), tenantId, new TenantCreateVO().setName(data.tenantName())));
	}

	@DisplayName("updateTenant(200): update nothing")
	@Test
	@Override
	public void updateTenant200() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(null).setEnabled(null);
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(tenant.getName(), updated.getName(), "name");
		assertEquals(tenant.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertEquals(tenant.getUpdated(), updated.getUpdated(), "updated");
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}

	@DisplayName("updateTenant(200): update name only")
	@Test
	public void updateTenant200Name() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(data.tenantName()).setEnabled(null);
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(tenant.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}

	@DisplayName("updateTenant(200): update enabled only")
	@Test
	public void updateTenant200Enabled() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(null).setEnabled(false);
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(tenant.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}

	@DisplayName("updateTenant(200): update unchanged")
	@Test
	public void updateTenant200Unchanged() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(tenant.getName()).setEnabled(tenant.getEnabled());
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(tenant.getName(), updated.getName(), "name");
		assertEquals(tenant.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertEquals(tenant.getUpdated(), updated.getUpdated(), "updated");
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}

	@DisplayName("updateTenant(200): update all")
	@Test
	public void updateTenant200All() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(data.tenantName()).setEnabled(false);
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}

	@DisplayName("updateTenant(400): is beanvalidation active")
	@Test
	@Override
	public void updateTenant400() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert400(() -> client.updateTenant(auth(user), tenant.getTenantId(), new TenantUpdateVO().setName("")));
	}

	@DisplayName("updateTenant(401): no token")
	@Test
	@Override
	public void updateTenant401() {
		var tenantId = data.tenant().getTenantId();
		assert401(() -> client.updateTenant(null, tenantId, new TenantUpdateVO().setName(data.tenantName())));
	}

	@DisplayName("updateTenant(404): not found")
	@Test
	@Override
	public void updateTenant404() {
		assert404(() -> client.updateTenant(auth(), data.tenantId(), new TenantUpdateVO().setName(data.tenantName())));
	}

	@DisplayName("updateTenant(404): not assigned")
	@Test
	public void updateTenant404NotAssigned() {
		var tenantId = data.tenant().getTenantId();
		assert404(() -> client.updateTenant(auth(), tenantId, new TenantUpdateVO().setName(data.tenantName())));
	}

	@DisplayName("deleteTenant(204): success")
	@Test
	@Override
	public void deleteTenant204() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var otherTenant = data.tenant();
		var otherUser = data.user(tenant, otherTenant);
		assert204(() -> client.deleteTenant(auth(user), tenant.getTenantId()));
		data.assertAssignment(user.getEmail());
		data.assertAssignment(otherUser.getEmail(), otherTenant.getTenantId());
	}

	@DisplayName("deleteTenant(401): no token")
	@Test
	@Override
	public void deleteTenant401() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert401(() -> client.deleteTenant(null, tenant.getTenantId()));
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}

	@DisplayName("deleteTenant(404): not found")
	@Test
	@Override
	public void deleteTenant404() {
		assert404(() -> client.deleteTenant(auth(), data.tenantId()));
	}

	@DisplayName("deleteTenant(404): not assigned")
	@Test
	public void deleteTenant404NotAssigned() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert404(() -> client.deleteTenant(auth(), tenant.getTenantId()));
		data.assertAssignment(user.getEmail(), tenant.getTenantId());
	}
}
