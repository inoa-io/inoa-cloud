package io.inoa.shared;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import io.inoa.fleet.FleetProperties;
import io.inoa.fleet.registry.auth.GatewayAuthentication;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.measurement.MeasurementProperties;
import io.inoa.rest.UserVO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.Claims;
import io.micronaut.security.utils.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Offers jwt related security.
 *
 * @author Stephan Schnabel
 */
@Slf4j
@Singleton
@RequiredArgsConstructor
public class Security {

	private static final String CLAIM_GIVEN_NAME = "given_name";
	private static final String CLAIM_NAME_FAMILY_NAME = "family_name";
	private static final String CLAIM_NAME_EMAIL = "email";

	private final MeasurementProperties measurementProperties;
	private final SecurityService securityService;
	private final FleetProperties fleetProperties;
	private final TenantRepository tenantRepository;

	@PostConstruct
	void log() {
		log.info(
				"Configured whitelist audience: {}", fleetProperties.getSecurity().getTenantAudienceWhitelist());
	}

	public List<Tenant> getGrantedTenants() {
		var grantedTenants = new ArrayList<Tenant>();
		var allAvailableTenants = tenantRepository.findByDeletedIsNullOrderByTenantId();
		for (String tenantId : getTenantIds()) {
			var tenant = allAvailableTenants.stream()
					.filter(entity -> entity.getTenantId().equals(tenantId))
					.findFirst();
			if (tenant.isEmpty()) {
				throw new HttpStatusException(
						HttpStatus.UNAUTHORIZED, "Tenant " + tenantId + " from JWT not found.");
			}
			grantedTenants.add(tenant.get());
		}
		return grantedTenants;
	}

	public List<String> getTenantIds() {

		// get attributes

		var authentication = securityService.getAuthentication();
		if (authentication.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.");
		}
		var attributes = authentication.get().getAttributes();

		// read tenant from http header if audience claim is in whitelist

		var audienceWhitelist = fleetProperties.getSecurity().getTenantAudienceWhitelist();
		var audience = get(attributes, Claims.AUDIENCE).filter(audienceWhitelist::contains).findFirst();
		if (audience.isPresent()) {
			var tenantId = ServerRequestContext.currentRequest()
					.map(HttpRequest::getHeaders)
					.flatMap(headers -> headers.getFirst(fleetProperties.getSecurity().getTenantHeaderName()));
			log.trace(
					"Audience {} found in JWT with tenantId {}.", audience.get(), tenantId.orElse(null));
			if (tenantId.isPresent()) {
				return Collections.singletonList(tenantId.get());
			}
		}

		// read tenant from jwt claim

		var claim = fleetProperties.getSecurity().getClaimTenants();
		var tenantGrants = get(attributes, claim).toList();
		if (tenantGrants.isEmpty()) {
			log.warn("Got request without claim '{}' for tenant.", claim);
			throw new HttpStatusException(
					HttpStatus.UNAUTHORIZED, "Unable to obtain tenant claim from JWT.");
		}
		return tenantGrants;
	}

	/**
	 * Returns the currently authenticated user as {@link UserVO}.
	 *
	 * @return the currently authenticated {@link UserVO}
	 */
	public UserVO getUser() {
		var claims = getAuthentication().getAttributes();
		var firstname = getClaim(claims, String.class, CLAIM_GIVEN_NAME);
		var lastname = getClaim(claims, String.class, CLAIM_NAME_FAMILY_NAME);
		var email = getClaim(claims, String.class, CLAIM_NAME_EMAIL);
		var sessionExpires = getClaim(claims, Instant.class, Claims.EXPIRATION_TIME);

		// In case of a landlord, set the first associated landlord of the user as start point for the
		// UI
		return new UserVO()
				.firstname(firstname)
				.lastname(lastname)
				.email(email)
				.sessionExpires(sessionExpires);
	}

	private <T> T getClaim(Map<String, Object> claims, Class<T> type, String name) {
		var claim = claims.get(name);
		if (claim == null) {
			log.error("Got token without claim {}", name);
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, null);
		}
		if (type.isInstance(claim)) {
			return type.cast(claim);
		}
		if (type.equals(Instant.class) && claim instanceof Date value) {
			return type.cast(value.toInstant());
		}
		log.error("Got token has unsupported type for claim {}: {}", name, claim.getClass().getName());
		throw new HttpStatusException(HttpStatus.UNAUTHORIZED, null);
	}

	public String getTenantId() {
		return getTenantIds().iterator().next();
	}

	private Stream<String> get(Map<String, Object> attributes, String claim) {
		return Stream.ofNullable(attributes.get(claim))
				.flatMap(obj -> obj instanceof List<?> list ? list.stream() : Stream.of(obj))
				.map(Object::toString);
	}

	private Authentication getAuthentication() {
		return securityService
				.getAuthentication()
				.orElseThrow(
						() -> {
							log.error("Got request in services without authentication");
							return new HttpStatusException(HttpStatus.UNAUTHORIZED, null);
						});
	}

	public Gateway getGateway() {
		return ServerRequestContext.currentRequest()
				.flatMap(request -> request.getUserPrincipal(GatewayAuthentication.class))
				.map(GatewayAuthentication::getGateway)
				.orElseThrow();
	}
}
