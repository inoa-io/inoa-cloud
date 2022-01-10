package io.inoa.test.client;

import java.util.UUID;

import io.inoa.cnpm.tenant.management.TenantCreateVO;
import io.inoa.cnpm.tenant.management.TenantUpdateVO;
import io.inoa.cnpm.tenant.management.TenantVO;
import io.inoa.cnpm.tenant.management.TenantsApiClient;
import io.inoa.cnpm.tenant.management.UserCreateVO;
import io.inoa.cnpm.tenant.management.UserRoleVO;
import io.inoa.cnpm.tenant.management.UserVO;
import io.inoa.cnpm.tenant.management.UsersApiClient;
import io.inoa.test.infrastructure.TestAssertions;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class TenantManagment {

	private final TestAssertions assertions;
	private final TenantsApiClient tenantsClient;
	private final UsersApiClient usersClient;

	public void reset(String token) {
		tenantsClient.findTenants(token).body()
				.forEach(tenant -> tenantsClient.deleteTenant(token, tenant.getTenantId()));
	}

	public TenantVO findTenant(String token, String tenantId) {
		return assertions.assert200(() -> tenantsClient.findTenant(token, tenantId));
	}

	public TenantVO createTenant(String token) {
		return createTenant(token, UUID.randomUUID().toString().substring(0, 10), UUID.randomUUID().toString(), true);
	}

	public TenantVO createTenant(String token, String tenantId, String name, boolean enabled) {
		var vo = new TenantCreateVO().setEnabled(enabled).setName(name);
		return assertions.assert201(() -> tenantsClient.createTenant(token, tenantId, vo));
	}

	public TenantVO updateTenant(String token, String tenantId, String name, boolean enabled) {
		var vo = new TenantUpdateVO().setEnabled(enabled).setName(name);
		return assertions.assert200(() -> tenantsClient.updateTenant(token, tenantId, vo));
	}

	public UserVO addUser(String token, String tenantId, String username, UserRoleVO role) {
		var vo = new UserCreateVO().setEmail(username + "@example.org").setRole(role);
		return assertions.assert201(() -> usersClient.createUser(token, tenantId, vo));
	}
}
