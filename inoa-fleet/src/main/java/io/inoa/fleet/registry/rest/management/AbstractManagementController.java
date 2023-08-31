package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;

import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

/**
 * Base class for management API controllers.
 *
 * @author Fabian Schlegel
 */
public abstract class AbstractManagementController {

	/**
	 * If an issuer uses the API with multiple tenants granted, a specific tenant
	 * the action should apply to, has to be given. Otherwise, a 401 is thrown. If
	 * the issuer has only one tenant grant, no specific tenant has to be given.
	 *
	 * @param grantedTenants tenant grants of the issuer
	 * @param tenantId       an optional, specific tenant
	 * @return the tenant to which further actions should apply to
	 */
	protected Tenant resolveAmbiguousTenant(List<Tenant> grantedTenants, Optional<String> tenantId) {
		if (grantedTenants.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "No tenants granted.");
		}
		if (tenantId.isPresent()) {
			var specificTenant = grantedTenants.stream().filter(tenant -> tenant.getTenantId().equals(tenantId.get()))
					.findFirst();
			if (specificTenant.isEmpty()) {
				throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Tenant " + tenantId.get() + " not granted.");
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
