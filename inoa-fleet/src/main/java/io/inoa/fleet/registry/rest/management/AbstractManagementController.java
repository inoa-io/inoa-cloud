package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;

import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public abstract class AbstractManagementController {

	protected Tenant resolveAmbiguousTenant(List<Tenant> grantedTenants, Optional<String> tenantId) {
		if (grantedTenants.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Tenant not granted.");
		}
		if (tenantId.isPresent()) {
			var specificTenant = grantedTenants.stream().filter(tenant -> tenant.getTenantId().equals(tenantId.get()))
				.findFirst();
			if (specificTenant.isEmpty()) {
				throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Tenant not granted.");
			}
			return specificTenant.get();
		} else {
			if (grantedTenants.size() > 1) {
				throw new HttpStatusException(HttpStatus.BAD_REQUEST,
					"Ambiguous tenant. Please specify tenant as query parameter.");
			}
			return grantedTenants.iterator().next();
		}
	}
}
