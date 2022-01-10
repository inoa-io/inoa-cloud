package io.inoa.cnpm.tenant.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import io.inoa.cnpm.tenant.ApplicationProperties;
import io.inoa.cnpm.tenant.rest.UserRoleVO;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * Helper for managing data in tests.
 */
@Singleton
@RequiredArgsConstructor
public class Data {

	private final ApplicationProperties properties;
	private final TenantTestRepository tenantRepository;
	private final UserTestRepository userRepository;
	private final IssuerRepository issuerRepository;
	private final AssignmentRepository assignmentRepository;

	public void deleteAll() {
		tenantRepository.deleteAll();
		userRepository.deleteAll();
	}

	// create data

	public String tenantId() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public String tenantName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Tenant tenant() {
		return tenant(tenantId(), tenantName(), true, false);
	}

	public Tenant tenantDeleted() {
		return tenant(tenantId(), tenantName(), true, true);
	}

	public Tenant tenantDisabled() {
		return tenant(tenantId(), tenantName(), false, false);
	}

	public Tenant tenant(String tenantId, String name, boolean enabled, boolean deleted) {
		var tenant = tenantRepository.save(new Tenant()
				.setTenantId(tenantId)
				.setName(name)
				.setEnabled(enabled)
				.setDeleted(deleted ? Instant.now() : null)
				.setIssuers(new ArrayList<>()));
		issuer(tenant,
				properties.getDefaultIssuer().getName(),
				properties.getDefaultIssuer().getUrl(),
				properties.getDefaultIssuer().getCacheDuration(),
				properties.getDefaultIssuer().getServices());
		return tenant;
	}

	public String issuerName() {
		return UUID.randomUUID().toString().substring(0, 10);
	}

	public Issuer issuer(Tenant tenant, String name, URL url, Duration cacheDuration, Set<String> services) {
		var issuer = issuerRepository.save(new Issuer()
				.setTenant(tenant)
				.setName(name)
				.setUrl(url)
				.setCacheDuration(cacheDuration)
				.setServices(services));
		issuer.getServiceObjects().forEach(o -> issuerRepository.addService(o.getIssuer().getId(), o.getName()));
		tenant.getIssuers().add(issuer);
		return issuer;
	}

	public String userEmail() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public UUID userId() {
		return UUID.randomUUID();
	}

	public User user(Tenant... tenants) {
		return user(userEmail(), tenants);
	}

	public User user(String email, Tenant... tenants) {
		var user = userRepository.save(new User().setUserId(UUID.randomUUID()).setEmail(email));
		Stream.of(tenants).forEach(tenant -> assignment(tenant, user, UserRoleVO.ADMIN));
		return user;
	}

	public Assignment assignment(Tenant tenant, User user, UserRoleVO role) {
		return assignmentRepository.save(new Assignment().setTenant(tenant).setUser(user).setRole(role));
	}

	// check

	public Optional<Tenant> findTenant(String tenantId) {
		return tenantRepository.findByTenantId(tenantId);
	}

	public Optional<User> findUser(String email) {
		return userRepository.findByEmail(email);
	}

	public Assignment assertAssignment(Tenant tenant, User user, UserRoleVO role) {
		var assignment = assignmentRepository
				.findByTenantTenantIdAndUserEmail(tenant.getTenantId(), user.getEmail())
				.orElse(null);
		assertNotNull(assignment, "assignment");
		assertEquals(role, assignment.getRole(), "role");
		return assignment;
	}

	public void assertNoAssignment(Tenant tenant, User user) {
		assertNull(assignmentRepository.findByTenantTenantIdAndUserEmail(tenant.getTenantId(), user.getEmail())
				.orElse(null), "assignment");
	}

	public void assertTenantSoftDelete(String tenantId) {
		assertNotNull(tenantRepository.findByTenantId(tenantId).map(Tenant::getDeleted).get(), "assignments");
	}
}
