package io.inoa.cnpm.tenant.rest;

import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert204;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert401;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert403;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert404;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.cnpm.tenant.AbstractTest;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.messaging.CloudEventSubjectVO;
import jakarta.inject.Inject;

/**
 * Test for {@link TenantsApi}.
 */
@DisplayName("api: tenants")
public class TenantsApiTest extends AbstractTest implements TenantsApiTestSpec {

	@Inject
	TenantsApiTestClient client;

	@DisplayName("findTenants(200): tenants with order found")
	@Test
	@Override
	public void findTenants200() {

		// create tenants

		var tenant1 = data.tenant(data.tenantId(), "abc2", true, false);
		var tenant2 = data.tenant(data.tenantId(), "abc1", true, false);
		var tenant3 = data.tenant(data.tenantId(), "aaa3", true, false);
		var tenant4 = data.tenantDeleted();
		var user = data.user(tenant1, tenant2, tenant3, tenant4);
		data.tenant(data.tenantId(), "aaa5", true, false);

		// execute

		var expected = List.of(tenant3.getTenantId(), tenant2.getTenantId(), tenant1.getTenantId());
		var actual = assert200(() -> client.findTenants(auth(user)));
		assertEquals(expected, actual.stream().map(TenantPageEntryVO::getTenantId).collect(Collectors.toList()),
				"ordering");
	}

	@DisplayName("findTenants(200): no tenant found")
	@Test
	public void findTenants200Empty() {
		assertEquals(List.of(), assert200(() -> client.findTenants(auth())));
	}

	@DisplayName("findTenants(401): no token")
	@Test
	@Override
	public void findTenants401() {
		assert401(() -> client.findTenants(null));
	}

	@DisplayName("findTenant(200): as user")
	@Test
	@Override
	public void findTenant200() {
		findTenant200(tenant -> auth(tenant));
	}

	@DisplayName("findTenant(200): as service")
	@Test
	public void findTenant200Service() {
		findTenant200(tenant -> authService());
	}

	private void findTenant200(Function<Tenant, String> auth) {
		var expected = data.tenant();
		var actual = assert200(() -> client.findTenant(auth.apply(expected), expected.getTenantId()));
		assertEquals(expected.getTenantId(), actual.getTenantId(), "tenantId");
		assertEquals(expected.getName(), actual.getName(), "name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "updated");
		assertDefaultIssuer(actual);
	}

	@DisplayName("findTenant(401): no token")
	@Test
	@Override
	public void findTenant401() {
		assert401(() -> client.findTenant(null, data.tenantId()));
		assert401(() -> client.findTenant(auth((String) null), data.tenantId()));
	}

	@DisplayName("findTenant(404): not exists")
	@Test
	@Override
	public void findTenant404() {
		assert404(() -> client.findTenant(auth(), data.tenantId()));
	}

	@DisplayName("findTenant(404): not assigned")
	@Test
	public void findTenant404NotAssigned() {
		assert404(() -> client.findTenant(auth(), data.tenant().getTenantId()));
	}

	@DisplayName("findTenant(404): deleted")
	@Test
	public void findTenant404Deleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		assert404(() -> client.findTenant(auth(user), tenant.getTenantId()));
	}

	@DisplayName("createTenant(201): with mandatory properties")
	@Test
	@Override
	public void createTenant201() {
		var email = data.userEmail();
		var tenantId = data.tenantId();
		var vo = new TenantCreateVO().setName(data.tenantName());
		var created = assert201(() -> client.createTenant(auth(email), tenantId, vo));
		assertEquals(tenantId, created.getTenantId(), "tenantId");
		assertEquals(true, created.getEnabled(), "enabled");
		assertEquals(vo.getName(), created.getName(), "name");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertDefaultIssuer(created);
		assertEquals(created, assert200(() -> client.findTenant(auth(email), tenantId)), "vo");
		var tenant = data.findTenant(tenantId).get();
		var user = data.findUser(email).get();
		var assignmemt = data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		messaging.assertTenant(tenantId, CloudEventSubjectVO.CREATE);
		messaging.assertAssignment(assignmemt, CloudEventSubjectVO.CREATE);
	}

	@DisplayName("createTenant(201): with optional properties")
	@Test
	public void createTenant201All() {
		var email = data.userEmail();
		var tenantId = data.tenantId();
		var vo = new TenantCreateVO().setEnabled(false).setName(data.tenantName());
		var created = assert201(() -> client.createTenant(auth(email), tenantId, vo));
		assertEquals(tenantId, created.getTenantId(), "tenantId");
		assertEquals(vo.getName(), created.getName(), "name");
		assertEquals(vo.getEnabled(), created.getEnabled(), "enabled");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertDefaultIssuer(created);
		assertEquals(created, assert200(() -> client.findTenant(auth(email), tenantId)), "vo");
		var tenant = data.findTenant(tenantId).get();
		var user = data.findUser(email).get();
		var assignmemt = data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		messaging.assertTenant(tenantId, CloudEventSubjectVO.CREATE);
		messaging.assertAssignment(assignmemt, CloudEventSubjectVO.CREATE);
	}

	@DisplayName("createTenant(201): with existing user")
	@Test
	public void createTenant201ExistingUser() {
		var otherTenant = data.tenant();
		var user = data.user(otherTenant);
		var tenantId = data.tenantId();
		var vo = new TenantCreateVO().setName(data.tenantName());
		var created = assert201(() -> client.createTenant(auth(user), tenantId, vo));
		assertDefaultIssuer(created);
		assertEquals(created, assert200(() -> client.findTenant(auth(user), tenantId)), "vo");
		var assignmemt = data.assertAssignment(data.findTenant(tenantId).get(), user, UserRoleVO.ADMIN);
		data.assertAssignment(otherTenant, user, UserRoleVO.ADMIN);
		messaging.assertTenant(tenantId, CloudEventSubjectVO.CREATE);
		messaging.assertAssignment(assignmemt, CloudEventSubjectVO.CREATE);
	}

	@DisplayName("createTenant(400): is beanvalidation active")
	@Test
	@Override
	public void createTenant400() {
		assert400(() -> client.createTenant(auth(), data.tenantId(), new TenantCreateVO()));
	}

	@DisplayName("createTenant(401): no token")
	@Test
	@Override
	public void createTenant401() {
		assert401(() -> client.createTenant(null, data.tenantId(), new TenantCreateVO().setName(data.tenantName())));
	}

	@DisplayName("createTenant(401): without email claim")
	@Test
	public void createTenant401WithoutEmailClaim() {
		var tenantId = data.tenantId();
		assert401(() -> client.createTenant(auth((String) null), tenantId,
				new TenantCreateVO().setName(data.tenantName())));
		assertTrue(data.findTenant(tenantId).isEmpty(), "tenant created");
	}

	@DisplayName("createTenant(409): tenantId exists")
	@Test
	@Override
	public void createTenant409() {
		var tenantId = data.tenant().getTenantId();
		assert409(() -> client.createTenant(auth(), tenantId, new TenantCreateVO().setName(data.tenantName())));
	}

	@DisplayName("createTenant(409): tenantId exists for deleted tenant")
	@Test
	public void createTenant409DeletedTenant() {
		var tenantId = data.tenantDeleted().getTenantId();
		assert409(() -> client.createTenant(auth(), tenantId, new TenantCreateVO().setName(data.tenantName())));
	}

	@DisplayName("updateTenant(200): update nothing")
	@Test
	@Override
	public void updateTenant200() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(null).setEnabled(null);
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(tenant.getName(), updated.getName(), "name");
		assertEquals(tenant.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertEquals(tenant.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findTenant(auth(user), updated.getTenantId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
	}

	@DisplayName("updateTenant(200): update name only")
	@Test
	public void updateTenant200Name() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(data.tenantName()).setEnabled(null);
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(tenant.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findTenant(auth(user), updated.getTenantId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		messaging.assertTenant(tenant.getTenantId(), CloudEventSubjectVO.UPDATE);
	}

	@DisplayName("updateTenant(200): update enabled only")
	@Test
	public void updateTenant200Enabled() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(null).setEnabled(false);
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(tenant.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findTenant(auth(user), updated.getTenantId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		messaging.assertTenant(tenant.getTenantId(), CloudEventSubjectVO.UPDATE);
	}

	@DisplayName("updateTenant(200): update unchanged")
	@Test
	public void updateTenant200Unchanged() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(tenant.getName()).setEnabled(tenant.getEnabled());
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(tenant.getName(), updated.getName(), "name");
		assertEquals(tenant.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertEquals(tenant.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findTenant(auth(user), updated.getTenantId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
	}

	@DisplayName("updateTenant(200): update all")
	@Test
	public void updateTenant200All() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new TenantUpdateVO().setName(data.tenantName()).setEnabled(false);
		var updated = assert200(() -> client.updateTenant(auth(user), tenant.getTenantId(), vo));
		assertEquals(tenant.getTenantId(), updated.getTenantId(), "tenantId");
		assertEquals(vo.getName(), updated.getName(), "name");
		assertEquals(vo.getEnabled(), updated.getEnabled(), "enabled");
		assertEquals(tenant.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findTenant(auth(user), updated.getTenantId())), "vo");
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
		messaging.assertTenant(tenant.getTenantId(), CloudEventSubjectVO.UPDATE);
	}

	@DisplayName("updateTenant(400): is beanvalidation active")
	@Test
	@Override
	public void updateTenant400() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert400(() -> client.updateTenant(auth(user), tenant.getTenantId(), new TenantUpdateVO().setName("")));
	}

	@DisplayName("updateTenant(401): no token")
	@Test
	@Override
	public void updateTenant401() {
		var tenantId = data.tenant().getTenantId();
		assert401(() -> client.updateTenant(null, tenantId, new TenantUpdateVO().setName(data.tenantName())));
	}

	@DisplayName("updateTenant(403): forbidden")
	@Test
	@Override
	public void updateTenant403() {
		var tenant = data.tenant();
		var vo = new TenantUpdateVO().setName(data.tenantName());
		assert403(() -> client.updateTenant(auth(tenant, UserRoleVO.VIEWER), tenant.getTenantId(), vo));
		assert403(() -> client.updateTenant(auth(tenant, UserRoleVO.EDITOR), tenant.getTenantId(), vo));

	}

	@DisplayName("updateTenant(404): not found")
	@Test
	@Override
	public void updateTenant404() {
		assert404(() -> client.updateTenant(auth(), data.tenantId(), new TenantUpdateVO().setName(data.tenantName())));
	}

	@DisplayName("updateTenant(404): not assigned")
	@Test
	public void updateTenant404NotAssigned() {
		var tenantId = data.tenant().getTenantId();
		assert404(() -> client.updateTenant(auth(), tenantId, new TenantUpdateVO().setName(data.tenantName())));
	}

	@DisplayName("updateTenant(404): deleted")
	@Test
	public void updateTenant404Deleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		assert404(() -> client.updateTenant(auth(user), tenant.getTenantId(),
				new TenantUpdateVO().setName(data.tenantName())));
	}

	@DisplayName("deleteTenant(204): success")
	@Test
	@Override
	public void deleteTenant204() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var otherTenant = data.tenant();
		var otherUser = data.user(tenant, otherTenant);
		assert204(() -> client.deleteTenant(auth(user), tenant.getTenantId()));
		assert404(() -> client.findTenant(auth(user), tenant.getTenantId()));
		assert200(() -> client.findTenant(auth(otherUser), otherTenant.getTenantId()));
		data.assertTenantSoftDelete(tenant.getTenantId());
		messaging.assertTenant(tenant.getTenantId(), CloudEventSubjectVO.DELETE);
	}

	@DisplayName("deleteTenant(401): no token")
	@Test
	@Override
	public void deleteTenant401() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert401(() -> client.deleteTenant(null, tenant.getTenantId()));
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
	}

	@DisplayName("deleteTenant(403): forbidden")
	@Test
	@Override
	public void deleteTenant403() {
		var tenant = data.tenant();
		assert403(() -> client.deleteTenant(auth(tenant, UserRoleVO.VIEWER), tenant.getTenantId()));
		assert403(() -> client.deleteTenant(auth(tenant, UserRoleVO.EDITOR), tenant.getTenantId()));
	}

	@DisplayName("deleteTenant(404): not found")
	@Test
	@Override
	public void deleteTenant404() {
		assert404(() -> client.deleteTenant(auth(), data.tenantId()));
	}

	@DisplayName("deleteTenant(404): not assigned")
	@Test
	public void deleteTenant404NotAssigned() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		assert404(() -> client.deleteTenant(auth(), tenant.getTenantId()));
		data.assertAssignment(tenant, user, UserRoleVO.ADMIN);
	}

	@DisplayName("deleteTenant(404): deleted")
	@Test
	public void deleteTenant404Deleted() {
		var tenant = data.tenantDeleted();
		var user = data.user(tenant);
		assert404(() -> client.deleteTenant(auth(user), tenant.getTenantId()));
	}

	private void assertDefaultIssuer(TenantVO tenant) {
		assertEquals(1, tenant.getIssuers().size(), "issuers");
		var issuer = tenant.getIssuers().iterator().next();
		assertEquals(properties.getDefaultIssuer().getName(), issuer.getName(), "issuer.name");
		assertEquals(properties.getDefaultIssuer().getUrl(), issuer.getUrl(), "issuer.url");
		assertEquals(properties.getDefaultIssuer().getCacheDuration(), issuer.getCacheDuration(), "issuer.cache");
		assertEquals(properties.getDefaultIssuer().getServices(), issuer.getServices(), "issuer.services");
		assertNotNull(issuer.getCreated(), "issuer.created");
		assertNotNull(issuer.getUpdated(), "issuer.updated");
	}
}
