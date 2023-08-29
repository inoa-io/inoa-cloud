package io.inoa.fleet.registry.rest.management;

import static io.inoa.fleet.api.HttpResponseAssertions.assert204;
import static io.inoa.fleet.api.HttpResponseAssertions.assert401;
import static io.inoa.fleet.api.HttpResponseAssertions.assert409;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.AbstractTest;
import io.inoa.fleet.api.TenantsApiTestClient;
import io.inoa.fleet.api.TenantsApiTestSpec;
import io.inoa.fleet.model.TenantCreateVO;
import io.inoa.fleet.model.TenantUpdateVO;
import jakarta.inject.Inject;

/**
 * Test for {@link io.inoa.fleet.api.TenantsApi}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("management: configuration")
public class TenantApiTest extends AbstractTest implements TenantsApiTestSpec {

	@Inject
	TenantsApiTestClient client;

	@DisplayName("createTenant -> 201: ok")
	@Test
	@Override
	public void createTenant201() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var tenantCreateVO = new TenantCreateVO().tenantId("grayc").name("GrayC GmbH").enabled(true)
				.gatewayIdPattern("IS[A-Z]{2}[0-9]{2}-[A-F0-9]{12}");
		assert201(() -> client.createTenant(auth, tenantCreateVO));
	}

	@DisplayName("createTenant -> 400: wrong pattern format")
	@Test
	@Override
	public void createTenant400() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var tenantCreateVO = new TenantCreateVO().tenantId("grayc").name("GrayC GmbH").enabled(true)
				.gatewayIdPattern("Gateway[0-9]**");
		assert400(() -> client.createTenant(auth, tenantCreateVO));
	}

	@DisplayName("createTenant -> 401: no token")
	@Test
	@Override
	public void createTenant401() {
		assert401(() -> client.createTenant(null, new TenantCreateVO()));
	}

	@DisplayName("createTenant -> 409: same tenant Id")
	@Test
	@Override
	public void createTenant409() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var tenantCreateVO = new TenantCreateVO().tenantId(tenant.getTenantId()).name("GrayC GmbH").enabled(true)
				.gatewayIdPattern("IS[A-Z]{2}[0-9]{2}-[A-F0-9]{12}");
		assert409(() -> client.createTenant(auth, tenantCreateVO));
	}

	@DisplayName("deleteTenant -> 204: ok")
	@Test
	@Override
	public void deleteTenant204() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var tenantCreateVO = new TenantCreateVO().tenantId("grayc").name("GrayC GmbH").enabled(true)
				.gatewayIdPattern("IS[A-Z]{2}[0-9]{2}-[A-F0-9]{12}");
		// Create a tenant
		assert201(() -> client.createTenant(auth, tenantCreateVO));
		// Delete this tenant
		assert204(() -> client.deleteTenant(auth, tenantCreateVO.getTenantId()));
		// Check that it cannot be found by Id
		assert404("Tenant does not exist.", () -> client.findTenant(auth, tenantCreateVO.getTenantId()));
		// Check that it cannot be found by finding "all"
		var pre = client.findTenants(auth);
		var tenants = assert200(() -> client.findTenants(auth));
		Assertions.assertEquals(1, tenants.size());
	}

	@DisplayName("deleteTenant -> 401: no token")
	@Test
	@Override
	public void deleteTenant401() {
		assert401(() -> client.deleteTenant(null, "inoa"));
	}

	@DisplayName("deleteTenant -> 404: not found")
	@Test
	@Override
	public void deleteTenant404() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		assert404("Tenant does not exist.", () -> client.deleteTenant(auth, "gibtsni"));
	}

	@DisplayName("findTenant -> 200: ok")
	@Test
	@Override
	public void findTenant200() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var result = assert200(() -> client.findTenant(auth, tenant.getTenantId()));
		Assertions.assertEquals(tenant.getTenantId(), result.getTenantId());
	}

	@DisplayName("findTenant -> 401: no token")
	@Test
	@Override
	public void findTenant401() {
		assert401(() -> client.findTenant(null, "inoa"));
	}

	@DisplayName("findTenant -> 404: not found")
	@Test
	@Override
	public void findTenant404() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		assert404("Tenant does not exist.", () -> client.findTenant(auth, "gibtsni"));
	}

	@DisplayName("findTenants -> 200: ok")
	@Test
	@Override
	public void findTenants200() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var result = assert200(() -> client.findTenants(auth));
		Assertions.assertFalse(result.isEmpty());
	}

	@DisplayName("findTenants -> 401: no token")
	@Test
	@Override
	public void findTenants401() {
		assert401(() -> client.findTenants(null));
	}

	@DisplayName("updateTenant -> 200: ok")
	@Test
	@Override
	public void updateTenant200() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var tenantUpdateVO = new TenantUpdateVO().name("INOA-UPDATED").enabled(true)
				.gatewayIdPattern(tenant.getGatewayIdPattern());
		var response = assert200(() -> client.updateTenant(auth, tenant.getTenantId(), tenantUpdateVO));
		Assertions.assertEquals("INOA-UPDATED", response.getName());
	}

	@DisplayName("updateTenant -> 400: bad pattern")
	@Test
	@Override
	public void updateTenant400() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		var tenantUpdateVO = new TenantUpdateVO().name("INOA").enabled(true).gatewayIdPattern("Gateway[0-9]**");
		assert400(() -> client.updateTenant(auth, tenant.getTenantId(), tenantUpdateVO));
	}

	@DisplayName("updateTenant -> 401: no token")
	@Test
	@Override
	public void updateTenant401() {
		assert401(() -> client.updateTenant(null, "inoa", new TenantUpdateVO()));
	}

	@DisplayName("updateTenant -> 404: not found")
	@Test
	@Override
	public void updateTenant404() {
		var tenant = data.tenant();
		var auth = auth(tenant);
		assert404("Tenant does not exist.", () -> client.updateTenant(auth, "gibtsni", new TenantUpdateVO()));
	}
}
