package io.inoa.cnpm.tenant.management;

import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert204;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert401;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert404;
import static io.inoa.cnpm.tenant.HttpResponseAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.cnpm.tenant.AbstractTest;
import jakarta.inject.Inject;

/**
 * Test for {@link IssuersApi}.
 *
 * @author Stephan Schnabel
 */
public class IssuerApiTest extends AbstractTest implements IssuersApiTestSpec {

	@Inject
	IssuersApiTestClient client;

	@DisplayName("findIssuers(200): success")
	@Test
	@Override
	public void findIssuers200() {

		// create issuers

		var tenant = data.tenant();
		var issuerDefault = tenant.getIssuers().iterator().next();
		var issuer1 = data.issuer(tenant, "e1", security.getOpenIDBase("1"), Duration.ofDays(1));
		var issuer2 = data.issuer(tenant, "e2", security.getOpenIDBase("1"), Duration.ofMinutes(8));

		// execute

		var expected = List.of(issuer1.getName(), issuer2.getName(), issuerDefault.getName());
		var actual = assert200(() -> client.findIssuers(auth(tenant), tenant.getTenantId()));
		assertEquals(expected, actual.stream().map(IssuerVO::getName).collect(Collectors.toList()), "ordering");
	}

	@DisplayName("findIssuers(401): no token")
	@Test
	@Override
	public void findIssuers401() {
		assert401(() -> client.findIssuers(null, data.tenantId()));
	}

	@DisplayName("findIssuers(404): tenant not exists")
	@Test
	@Override
	public void findIssuers404() {
		assert404(() -> client.findIssuers(auth(), data.tenantId()));
	}

	@DisplayName("findIssuers(404): tenant not assigned")
	@Test
	public void findIssuers404TenantNotAssigned() {
		assert404(() -> client.findIssuers(auth(), data.tenant().getTenantId()));
	}

	@DisplayName("findIssuer(404): tenant deleted")
	@Test
	public void findIssuers404TenantDeleted() {
		var tenant = data.tenantDeleted();
		assert404(() -> client.findIssuers(auth(tenant), tenant.getTenantId()));
	}

	@DisplayName("findIssuer(200): success")
	@Test
	@Override
	public void findIssuer200() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		var actual = assert200(() -> client.findIssuer(auth(tenant), tenant.getTenantId(), issuer.getName()));
		assertEquals(issuer.getName(), actual.getName(), "name");
		assertEquals(issuer.getUrl(), actual.getUrl(), "url");
		assertEquals(issuer.getCacheDuration(), actual.getCacheDuration(), "cacheDuration");
		assertEquals(issuer.getCreated(), actual.getCreated(), "created");
		assertEquals(issuer.getUpdated(), actual.getUpdated(), "updated");
	}

	@DisplayName("findIssuer(401): no token")
	@Test
	@Override
	public void findIssuer401() {
		assert401(() -> client.findIssuer(null, data.tenantId(), data.issuerName()));
	}

	@DisplayName("findIssuer(404): issuer not exists")
	@Test
	@Override
	public void findIssuer404() {
		var tenant = data.tenant();
		assert404(() -> client.findIssuer(auth(tenant), tenant.getTenantId(), data.issuerName()));
	}

	@DisplayName("findIssuer(404): tenant not exists")
	@Test
	public void findIssuer404TenantNotExists() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		assert404(() -> client.findIssuer(auth(tenant), data.tenantId(), issuer.getName()));
	}

	@DisplayName("findIssuer(404): tenant deleted")
	@Test
	public void findIssuer404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var issuer = tenant.getIssuers().iterator().next();
		assert404(() -> client.findIssuer(auth(tenant), tenant.getTenantId(), issuer.getName()));
	}

	@DisplayName("findIssuer(404): user not assigned to tenant")
	@Test
	public void findIssuer404TenantNotAssigned() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		assert404(() -> client.findIssuer(auth(), tenant.getTenantId(), issuer.getName()));
	}

	@DisplayName("createIssuer(201): with mandatory properties")
	@Test
	@Override
	public void createIssuer201() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new IssuerCreateVO().setUrl(security.getOpenIDBase("1"));
		var name = data.issuerName();
		var created = assert201(() -> client.createIssuer(auth(user), tenant.getTenantId(), name, vo));
		assertEquals(name, created.getName(), "name");
		assertEquals(vo.getUrl(), created.getUrl(), "url");
		assertEquals(properties.getIssuerDefaults().getCacheDuration(), created.getCacheDuration(), "cacheDuration");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findIssuer(auth(user), tenant.getTenantId(), name)), "vo");
	}

	@DisplayName("createIssuer(201): with optional properties")
	@Test
	public void createIssuer201All() {
		var tenant = data.tenant();
		var user = data.user(tenant);
		var vo = new IssuerCreateVO().setUrl(security.getOpenIDBase("1")).setCacheDuration(Duration.ofHours(23));
		var name = data.issuerName();
		var created = assert201(() -> client.createIssuer(auth(user), tenant.getTenantId(), name, vo));
		assertEquals(name, created.getName(), "name");
		assertEquals(vo.getUrl(), created.getUrl(), "url");
		assertEquals(vo.getCacheDuration(), created.getCacheDuration(), "cacheDuration");
		assertNotNull(created.getCreated(), "created");
		assertNotNull(created.getUpdated(), "updated");
		assertEquals(created, assert200(() -> client.findIssuer(auth(user), tenant.getTenantId(), name)), "vo");
	}

	@DisplayName("createIssuer(400): is beanvalidation active")
	@Test
	@Override
	public void createIssuer400() {
		assert400(() -> client.createIssuer(auth(), data.tenantId(), data.issuerName(), new IssuerCreateVO()));
	}

	@DisplayName("createIssuer(401): no token")
	@Test
	@Override
	public void createIssuer401() {
		var vo = new IssuerCreateVO().setUrl(security.getOpenIDBase("1"));
		assert401(() -> client.createIssuer(null, data.tenantId(), data.issuerName(), vo));
	}

	@DisplayName("createIssuer(404): tenant not exists")
	@Override
	@Test
	public void createIssuer404() {
		var vo = new IssuerCreateVO().setUrl(security.getOpenIDBase("1"));
		assert404(() -> client.createIssuer(auth(), data.tenantId(), data.issuerName(), vo));
	}

	@DisplayName("createIssuer(404): user not assigned to tenant")
	@Test
	public void createIssuer404TenantNotAssigned() {
		var tenant = data.tenant();
		var vo = new IssuerCreateVO().setUrl(security.getOpenIDBase("1"));
		assert404(() -> client.createIssuer(auth(), tenant.getTenantId(), data.issuerName(), vo));
	}

	@DisplayName("createIssuer(404): tenant deleted")
	@Test
	public void createIssuer404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var vo = new IssuerCreateVO().setUrl(security.getOpenIDBase("1"));
		assert404(() -> client.createIssuer(auth(tenant), tenant.getTenantId(), data.issuerName(), vo));
	}

	@DisplayName("createIssuer(409): url already exists")
	@Test
	@Override
	public void createIssuer409() {
		var tenant = data.tenant();
		var vo = new IssuerCreateVO().setUrl(tenant.getIssuers().iterator().next().getUrl());
		assert409(() -> client.createIssuer(auth(tenant), tenant.getTenantId(), data.issuerName(), vo));
	}

	@DisplayName("createIssuer(409): name already exists")
	@Test
	public void createIssuer409Name() {
		var tenant = data.tenant();
		var name = tenant.getIssuers().iterator().next().getName();
		var vo = new IssuerCreateVO().setUrl(security.getOpenIDBase("1"));
		assert409(() -> client.createIssuer(auth(tenant), tenant.getTenantId(), name, vo));
	}

	@DisplayName("updateIssuer(200): update nothing")
	@Test
	@Override
	public void updateIssuer200() {
		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.user(tenant);
		var vo = new IssuerUpdateVO().setUrl(null).setCacheDuration(null);
		var updated = assert200(() -> client.updateIssuer(auth(user), tenantId, issuer.getName(), vo));
		assertEquals(issuer.getName(), updated.getName(), "name");
		assertEquals(issuer.getUrl(), updated.getUrl(), "url");
		assertEquals(issuer.getCacheDuration(), updated.getCacheDuration(), "cacheDuration");
		assertEquals(issuer.getCreated(), updated.getCreated(), "created");
		assertEquals(issuer.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findIssuer(auth(user), tenantId, issuer.getName())), "vo");
	}

	@DisplayName("updateIssuer(200): update url only")
	@Test
	public void updateIssuer200Url() {
		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.user(tenant);
		var vo = new IssuerUpdateVO().setUrl(security.getOpenIDBase("1")).setCacheDuration(null);
		var updated = assert200(() -> client.updateIssuer(auth(user), tenant.getTenantId(), issuer.getName(), vo));
		assertEquals(issuer.getName(), updated.getName(), "name");
		assertEquals(vo.getUrl(), updated.getUrl(), "url");
		assertEquals(issuer.getCacheDuration(), updated.getCacheDuration(), "cacheDuration");
		assertEquals(issuer.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(issuer.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findIssuer(auth(user), tenantId, issuer.getName())), "vo");
	}

	@DisplayName("updateIssuer(200): update cache duration only")
	@Test
	public void updateIssuer200CacheDuration() {
		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.user(tenant);
		var vo = new IssuerUpdateVO().setUrl(null).setCacheDuration(Duration.ofMinutes(123));
		var updated = assert200(() -> client.updateIssuer(auth(user), tenant.getTenantId(), issuer.getName(), vo));
		assertEquals(issuer.getName(), updated.getName(), "name");
		assertEquals(issuer.getUrl(), updated.getUrl(), "url");
		assertEquals(vo.getCacheDuration(), updated.getCacheDuration(), "cacheDuration");
		assertEquals(issuer.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findIssuer(auth(user), tenantId, issuer.getName())), "vo");
	}

	@DisplayName("updateIssuer(200): update unchanged")
	@Test
	public void updateIssuer200Unchanged() {
		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.user(tenant);
		var vo = new IssuerUpdateVO().setUrl(issuer.getUrl()).setCacheDuration(issuer.getCacheDuration());
		var updated = assert200(() -> client.updateIssuer(auth(user), tenantId, issuer.getName(), vo));
		assertEquals(issuer.getName(), updated.getName(), "name");
		assertEquals(issuer.getUrl(), updated.getUrl(), "url");
		assertEquals(issuer.getCacheDuration(), updated.getCacheDuration(), "cacheDuration");
		assertEquals(issuer.getCreated(), updated.getCreated(), "created");
		assertEquals(issuer.getUpdated(), updated.getUpdated(), "updated");
		assertEquals(updated, assert200(() -> client.findIssuer(auth(user), tenantId, issuer.getName())), "vo");
	}

	@DisplayName("updateIssuer(200): update all")
	@Test
	public void updateIssuer200All() {
		var tenant = data.tenant();
		var tenantId = tenant.getTenantId();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.user(tenant);
		var vo = new IssuerUpdateVO().setUrl(security.getOpenIDBase("1")).setCacheDuration(Duration.ofMinutes(123));
		var updated = assert200(() -> client.updateIssuer(auth(user), tenant.getTenantId(), issuer.getName(), vo));
		assertEquals(issuer.getName(), updated.getName(), "name");
		assertEquals(vo.getUrl(), updated.getUrl(), "url");
		assertEquals(vo.getCacheDuration(), updated.getCacheDuration(), "cacheDuration");
		assertEquals(issuer.getCreated(), updated.getCreated(), "created");
		assertTrue(updated.getUpdated().isAfter(tenant.getUpdated()), "updated");
		assertEquals(updated, assert200(() -> client.findIssuer(auth(user), tenantId, issuer.getName())), "vo");
	}

	@DisplayName("updateIssuer(400): is beanvalidation active")
	@Test
	@Override
	public void updateIssuer400() {
		var tenant = data.tenant();
		assert404(() -> client.updateIssuer(auth(tenant), tenant.getTenantId(), "12345678901", new IssuerUpdateVO()));
	}

	@DisplayName("updateIssuer(401): no token")
	@Test
	@Override
	public void updateIssuer401() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		var vo = new IssuerUpdateVO();
		assert401(() -> client.updateIssuer(null, tenant.getTenantId(), issuer.getName(), vo));
	}

	@DisplayName("updateIssuer(404): issuer not exists")
	@Override
	@Test
	public void updateIssuer404() {
		var tenant = data.tenant();
		var vo = new IssuerUpdateVO();
		assert404(() -> client.updateIssuer(auth(tenant), tenant.getTenantId(), data.issuerName(), vo));
	}

	@DisplayName("updateIssuer(404): tenant not exists")
	@Test
	public void updateIssuer404TenantNotExists() {
		var vo = new IssuerUpdateVO();
		assert404(() -> client.updateIssuer(auth(), data.tenantId(), data.issuerName(), vo));
	}

	@DisplayName("updateIssuer(404): user not assigned to tenant")
	@Test
	public void updateIssuer404TenantNotAssigned() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		var vo = new IssuerUpdateVO();
		assert404(() -> client.updateIssuer(auth(), tenant.getTenantId(), issuer.getName(), vo));
	}

	@DisplayName("updateIssuer(404): tenant deleted")
	@Test
	public void updateIssuer404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var issuer = tenant.getIssuers().iterator().next();
		var vo = new IssuerUpdateVO();
		assert404(() -> client.updateIssuer(auth(tenant), tenant.getTenantId(), issuer.getName(), vo));
	}

	@DisplayName("updateIssuer(409): url already exists")
	@Test
	@Override
	public void updateIssuer409() {
		var tenant = data.tenant();
		var issuerDefault = tenant.getIssuers().iterator().next();
		var issuerTest = data.issuer(tenant, data.issuerName(), security.getOpenIDBase("1"), Duration.ofDays(1));
		var vo = new IssuerUpdateVO().setUrl(issuerDefault.getUrl());
		assert409(() -> client.updateIssuer(auth(tenant), tenant.getTenantId(), issuerTest.getName(), vo));
	}

	@DisplayName("deleteIssuer(204): success")
	@Test
	@Override
	public void deleteIssuer204() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		var user = data.user(tenant);
		assert204(() -> client.deleteIssuer(auth(user), tenant.getTenantId(), issuer.getName()));
		assert404(() -> client.findIssuer(auth(user), tenant.getTenantId(), issuer.getName()));
	}

	@DisplayName("deleteIssuer(401): no token")
	@Test
	@Override
	public void deleteIssuer401() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		assert401(() -> client.deleteIssuer(null, tenant.getTenantId(), issuer.getName()));
	}

	@DisplayName("deleteIssuer(404): issuer not exists")
	@Test
	@Override
	public void deleteIssuer404() {
		var tenant = data.tenant();
		assert404(() -> client.deleteIssuer(auth(tenant), tenant.getTenantId(), data.issuerName()));
	}

	@DisplayName("deleteIssuer(404): tenant not exists")
	@Test
	public void deleteIssuer404TenantNotExists() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		assert404(() -> client.deleteIssuer(auth(tenant), data.tenantId(), issuer.getName()));
	}

	@DisplayName("deleteIssuer(404): user not assigned to tenant")
	@Test
	public void deleteIssuer404TenantNotAssigned() {
		var tenant = data.tenant();
		var issuer = tenant.getIssuers().iterator().next();
		assert404(() -> client.deleteIssuer(auth(), tenant.getTenantId(), issuer.getName()));
	}

	@DisplayName("deleteIssuer(404): tenant deleted")
	@Test
	public void deleteIssuer404TenantDeleted() {
		var tenant = data.tenantDeleted();
		var issuer = tenant.getIssuers().iterator().next();
		assert404(() -> client.deleteIssuer(auth(tenant), tenant.getTenantId(), issuer.getName()));
	}
}
