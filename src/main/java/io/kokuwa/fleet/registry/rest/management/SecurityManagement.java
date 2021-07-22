package io.kokuwa.fleet.registry.rest.management;

import java.util.UUID;

import javax.inject.Singleton;

import io.kokuwa.fleet.registry.ApplicationProperties;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.domain.TenantRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.utils.SecurityService;
import lombok.RequiredArgsConstructor;

/**
 * Offers jwt related security for management interface.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
public class SecurityManagement {

	private final ApplicationProperties properties;
	private final TenantRepository tenantRepository;
	private final SecurityService securityService;

	public Tenant getTenant() {
		return tenantRepository
				.findByTenantId(getTenantId())
				.orElseThrow(() -> new HttpStatusException(HttpStatus.UNAUTHORIZED, "Tenant from JWT not found."));
	}

	private UUID getTenantId() {
		return securityService.getAuthentication()
				.map(auth -> auth.getAttributes().get(properties.getSecurity().getClaimTenant()))
				.map(tenantId -> UUID.fromString(tenantId.toString()))
				.orElseThrow(
						() -> new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unable to obtain tenant from JWT."));
	}
}
