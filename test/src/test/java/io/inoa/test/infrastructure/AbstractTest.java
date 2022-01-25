package io.inoa.test.infrastructure;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.test.client.AuthClient;
import io.inoa.test.client.EchoClient;
import io.inoa.test.client.GrafanaClient;
import io.inoa.test.client.InfluxDBClient;
import io.inoa.test.client.KeycloakClient;
import io.inoa.test.client.TenantManagment;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Base for all integration tests.
 */
@MicronautTest(startApplication = false)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractTest {

	public static final String EMAIL_SUFFIX = "@example.org";
	public static final String INOA_ADMIN = "admin";
	public static final String INOA_USER1 = "user1";
	public static final String INOA_USER2 = "user2";
	public static final String INOA_USER3 = "user3";
	public static final String INOA_UI = "inoa-ui";
	public static final String INOA_BACKEND = "inoa-backend";

	@Inject
	public TestAssertions assertions;
	@Inject
	public TenantManagment tenantManagment;
	@Inject
	public AuthClient auth;
	@Inject
	public InfluxDBClient influx;
	@Inject
	public GrafanaClient grafana;
	@Inject
	public EchoClient echo;
	@Inject
	public KeycloakClient keycloak;

	public void reset() {
		tenantManagment.reset(keycloak.realmInoa().token(INOA_UI, INOA_ADMIN));
		influx.reset();
		grafana.reset();
	}
}
