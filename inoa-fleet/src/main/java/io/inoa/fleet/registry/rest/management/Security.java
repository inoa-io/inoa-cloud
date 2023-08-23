package io.inoa.fleet.registry.rest.management;

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
import io.micronaut.security.token.jwt.generator.claims.JwtClaims;
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

	Tenant getTenant() {
		return tenantRepository
				.findByTenantIdAndDeletedIsNull(getTenantId())
				.orElseThrow(() -> new HttpStatusException(HttpStatus.UNAUTHORIZED, "Tenant from JWT not found."));
	}

	String getTenantId() {

		// get attributes

		var authentication = securityService.getAuthentication();
		if (authentication.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.");
		}
		var attributes = authentication.get().getAttributes();

		// read tenant from http header if audience claim is in whitelist

		var audienceWhitelist = properties.getSecurity().getTenantAudienceWhitelist();
		var audience = get(attributes, JwtClaims.AUDIENCE).filter(a -> audienceWhitelist.contains(a)).findFirst();
		if (audience.isPresent()) {
			var tenantId = ServerRequestContext.currentRequest()
					.map(HttpRequest::getHeaders)
					.flatMap(headers -> headers.getFirst(properties.getSecurity().getTenantHeaderName()));
			log.trace("Audience {} found in JWT with tenantId {}.", audience.get(), tenantId.orElse(null));
			if (tenantId.isPresent()) {
				return tenantId.get();
			}
		}

		// read tenant from jwt claim

		var claim = properties.getSecurity().getClaimTenants();
		var tenantId = get(attributes, claim).findFirst();
		if (tenantId.isEmpty()) {
			log.warn("Got request without claim '{}' for tenant.", claim);
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Unable to obtain tenant claim from JWT.");
		}
		return tenantId.get();
	}

	private Stream<String> get(Map<String, Object> attributes, String claim) {
		return Stream
				.ofNullable(attributes.get(claim))
				.flatMap(obj -> obj instanceof List ? List.class.cast(obj).stream() : Stream.of(obj))
				.map(Object::toString);
	}
}
