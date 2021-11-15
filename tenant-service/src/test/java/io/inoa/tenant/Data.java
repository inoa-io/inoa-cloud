package io.inoa.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import io.inoa.tenant.domain.Tenant;
import io.inoa.tenant.domain.TenantTestRepository;
import io.inoa.tenant.domain.TenantUser;
import io.inoa.tenant.domain.TenantUserRepository;
import io.inoa.tenant.domain.User;
import io.inoa.tenant.domain.UserTestRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class Data {

	private final TenantTestRepository tenantRepository;
	private final TenantUserRepository tenantUserRepository;
	private final UserTestRepository userRepository;

	void deleteAll() {
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

	public Tenant tenant(String tenantId, String name, boolean enabled, boolean deleted) {
		return tenantRepository.save(new Tenant()
				.setTenantId(tenantId)
				.setName(name)
				.setEnabled(enabled)
				.setDeleted(deleted ? Instant.now() : null));
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
		Stream.of(tenants).forEach(tenant -> tenantUserRepository.save(new TenantUser(tenant, user)));
		return user;
	}

	// check

	public void assertAssignment(String email, String... expectedTenantIds) {
		assertTrue(StreamSupport
				.stream(userRepository.findAll().spliterator(), false)
				.anyMatch(user -> user.getEmail().equals(email)), "user not found");
		assertEquals(Set.of(expectedTenantIds), tenantUserRepository
				.findTenantByUserEmail(email).stream().map(Tenant::getTenantId).collect(Collectors.toSet()),
				"assignments");
	}

	public void assertTenantSoftDelete(String tenantId) {
		assertNotNull(tenantRepository.findByTenantId(tenantId).map(Tenant::getDeleted).get(), "assignments");
	}
}
