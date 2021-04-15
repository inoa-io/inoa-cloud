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
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.AbstractTest;

/**
 * Test for {@link TenantApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: tenant")
public class TenantApiTest extends AbstractTest implements TenantApiTestSpec {

	@Inject
	TenantApiTestClient client;

	@DisplayName("getTenants(200): success")
	@Test
	@Override
	public void getTenants200() {

		// create tenants

		var tenant1 = data.tenant("abc2");
		var tenant2 = data.tenant("abc1");
		var tenant3 = data.tenant("aaa");

		// execute

		var expected = List.of(tenant3.getExternalId(), tenant2.getExternalId(), tenant1.getExternalId());
		var actual = assert200(() -> client.getTenants(bearerAdmin()));
		assertEquals(expected, actual.stream().map(TenantVO::getTenantId).collect(Collectors.toList()), "ordering");
	}

	@DisplayName("getTenants(401): no token")
	@Test
	@Override
	public void getTenants401() {
		assert401(() -> client.getTenants(null));
	}

	@DisplayName("getTenant(200): success")
	@Test
	@Override
	public void getTenant200() {
		var expected = data.tenant();
		var actual = assert200(() -> client.getTenant(bearerAdmin(), expected.getExternalId()));
		assertEquals(expected.getExternalId(), actual.getTenantId(), "tenantId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("getTenant(401): no token")
	@Test
	@Override
	public void getTenant401() {
		assert401(() -> client.getTenant(null, UUID.randomUUID()));
	}

	@DisplayName("getTenant(404): not found")
	@Test
	@Override
	public void getTenant404() {
		assert404(() -> client.getTenant(bearerAdmin(), UUID.randomUUID()));
	}

	@DisplayName("createTenant(201): with mandatory properties")
	@Test
	@Override
	public void createTenant201() {
		var vo = new TenantCreateVO().setName(data.tenantName());
		var created = assert201(() -> client.createTenant(bearerAdmin(), vo));
		assertNotNull(created.getTenantId(), "tenantId");
		assertEquals(true, created.getEnabled(), "enabled");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.getTenant(bearerAdmin(), created.getTenantId())), "vo");
	}

	@DisplayName("createTenant(201): with optional properties")
	@Test
	public void createTenant201All() {
		var vo = new TenantCreateVO().setTenantId(UUID.randomUUID()).setEnabled(false).setName(data.tenantName());
		var created = assert201(() -> client.createTenant(bearerAdmin(), vo));
		assertEquals(vo.getTenantId(), created.getTenantId(), "tenantId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.getTenant(bearerAdmin(), created.getTenantId())), "vo");
	}

	@DisplayName("createTenant(400): check bean validation")
	@Test
	@Override
	public void createTenant400() {
		assert400(() -> client.createTenant(bearerAdmin(), new TenantCreateVO()));
		assertEquals(0, data.countTenants(), "created");
	}

	@DisplayName("createTenant(401): no token")
	@Test
	@Override
	public void createTenant401() {
		assert401(() -> client.createTenant(null, new TenantCreateVO().setName(data.tenantName())));
		assertEquals(0, data.countTenants(), "created");
	}

	@DisplayName("createTenant(409): id exists")
	@Test
	public void createTenant409Id() {
		var existing = data.tenant();
		assert409(() -> client.createTenant(bearerAdmin(), new TenantCreateVO()
				.setTenantId(existing.getExternalId())
				.setName(data.tenantName())));
		assertEquals(1, data.countTenants(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("createTenant(409): name exists")
	@Test
	@Override
	public void createTenant409() {
		var existing = data.tenant();
		assert409(() -> client.createTenant(bearerAdmin(), new TenantCreateVO().setName(existing.getName())));
		assertEquals(1, data.countTenants(), "created");
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateTenant(200): update nothing")
	@Test
	@Override
	public void updateTenant200() {
		var existing = data.tenant();
		var vo = new TenantUpdateVO().setName(null).setEnabled(null);
		var updated = assert200(() -> client.updateTenant(bearerAdmin(), existing.getExternalId(), vo));
		assertEquals(existing.getExternalId(), updated.getTenantId(), "tenantId");
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
		var updated = assert200(() -> client.updateTenant(bearerAdmin(), existing.getExternalId(), vo));
		assertEquals(existing.getExternalId(), updated.getTenantId(), "tenantId");
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
		var updated = assert200(() -> client.updateTenant(bearerAdmin(), existing.getExternalId(), vo));
		assertEquals(existing.getExternalId(), updated.getTenantId(), "tenantId");
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
		var updated = assert200(() -> client.updateTenant(bearerAdmin(), tenant.getExternalId(), vo));
		assertEquals(tenant.getExternalId(), updated.getTenantId(), "tenantId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
	}

	@DisplayName("updateTenant(400): check bean validation")
	@Test
	@Override
	public void updateTenant400() {
		var existing = data.tenant();
		assert400(() -> client.updateTenant(bearerAdmin(), existing.getExternalId(), new TenantUpdateVO().setName("")));
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateTenant(401): no token")
	@Test
	@Override
	public void updateTenant401() {
		var existing = data.tenant();
		assert401(() -> client.updateTenant(null, existing.getExternalId(),
				new TenantUpdateVO().setName(data.tenantName())));
		assertEquals(existing, data.find(existing), "entity changed");
	}

	@DisplayName("updateTenant(404): not found")
	@Test
	@Override
	public void updateTenant404() {
		assert404(() -> client.updateTenant(bearerAdmin(), UUID.randomUUID(), new TenantUpdateVO()));
	}

	@DisplayName("updateTenant(409): name exists")
	@Test
	@Override
	public void updateTenant409() {
		var tenant = data.tenant();
		var other = data.tenant();
		var vo = new TenantUpdateVO().setName(other.getName()).setEnabled(null);
		assert409(() -> client.updateTenant(bearerAdmin(), tenant.getExternalId(), vo));
		assertEquals(tenant, data.find(tenant), "tenant changed");
		assertEquals(other, data.find(other), "other changed");
	}

	@DisplayName("deleteTenant(204): success")
	@Test
	@Override
	public void deleteTenant204() {
		var tenant = data.tenant();
		assert204(() -> client.deleteTenant(bearerAdmin(), tenant.getExternalId()));
		assertEquals(0, data.countTenants(), "not deleted");
	}

	@DisplayName("deleteTenant(400): gateways exist")
	@Test
	@Override
	public void deleteTenant400() {
		var tenant = data.gateway().getTenant();
		assert400(() -> client.deleteTenant(bearerAdmin(), tenant.getExternalId()));
		assertEquals(1, data.countTenants(), "deleted");
		assertEquals(1, data.countGateways(), "deleted");
	}

	@DisplayName("deleteTenant(401): no token")
	@Test
	@Override
	public void deleteTenant401() {
		var tenant = data.tenant();
		assert401(() -> client.deleteTenant(null, tenant.getExternalId()));
		assertEquals(1, data.countTenants(), "deleted");
	}

	@DisplayName("deleteTenant(404): tenant not found")
	@Test
	@Override
	public void deleteTenant404() {
		assert404(() -> client.deleteTenant(bearerAdmin(), UUID.randomUUID()));
	}
}
