package io.inoa.measurement.things.rest;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

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
 * Offers jwt related security for management interface.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class Security {
	private static final String CLAIM_GIVEN_NAME = "given_name";
	private static final String CLAIM_NAME_FAMILY_NAME = "family_name";
	private static final String CLAIM_NAME_EMAIL = "email";

	private final MeasurementProperties properties;
	private final SecurityService securityService;

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

	@PostConstruct
	void log() {
		log.info(
				"Configured whitelist audience: {}", properties.getSecurity().getTenantAudienceWhitelist());
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

	String getTenantId() {

		// get attributes

		var authentication = securityService.getAuthentication();
		if (authentication.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated.");
		}
		var attributes = authentication.get().getAttributes();

		// read tenant from http header if audience claim is in whitelist

		var audienceWhitelist = properties.getSecurity().getTenantAudienceWhitelist();
		var audience = get(attributes, Claims.AUDIENCE).filter(a -> audienceWhitelist.contains(a)).findFirst();
		if (audience.isPresent()) {
			var tenantId = ServerRequestContext.currentRequest()
					.map(HttpRequest::getHeaders)
					.flatMap(headers -> headers.getFirst(properties.getSecurity().getTenantHeaderName()));
			log.trace(
					"Audience {} found in JWT with tenantId {}.", audience.get(), tenantId.orElse(null));
			if (tenantId.isPresent()) {
				return tenantId.get();
			}
		}

		// read tenant from jwt claim

		var claim = properties.getSecurity().getClaimTenants();
		var tenantId = get(attributes, claim).findFirst();
		if (tenantId.isEmpty()) {
			log.warn("Got request without claim '{}' for tenant.", claim);
			throw new HttpStatusException(
					HttpStatus.UNAUTHORIZED, "Unable to obtain tenant claim from JWT.");
		}
		return tenantId.get();
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
}
