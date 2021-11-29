package io.inoa.fleet.registry.test;

import java.util.UUID;

import io.inoa.cnpm.tenant.management.TenantCreateVO;
import io.inoa.cnpm.tenant.management.TenantUpdateVO;
import io.inoa.cnpm.tenant.management.TenantVO;
import io.inoa.cnpm.tenant.management.TenantsApiClient;
import io.inoa.fleet.registry.infrastructure.TestAssertions;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class TenantServiceClient {

	private final KeycloakClient keycloak;
	private final TestAssertions assertions;
	private final TenantsApiClient tenantsClient;

	public TenantVO find(String tenantId) {
		return assertions.assert200(() -> tenantsClient.findTenant(keycloak.auth("admin"), tenantId));
	}

	public TenantVO create() {
		return create(UUID.randomUUID().toString().substring(0, 10), UUID.randomUUID().toString(), true);
	}

	public TenantVO create(String tenantId, String name, boolean enabled) {
		var vo = new TenantCreateVO().setEnabled(enabled).setName(name);
		return assertions.assert201(() -> tenantsClient.createTenant(keycloak.auth("admin"), tenantId, vo));
	}

	public TenantVO update(String tenantId, String name, boolean enabled) {
		var vo = new TenantUpdateVO().setEnabled(enabled).setName(name);
		return assertions.assert200(() -> tenantsClient.updateTenant(keycloak.auth("admin"), tenantId, vo));
	}
}
