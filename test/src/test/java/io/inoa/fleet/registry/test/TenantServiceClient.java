package io.inoa.fleet.registry.test;

import io.inoa.cloud.tenant.TenantCreateVO;
import io.inoa.cloud.tenant.TenantVO;
import io.inoa.cloud.tenant.TenantsApiClient;
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

	public TenantVO create(String tenantId, String name, boolean enabled) {
		var vo = new TenantCreateVO().setEnabled(enabled).setName(name);
		return assertions.assert201(() -> tenantsClient.createTenant(keycloak.auth("admin"), tenantId, vo));
	}
}
