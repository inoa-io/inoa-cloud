package io.inoa.measurement;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.measurement.provisioner.grafana.UserRole;
import io.inoa.test.infrastructure.AbstractTest;

/**
 * Tests for tenants without custom OIDC issuer.
 */
@DisplayName("measurement: influx/grafana provisioning")
public class TenantProvisioningTest extends AbstractTest {

	private static final String TENANT_NAME = UUID.randomUUID().toString();
	private static final String TENANT_ID = TENANT_NAME.substring(0, 10);

	@DisplayName("1. create tenant with implicit admin")
	@Test
	void create() {

		// create

		var tokenAdmin = keycloak.realmInoa().token(INOA_UI, INOA_ADMIN);
		tenantManagment.createTenant(tokenAdmin, TENANT_ID, TENANT_NAME, true);

		// check

		influx.getOrganization(TENANT_ID);
		grafana.assertUserRole(TENANT_ID, INOA_ADMIN + EMAIL_SUFFIX, UserRole.Admin);
		grafana.assertDatasourceWorking(TENANT_ID, "influxdb");
	}
}
