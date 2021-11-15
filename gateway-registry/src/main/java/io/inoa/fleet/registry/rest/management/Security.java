package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;

import io.inoa.fleet.registry.ApplicationProperties;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Offers jwt related security for management interface.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
class Security {

	private final ApplicationProperties properties;
	private final TenantRepository tenantRepository;
	private final SecurityService securityService;

	Tenant getTenant() {
		return tenantRepository
				.findByTenantIdAndDeletedIsNull(getTenantId())
				.orElseThrow(() -> new HttpStatusException(HttpStatus.UNAUTHORIZED, "Tenant from JWT not found."));
	}

	String getTenantId() {
		var claim = properties.getSecurity().getClaimTenant();
		var value = securityService.getAuthentication().map(a -> a.getAttributes().get(claim))
				.flatMap(obj -> obj instanceof List ? List.class.cast(obj).stream().findFirst() : Optional.of(obj))
				.filter(String.class::isInstance);
		if (value.isEmpty()) {
			log.warn("Got request without claim '{}' for tenant.", claim);
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unable to obtain tenant claim from JWT.");
		}
		return (String) value.get();
	}
}
