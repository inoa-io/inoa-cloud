package io.inoa.fleet.registry.rest.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.inoa.fleet.ApplicationProperties;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.token.Claims;
import io.micronaut.security.utils.SecurityService;
import jakarta.annotation.PostConstruct;
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
public class Security {

	private final ApplicationProperties properties;
	private final TenantRepository tenantRepository;
	private final SecurityService securityService;

	@PostConstruct
	void log() {
		log.info("Configured whitelist audience: {}", properties.getSecurity().getTenantAudienceWhitelist());
	}

	List<Tenant> getGrantedTenants() {
		var grantedTenants = new ArrayList<Tenant>();
		var allAvailableTenants = tenantRepository.findByDeletedIsNullOrderByTenantId();
		for (String tenantId : getTenantIds()) {
			var tenant = allAvailableTenants.stream().filter(entity -> entity.getTenantId().equals(tenantId))
					.findFirst();
			if (tenant.isEmpty()) {
				throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Tenant " + tenantId + " from JWT not found.");
			}
			grantedTenants.add(tenant.get());
		}
		return grantedTenants;
	}

	List<String> getTenantIds() {

		// get attributes

		var authentication = securityService.getAuthentication();
		if (authentication.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.");
		}
		var attributes = authentication.get().getAttributes();

		// read tenant from http header if audience claim is in whitelist

		var audienceWhitelist = properties.getSecurity().getTenantAudienceWhitelist();
		var audience = get(attributes, Claims.AUDIENCE).filter(audienceWhitelist::contains).findFirst();
		if (audience.isPresent()) {
			var tenantId = ServerRequestContext.currentRequest().map(HttpRequest::getHeaders)
					.flatMap(headers -> headers.getFirst(properties.getSecurity().getTenantHeaderName()));
			log.trace("Audience {} found in JWT with tenantId {}.", audience.get(), tenantId.orElse(null));
			if (tenantId.isPresent()) {
				return Collections.singletonList(tenantId.get());
			}
		}

		// read tenant from jwt claim

		var claim = properties.getSecurity().getClaimTenants();
		var tenantGrants = get(attributes, claim).toList();
		if (tenantGrants.isEmpty()) {
			log.warn("Got request without claim '{}' for tenant.", claim);
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unable to obtain tenant claim from JWT.");
		}
		return tenantGrants;
	}

	@SuppressWarnings("unchecked")
	private Stream<String> get(Map<String, Object> attributes, String claim) {
		return Stream.ofNullable(attributes.get(claim))
				.flatMap(obj -> obj instanceof List ? List.class.cast(obj).stream() : Stream.of(obj))
				.map(Object::toString);
	}
}
