package io.inoa.fleet.auth.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.inoa.fleet.auth.AbstractTest;
import io.inoa.fleet.auth.domain.Tenant;
import io.inoa.fleet.auth.domain.TenantRepository;
import io.inoa.fleet.auth.domain.TenantUser;
import io.inoa.fleet.auth.domain.TenantUserRepository;
import io.inoa.fleet.auth.domain.User;
import io.inoa.fleet.auth.domain.UserRepository;
import io.inoa.fleet.auth.rest.management.TenantCreateVO;
import io.inoa.fleet.auth.rest.management.TenantVO;
import io.inoa.fleet.auth.rest.management.TenantsApiTestClient;
import io.inoa.fleet.auth.rest.management.TenantsApiTestSpec;
import jakarta.inject.Inject;

public class TenantsApiTest extends AbstractTest implements TenantsApiTestSpec {

	@Inject
	TenantsApiTestClient client;

	@Inject
	UserRepository userRepository;

	@Inject
	TenantRepository tenantRepository;

	@Inject
	TenantUserRepository tenantUserRepository;

	@Test
	@Override
	public void createTenant201() throws Exception {
		String tenantId = "testid-1234";
		TenantCreateVO tenantVO = new TenantCreateVO().setTenantId(tenantId).setName("test");
		assert201(() -> client.createTenant(auth(), tenantVO));
		List<User> result = new ArrayList<>();
		Iterable<User> all = userRepository.findAll();
		all.forEach(result::add);
		assertEquals(1, result.size());
		assertEquals("test@test.de", result.get(0).getEmail());
		Optional<Tenant> tenant = tenantRepository.findByTenantId(tenantId);
		assertEquals(true, tenant.isPresent());
		List<User> users = tenantUserRepository.findUserByTenant(tenant.get());
		assertEquals(1, users.size());
		assertEquals("test@test.de", users.get(0).getEmail());
	}

	@Test
	@Override
	public void createTenant400() throws Exception {
		// to short
		assert400(() -> client.createTenant(auth(), new TenantCreateVO().setTenantId("ttt").setName("test")));
		assert400(() -> client.createTenant(auth(), new TenantCreateVO().setTenantId("ttttttt").setName("tt")));

		// to long
		assert400(() -> client.createTenant(auth(),
				new TenantCreateVO().setTenantId("ttttttttttttttttttttttttttttttttttt").setName("test")));
		assert400(() -> client.createTenant(auth(), new TenantCreateVO().setTenantId("ttttttt").setName(
				"ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt")));

		assert400(() -> client.createTenant(auth(), new TenantCreateVO().setTenantId("Ttttttt").setName("tttt")));
	}

	@Test
	@Override
	public void createTenant401() throws Exception {

	}

	@Test
	@Override
	public void createTenant409() throws Exception {

	}

	@Test
	@Override
	public void deleteTenant204() throws Exception {

	}
	@Test
	@Override
	public void deleteTenant400() throws Exception {

	}

	@Test
	@Override
	public void deleteTenant401() throws Exception {

	}

	@Test
	@Override
	public void deleteTenant404() throws Exception {

	}

	@Test
	@Override
	public void findTenant200() throws Exception {
		var tenant1 = data.tenant("abc2");
		var tenant2 = data.tenant("abc1");
		var tenant3 = data.tenant("aaa");
		User user = new User().setUserId(UUID.randomUUID()).setEmail("test@test.de");
		user = userRepository.save(user);

		tenantUserRepository.save(new TenantUser().setUser(user).setTenant(tenant1));
		tenantUserRepository.save(new TenantUser().setUser(user).setTenant(tenant2));
		// execute

		var expected = List.of(tenant2.getName(), tenant1.getName());
		var actual = assert200(() -> client.findTenants(auth()));
		assertThat(expected).hasSameElementsAs(actual.stream().map(TenantVO::getName).collect(Collectors.toList()));
	}

	@Override
	public void findTenant401() throws Exception {

	}

	@Test
	@Override
	public void findTenant404() throws Exception {

	}

	@Test
	@Override
	public void findTenants200() throws Exception {

	}

	@Test
	@Override
	public void findTenants401() throws Exception {

	}

	@Test
	@Override
	public void updateTenant200() throws Exception {

	}

	@Test
	@Override
	public void updateTenant400() throws Exception {

	}

	@Test
	@Override
	public void updateTenant401() throws Exception {

	}

	@Test
	@Override
	public void updateTenant404() throws Exception {

	}

	@Test
	@Override
	public void updateTenant409() throws Exception {

	}
}
