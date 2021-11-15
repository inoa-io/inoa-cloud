package io.inoa.fleet.registry.rest.management;

import static io.inoa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

	@DisplayName("findTenant(200): success")
	@Test
	@Override
	public void findTenant200() {
		var tenant = data.tenant();
		var actual = assert200(() -> client.findTenant(auth(tenant), tenant.getTenantId()));
		assertEquals(tenant.getTenantId(), actual.getTenantId(), "tenantId");
		assertEquals(tenant.getName(), actual.getName(), "name");
		assertEquals(tenant.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), actual.getCreated(), "created");
		assertEquals(tenant.getUpdated(), actual.getUpdated(), "updated");
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
		assert404("Tenant not found.", () -> client.findTenant(auth(data.tenantId()), "junit"));
	}

	@DisplayName("findTenant(404): not assigned")
	@Test
	public void findTenant404NotAssigned() {
		assert404("Tenant not found.", () -> client.findTenant(auth(data.tenantId()), data.tenantId()));
	}

	@DisplayName("findTenant(404): deleted")
	@Test
	public void findTenant404Deleted() {
		var tenant = data.tenant(true, true);
		assert404("Tenant not found.", () -> client.findTenant(auth(tenant), tenant.getTenantId()));
	}
}
