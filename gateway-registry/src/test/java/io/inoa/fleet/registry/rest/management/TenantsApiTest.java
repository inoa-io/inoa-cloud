package io.inoa.fleet.registry.rest.management;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.AbstractTest;
import jakarta.inject.Inject;

/**
 * Test for {@link TenantsApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: tenants")
public class TenantsApiTest extends AbstractTest implements TenantsApiTestSpec {

	@Inject
	TenantsApiTestClient client;

	@DisplayName("findTenants(200): success")
	@Test
	@Override
	public void findTenants200() {

		// create tenants

		var tenant1 = data.tenant(data.tenantId(), "abc2", true);
		var tenant2 = data.tenant(data.tenantId(), "abc1", true);
		var tenant3 = data.tenant(data.tenantId(), "aaa3", true);

		// execute

		var expected = List.of(tenant3.getTenantId(), tenant2.getTenantId(), tenant1.getTenantId());
		var actual = assert200(() -> client.findTenants(auth()));
		assertEquals(expected, actual.stream().map(TenantVO::getTenantId).collect(Collectors.toList()), "ordering");
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
		var actual = assert200(() -> client.findTenant(auth(), expected.getTenantId()));
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
		assert401(() -> client.findTenant(null, "junit"));
	}

	@DisplayName("findTenant(404): not found")
	@Test
	@Override
	public void findTenant404() {
		assert404("Tenant not found.", () -> client.findTenant(auth(), "junit"));
	}

	@DisplayName("createTenant(201): with mandatory properties")
	@Test
	@Override
	public void createTenant201() {
		var vo = new TenantCreateVO().setTenantId("junit").setName(data.tenantName());
		var created = assert201(() -> client.createTenant(auth(), vo));
		assertNotNull(created.getTenantId(), "tenantId");
		assertEquals(true, created.getEnabled(), "enabled");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findTenant(auth(), created.getTenantId())), "vo");
	}

	@DisplayName("createTenant(201): with optional properties")
	@Test
	public void createTenant201All() {
		var vo = new TenantCreateVO().setTenantId("junit").setEnabled(false).setName(data.tenantName());
		var created = assert201(() -> client.createTenant(auth(), vo));
		assertEquals(vo.getTenantId(), created.getTenantId(), "tenantId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findTenant(auth(), created.getTenantId())), "vo");
	}

	@DisplayName("createTenant(400): is beanvalidation active")
	@Test
	@Override
	public void createTenant400() {
		assert400(() -> client.createTenant(auth(), new TenantCreateVO()));
		assertEquals(0, data.countTenants(), "created");
	}

	@DisplayName("createTenant(401): no token")
	@Test
	@Override
	public void createTenant401() {
		assert401(() -> client.createTenant(null, new TenantCreateVO().setName(data.tenantName())));
		assertEquals(0, data.countTenants(), "created");
	}

	@DisplayName("createTenant(409): id & name exists")
	@Test
	@Override
	public void createTenant409() {
		var existing = data.tenant();
		assert409(() -> client.createTenant(auth(), new TenantCreateVO()
				.setTenantId(existing.getTenantId())
				.setName(existing.getName())));
		assertEquals(1, data.countTenants(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("createTenant(409): id exists")
	@Test
	public void createTenant409Id() {
		var existing = data.tenant();
		assert409(() -> client.createTenant(auth(), new TenantCreateVO()
				.setTenantId(existing.getTenantId())
				.setName(data.tenantName())));
		assertEquals(1, data.countTenants(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("createTenant(409): name exists")
	@Test
	public void createTenant409Name() {
		var existing = data.tenant();
		assert409(() -> client.createTenant(auth(), new TenantCreateVO()
				.setTenantId(data.tenantId())
				.setName(existing.getName())));
		assertEquals(1, data.countTenants(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateTenant(200): update nothing")
	@Test
	@Override
	public void updateTenant200() {
		var existing = data.tenant();
		var vo = new TenantUpdateVO().setName(null).setEnabled(null);
		var updated = assert200(() -> client.updateTenant(auth(), existing.getTenantId(), vo));
		assertEquals(existing.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(existing.getName(), updated.getName(), "name");
		assertEquals(existing.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(existing.getCreated(), updated.getCreated(), "created");
		assertEquals(existing.getUpdated(), updated.getUpdated(), "updated");
	}

	@DisplayName("updateTenant(200): update name only")
	@Test
	public void updateTenant200Name() {
		var existing = data.tenant();
		var vo = new TenantUpdateVO().setName(data.tenantName()).setEnabled(null);
		var updated = assert200(() -> client.updateTenant(auth(), existing.getTenantId(), vo));
		assertEquals(existing.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(existing.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(existing.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(existing.getUpdated()), "updated");
	}

	@DisplayName("updateTenant(200): update unchanged")
	@Test
	public void updateTenant200Unchanged() {
		var existing = data.tenant();
		var vo = new TenantUpdateVO().setName(existing.getName()).setEnabled(existing.getEnabled());
		var updated = assert200(() -> client.updateTenant(auth(), existing.getTenantId(), vo));
		assertEquals(existing.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(existing.getName(), updated.getName(), "name");
		assertEquals(existing.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(existing.getCreated(), updated.getCreated(), "created");
		assertEquals(existing.getUpdated(), updated.getUpdated(), "updated");
	}

	@DisplayName("updateTenant(200): update all")
	@Test
	public void updateTenant200All() {
		var tenant = data.tenant();
		var vo = new TenantUpdateVO().setName(data.tenantName()).setEnabled(false);
		var updated = assert200(() -> client.updateTenant(auth(), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
	}

	@DisplayName("updateTenant(400): is beanvalidation active")
	@Test
	@Override
	public void updateTenant400() {
		var existing = data.tenant();
		assert400(() -> client.updateTenant(auth(), existing.getTenantId(), new TenantUpdateVO().setName("")));
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateTenant(401): no token")
	@Test
	@Override
	public void updateTenant401() {
		var existing = data.tenant();
		assert401(() -> client.updateTenant(null, existing.getTenantId(),
				new TenantUpdateVO().setName(data.tenantName())));
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateTenant(404): not found")
	@Test
	@Override
	public void updateTenant404() {
		assert404("Tenant not found.", () -> client.updateTenant(auth(), "junit", new TenantUpdateVO()));
	}

	@DisplayName("updateTenant(409): name exists")
	@Test
	@Override
	public void updateTenant409() {
		var tenant = data.tenant();
		var other = data.tenant();
		var vo = new TenantUpdateVO().setName(other.getName()).setEnabled(null);
		assert409(() -> client.updateTenant(auth(), tenant.getTenantId(), vo));
		assertEquals(tenant, data.find(tenant), "tenant changed");
		assertEquals(other, data.find(other), "other changed");
	}

	@DisplayName("deleteTenant(204): success")
	@Test
	@Override
	public void deleteTenant204() {
		var tenant = data.tenant();
		assert204(() -> client.deleteTenant(auth(), tenant.getTenantId()));
		assertEquals(0, data.countTenants(), "not deleted");
	}

	@DisplayName("deleteTenant(400): gateways exist")
	@Test
	@Override
	public void deleteTenant400() {
		var tenant = data.tenant();
		data.gateway(tenant);
		assert400(() -> client.deleteTenant(auth(), tenant.getTenantId()));
		assertEquals(1, data.countTenants(), "deleted");
		assertEquals(1, data.countGateways(), "deleted");
	}

	@DisplayName("deleteTenant(401): no token")
	@Test
	@Override
	public void deleteTenant401() {
		var tenant = data.tenant();
		assert401(() -> client.deleteTenant(null, tenant.getTenantId()));
		assertEquals(1, data.countTenants(), "deleted");
	}

	@DisplayName("deleteTenant(404): tenant not found")
	@Test
	@Override
	public void deleteTenant404() {
		assert404("Tenant not found.", () -> client.deleteTenant(auth(), "junit"));
	}
}
