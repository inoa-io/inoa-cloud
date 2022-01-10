package io.inoa.cnpm;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.cnpm.tenant.management.UserRoleVO;
import io.inoa.test.infrastructure.AbstractTest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;

/**
 * Tests for tenants without custom OIDC issuer.
 */
@DisplayName("tenant: with defaults")
public class TenantWithDefaultsTest extends AbstractTest {

	private static final String TENANT_1 = UUID.randomUUID().toString().substring(0, 10);
	private static final String TENANT_2 = UUID.randomUUID().toString().substring(0, 10);
	private static final String TENANT_3 = UUID.randomUUID().toString().substring(0, 10);

	@DisplayName("1. create tenants")
	@Test
	void tenants() {

		// create tenants

		var tokenAdmin = keycloak.realmInoa().token(INOA_UI, INOA_ADMIN);
		tenantManagment.createTenant(tokenAdmin, TENANT_1, TENANT_1, true);
		tenantManagment.createTenant(tokenAdmin, TENANT_2, TENANT_2, true);
		tenantManagment.createTenant(tokenAdmin, TENANT_3, TENANT_3, true);

		// admin can access all tenants

		assertEcho(TENANT_1, tokenAdmin, UserRoleVO.ADMIN);
		assertEcho(TENANT_2, tokenAdmin, UserRoleVO.ADMIN);
		assertEcho(TENANT_3, tokenAdmin, UserRoleVO.ADMIN);
	}

	@DisplayName("2. monitoring user")
	@Test
	void monitoringUser() {

		// assign monitoring user

		var tokenAdmin = keycloak.realmInoa().token(INOA_UI, INOA_ADMIN);
		tenantManagment.addUser(tokenAdmin, TENANT_1, INOA_USER1, UserRoleVO.VIEWER);
		tenantManagment.addUser(tokenAdmin, TENANT_2, INOA_USER1, UserRoleVO.VIEWER);

		// verify monitoring user

		var tokenUser = keycloak.realmInoa().token(INOA_UI, INOA_USER1);
		assertEcho(TENANT_1, tokenUser, UserRoleVO.VIEWER);
		assertEcho(TENANT_2, tokenUser, UserRoleVO.VIEWER);
		assertEchoFail(TENANT_3, tokenUser);
	}

	@DisplayName("3. user with multiple roles")
	@Test
	void multipleRoles() {

		// assign monitoring user

		var tokenAdmin = keycloak.realmInoa().token(INOA_UI, INOA_ADMIN);
		tenantManagment.addUser(tokenAdmin, TENANT_2, INOA_USER2, UserRoleVO.EDITOR);
		tenantManagment.addUser(tokenAdmin, TENANT_3, INOA_USER2, UserRoleVO.ADMIN);

		// verify monitoring user

		var tokenUser = keycloak.realmInoa().token(INOA_UI, INOA_USER2);
		assertEchoFail(TENANT_1, tokenUser);
		assertEcho(TENANT_2, tokenUser, UserRoleVO.EDITOR);
		assertEcho(TENANT_3, tokenUser, UserRoleVO.ADMIN);
	}

	private void assertEcho(String tenantId, String token, UserRoleVO role) {
		var response = echo.call(tenantId, token);
		assertEquals(HttpStatus.OK, response.getStatus(), "status");
		assertEquals(tenantId, response.body().getTenantId(), "tenant id header");
		var claims = auth.parse(response.body().getAuthorization());
		assertAll("exchanged token claims",
				() -> assertEquals("http://cnpm-auth-service:8080", claims.getIssuer(), "issuer"),
				() -> assertEquals(tenantId, claims.getClaim("tenantId"), "tenantId"),
				() -> assertNotNull(claims.getClaim("userId"), "userId"),
				() -> assertEquals(List.of(role.getValue()), claims.getClaim("roles"), "role"));
	}

	private void assertEchoFail(String tenantId, String token) {
		var exception = assertThrows(HttpClientResponseException.class, () -> echo.call(tenantId, token));
		assertEquals(HttpStatus.FORBIDDEN, exception.getResponse().getStatus(), "status");
	}
}
