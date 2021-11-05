package io.inoa.fleet.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.auth.AbstractTest;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TenantUserRepositoryTest extends AbstractTest {
	@Inject
	UserRepository userRepository;

	@Inject
	TenantRepository tenantRepository;

	@Inject
	TenantUserRepositoryImpl tenantUserRepository;

	@Test
	public void testSimpleUserSearch() {
		var tenant1 = data.tenant("one");
		data.tenant("two");
		var user1 = data.user("test1@test.de");
		var user2 = data.user("test2@test.de");
		data.user("test3@test.de");
		tenantUserRepository.save(new TenantUser().setTenant(tenant1).setUser(user1));
		tenantUserRepository.save(new TenantUser().setTenant(tenant1).setUser(user2));
		assertEquals(2, tenantUserRepository.findUserByTenant(tenant1).size());
	}

	@Test
	public void testPageableUserSearch() {
		var tenant1 = data.tenant("one");
		var tenant2 = data.tenant("two");
		var user1 = data.user("test1@test.de");
		var user2 = data.user("test2@test.de");
		var user3 = data.user("test3@test.de");
		tenantUserRepository.save(new TenantUser().setTenant(tenant1).setUser(user1));
		tenantUserRepository.save(new TenantUser().setTenant(tenant1).setUser(user2));
		tenantUserRepository.save(new TenantUser().setTenant(tenant2).setUser(user3));
		Page<User> users = tenantUserRepository.findUserByTenant(tenant1, Pageable.from(0, 1));
		assertEquals(1, users.getContent().size());

		users = tenantUserRepository.findUserByTenant(tenant1, Pageable.from(0));
		assertEquals(2, users.getContent().size());
		assertTrue(users.getContent().stream().anyMatch(u -> u.getEmail().equals("test1@test.de")));
		assertTrue(users.getContent().stream().anyMatch(u -> u.getEmail().equals("test2@test.de")));
		assertTrue(users.getContent().stream().noneMatch(u -> u.getEmail().equals("test3@test.de")));

		users = tenantUserRepository.findUserByTenant(tenant2, Pageable.from(0));
		assertEquals(1, users.getContent().size());
		assertTrue(users.getContent().stream().noneMatch(u -> u.getEmail().equals("test1@test.de")));
		assertTrue(users.getContent().stream().noneMatch(u -> u.getEmail().equals("test2@test.de")));
		assertTrue(users.getContent().stream().anyMatch(u -> u.getEmail().equals("test3@test.de")));

		var user4 = data.user("test4@test.de");

		tenantUserRepository.save(new TenantUser().setTenant(tenant1).setUser(user4));
		tenantUserRepository.save(new TenantUser().setTenant(tenant2).setUser(user4));

		users = tenantUserRepository.findUserByTenant(tenant1, Pageable.from(0));
		assertEquals(3, users.getContent().size());
		users = tenantUserRepository.findUserByTenant(tenant2, Pageable.from(0));
		assertEquals(2, users.getContent().size());
	}

	@Test
	public void testPageableUserSearchWithFilter() {
		var tenant1 = data.tenant("one");
		var tenant2 = data.tenant("two");
		var user1 = data.user("test1@test.de");
		var user2 = data.user("test2@test.de");
		var user3 = data.user("test3@test.de");
		tenantUserRepository.save(new TenantUser().setTenant(tenant1).setUser(user1));
		tenantUserRepository.save(new TenantUser().setTenant(tenant1).setUser(user2));
		tenantUserRepository.save(new TenantUser().setTenant(tenant2).setUser(user3));
		Page<User> users = tenantUserRepository.findUserForTenant(tenant1, Optional.of("test2"), Pageable.from(0));
		assertEquals(1, users.getContent().size());

		users = tenantUserRepository.findUserForTenant(tenant1, Optional.empty(), Pageable.from(0));
		assertEquals(2, users.getContent().size());
	}
}
