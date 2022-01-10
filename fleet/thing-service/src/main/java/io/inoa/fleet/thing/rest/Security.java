package io.inoa.fleet.thing.rest;

import java.util.List;
import java.util.Optional;

import javax.inject.Singleton;

import io.inoa.fleet.thing.ApplicationProperties;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.utils.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class Security {

	private final ApplicationProperties properties;
	private final SecurityService securityService;

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
