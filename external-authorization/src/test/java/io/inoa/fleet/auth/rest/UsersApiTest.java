package io.inoa.fleet.auth.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.inoa.fleet.auth.AbstractTest;
import io.inoa.fleet.auth.domain.TenantRepository;
import io.inoa.fleet.auth.domain.TenantUser;
import io.inoa.fleet.auth.domain.TenantUserRepository;
import io.inoa.fleet.auth.domain.User;
import io.inoa.fleet.auth.domain.UserRepository;
import io.inoa.fleet.auth.rest.management.UsersApiTestClient;
import io.inoa.fleet.auth.rest.management.UsersApiTestSpec;
import jakarta.inject.Inject;

public class UsersApiTest extends AbstractTest implements UsersApiTestSpec {

	@Inject
	UsersApiTestClient client;

	@Inject
	UserRepository userRepository;

	@Inject
	TenantRepository tenantRepository;

	@Inject
	TenantUserRepository tenantUserRepository;

	@Test
	@Override
	public void createUser201() throws Exception {

	}

	@Test
	@Override
	public void createUser400() throws Exception {

	}

	@Test
	@Override
	public void createUser401() throws Exception {

	}

	@Test
	@Override
	public void createUser409() throws Exception {

	}

	@Override
	public void deleteUser204() throws Exception {

	}

	@Test
	@Override
	public void deleteUser401() throws Exception {

	}

	@Test
	@Override
	public void deleteUser404() throws Exception {

	}

	@Test
	@Override
	public void findUser200() throws Exception {

	}

	@Test
	public void findProjects200WithSortSingleProperty() {
		/*
		 * IntStream.range(0, 5).forEach(i ->
		 * data.project(data.projectVO().setName("project" + i))); var sort =
		 * List.of(ProjectPageEntryVO.JSON_PROPERTY_NAME + ",Asc"); var comparator =
		 * Comparator.comparing(ProjectPageEntryVO::getName); var page = assert200(() ->
		 * client.findProjects(auth(), null, 100, sort, null));
		 * assertSorted(page.getContent(), ProjectPageEntryVO::getName, comparator);
		 */
	}

	@Test
	@Override
	public void findUser401() throws Exception {

	}
	@Test
	@Override
	public void findUser404() throws Exception {

	}

	@Test
	@Override
	public void findUsers200() throws Exception {
		var tenant1 = data.tenant("abc2");
		var tenant2 = data.tenant("abc1");
		User user1 = new User().setUserId(UUID.randomUUID()).setEmail("test@test.de");
		User user2 = new User().setUserId(UUID.randomUUID()).setEmail("test1@test.de");
		user1 = userRepository.save(user1);
		user2 = userRepository.save(user2);

		tenantUserRepository.save(new TenantUser().setUser(user1).setTenant(tenant1));
		tenantUserRepository.save(new TenantUser().setUser(user2).setTenant(tenant2));
		// execute

		var expected = List.of(tenant2.getName(), tenant1.getName());
		var actual = assert200(() -> client.findUsers(auth(), tenant1.getTenantId(), null, null, null, null));
		assertEquals(1, actual.getTotalSize());
	}

	@Test
	public void findUsers200WithFilter() throws Exception {
		var tenant1 = data.tenant("abc2");
		var tenant2 = data.tenant("abc1");
		User user1 = new User().setUserId(UUID.randomUUID()).setEmail("test@test.de");
		User user2 = new User().setUserId(UUID.randomUUID()).setEmail("test1@test.de");
		User user3 = new User().setUserId(UUID.randomUUID()).setEmail("test2@test.de");
		user1 = userRepository.save(user1);
		user2 = userRepository.save(user2);
		user3 = userRepository.save(user3);

		tenantUserRepository.save(new TenantUser().setUser(user1).setTenant(tenant1));
		tenantUserRepository.save(new TenantUser().setUser(user2).setTenant(tenant1));
		tenantUserRepository.save(new TenantUser().setUser(user3).setTenant(tenant2));
		// execute

		var actual = assert200(() -> client.findUsers(auth(), tenant1.getTenantId(), null, null, null, "test1"));
		assertEquals(1, actual.getTotalSize());
	}

	@Test
	@Override
	public void findUsers401() throws Exception {

	}

	@Test
	@Override
	public void updateUser200() throws Exception {

	}

	@Test
	@Override
	public void updateUser400() throws Exception {

	}

	@Test
	@Override
	public void updateUser401() throws Exception {

	}

	@Test
	@Override
	public void updateUser404() throws Exception {

	}

	@Test
	@Override
	public void updateUser409() throws Exception {

	}
}
