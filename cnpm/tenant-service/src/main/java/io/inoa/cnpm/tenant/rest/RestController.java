package io.inoa.cnpm.tenant.rest;

import java.util.List;

import io.inoa.cnpm.tenant.ApplicationProperties;
import io.inoa.cnpm.tenant.PageableProvider;
import io.inoa.cnpm.tenant.domain.Assignment;
import io.inoa.cnpm.tenant.domain.AssignmentRepository;
import io.inoa.cnpm.tenant.domain.IssuerRepository;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.domain.TenantRepository;
import io.inoa.cnpm.tenant.domain.UserRepository;
import io.inoa.cnpm.tenant.messaging.MessagingClient;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Base for controller.
 */
@Slf4j
public abstract class RestController {

	@Inject
	ApplicationProperties properties;
	@Inject
	PageableProvider pageableProvider;
	@Inject
	MessagingClient messaging;
	@Inject
	SecurityService security;
	@Inject
	RestMapper mapper;

	@Inject
	TenantRepository tenantRepository;
	@Inject
	IssuerRepository issuerRepository;
	@Inject
	AssignmentRepository assignmentRepository;
	@Inject
	UserRepository userRepository;

	protected boolean isService() {
		return security.getAuthentication()
				.map(Authentication::getAttributes)
				.filter(attributes -> !attributes.containsKey("email"))
				.map(attributes -> attributes.get("aud"))
				.filter(aud -> aud instanceof List
						? ((List<?>) aud).contains(properties.getServiceAudience())
						: properties.getServiceAudience().equals(aud))
				.isPresent();
	}

	protected String getUserEmail() {
		var email = security.getAuthentication().map(auth -> auth.getAttributes().get("email")).map(Object::toString);
		if (email.isEmpty()) {
			log.warn("Got authenticated jwt without email claim.");
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authorized. No email claim.");
		}
		return email.get();
	}

	protected Assignment getAssignment(String tenantId) {
		return assignmentRepository
				.findByTenantTenantIdAndUserEmail(tenantId, getUserEmail())
				.orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found."));
	}

	protected Tenant getTenant(String tenantId) {
		return getAssignment(tenantId).getTenant();
	}

	protected Tenant getTenantAsService(String tenantId) {
		return tenantRepository
				.findByTenantId(tenantId)
				.orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "Tenant not found."));
	}

	protected Tenant getTenantAsAdmin(String tenantId) {
		var assignment = getAssignment(tenantId);
		if (assignment.getRole() != UserRoleVO.ADMIN) {
			throw new HttpStatusException(HttpStatus.FORBIDDEN, "Not in role 'admin'.");
		}
		return assignment.getTenant();
	}
}
