package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Singleton;

import io.kokuwa.fleet.registry.ApplicationProperties;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.domain.TenantRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.utils.SecurityService;
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
				.findByTenantId(getTenantId())
				.orElseThrow(() -> new HttpStatusException(HttpStatus.UNAUTHORIZED, "Tenant from JWT not found."));
	}

	UUID getTenantId() {

		var claim = properties.getSecurity().getClaimTenant();
		Optional<String> value = securityService.getAuthentication().map(a -> a.getAttributes().get(claim))
				.flatMap(obj -> obj instanceof List ? List.class.cast(obj).stream().findFirst() : Optional.of(obj))
				.filter(String.class::isInstance).map(String.class::cast);
		if (value.isEmpty()) {
			log.warn("Got request without claim '{}' for tenant.", claim);
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unable to obtain tenant claim from JWT.");
		}

		try {
			return UUID.fromString(value.get());
		} catch (IllegalArgumentException e) {
			return value.flatMap(tenantRepository::findTenantIdByName).orElseThrow(() -> {
				log.warn("Got request with invalid value '{}' for tenant claim '{}'.", value.get(), claim);
				throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unable to obtain tenant claim from JWT.");
			});
		}
	}
}
