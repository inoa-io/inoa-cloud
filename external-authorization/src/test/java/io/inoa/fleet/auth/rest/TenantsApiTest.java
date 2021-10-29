package io.inoa.fleet.auth.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.inoa.fleet.auth.rest.management.TenantsApiTestClient;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.auth.AbstractTest;
import io.inoa.fleet.auth.rest.management.TenantVO;
import io.inoa.fleet.auth.rest.management.TenantsApiTestSpec;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TenantsApiTest extends AbstractTest implements TenantsApiTestSpec {

	@Inject
	TenantsApiTestClient client;

	@Test
	@Override
	public void createTenant201() throws Exception {

	}

	@Test
	@Override
	public void createTenant400() throws Exception {

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

		// execute

		var expected = List.of(tenant3.getName(), tenant2.getName(), tenant1.getName());
		var actual = assert200(() -> client.findTenants(auth()));
		assertEquals(expected, actual.stream().map(TenantVO::getName).collect(Collectors.toList()), "ordering");
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
