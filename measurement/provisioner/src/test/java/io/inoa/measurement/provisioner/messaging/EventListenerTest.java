package io.inoa.measurement.provisioner.messaging;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.inoa.cnpm.tenant.messaging.CloudEventSubjectVO;
import io.inoa.cnpm.tenant.messaging.CloudEventTypeVO;
import io.inoa.cnpm.tenant.messaging.TenantVO;
import io.inoa.cnpm.tenant.messaging.UserVO;
import io.inoa.measurement.provisioner.AbstractTest;
import io.inoa.measurement.provisioner.grafana.UserRole;
import io.micronaut.http.MediaType;
import jakarta.inject.Inject;

/**
 * Test for {@link EventListener}.
 */
public class EventListenerTest extends AbstractTest {

	@Inject
	EventListener listener;
	@Inject
	ObjectMapper mapper;

	@DisplayName("ignore: unsupported type")
	@Test
	public void ignoreUnsupportedType() {
		var tenantId = "ignoreUnsupportedType";
		send("nope", CloudEventSubjectVO.CREATE_VALUE, tenantId, Map.of());
	}

	@DisplayName("ignore: unsupported subject")
	@Test
	public void ignoreUnsupportedSubject() {
		var tenantId = "ignoreUnsupportedSubject";
		send(CloudEventTypeVO.TENANT_VALUE, "nope", tenantId, Map.of());
	}

	@DisplayName("ignore: unparseable payload")
	@Test
	public void ignoreUnparseablePayload() {
		var tenantId = "ignoreUnparseablePayload";
		send(CloudEventTypeVO.TENANT_VALUE, CloudEventSubjectVO.CREATE_VALUE, tenantId, "nope");
		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.CREATE_VALUE, tenantId, "nope");
	}

	@DisplayName("service: tenant")
	@Test
	public void serviceTenant() {

		var type = CloudEventTypeVO.TENANT_VALUE;
		var tenantId = "tenant";
		var tenant = new TenantVO().setTenantId(tenantId).setEnabled(true).setName("name");

		// create tenant

		send(type, CloudEventSubjectVO.CREATE_VALUE, tenantId, tenant);
		assertTrue(getInfluxOrganization(tenantId).isPresent());
		assertTrue(getGrafanaOrganization(tenantId).isPresent());

		// update tenant without change

		send(type, CloudEventSubjectVO.UPDATE_VALUE, tenantId, tenant);
		assertTrue(getInfluxOrganization(tenantId).isPresent());
		assertTrue(getGrafanaOrganization(tenantId).isPresent());

		// delete tenant

		send(type, CloudEventSubjectVO.DELETE_VALUE, tenantId, tenant);
		assertFalse(getInfluxOrganization(tenantId).isPresent());
		assertFalse(getGrafanaOrganization(tenantId).isPresent());

		// delete tenant again

		send(type, CloudEventSubjectVO.DELETE_VALUE, tenantId, tenant);
		assertFalse(getInfluxOrganization(tenantId).isPresent());
		assertFalse(getGrafanaOrganization(tenantId).isPresent());
	}

	@DisplayName("service: user")
	@Test
	public void serviceUser() {

		var tenant1 = "user_1";
		var tenant2 = "user_2";
		var tenant = new TenantVO().setEnabled(true).setName("name");
		send(CloudEventTypeVO.TENANT_VALUE, CloudEventSubjectVO.CREATE_VALUE, tenant1, tenant.setTenantId(tenant1));
		send(CloudEventTypeVO.TENANT_VALUE, CloudEventSubjectVO.CREATE_VALUE, tenant2, tenant.setTenantId(tenant2));

		var userEmail = UUID.randomUUID() + "@example.org";
		var userId = UUID.randomUUID();
		var user = new UserVO().setRole("Admin").setEmail(userEmail).setUserId(userId);

		// create user with unknown role

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.CREATE_VALUE, tenant1, user.setRole("nope"));
		assertGrafanaRole(tenant1, userEmail, null);
		assertGrafanaRole(tenant2, userEmail, null);

		// add to tenants

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.CREATE_VALUE, tenant1, user.setRole("admin"));
		assertGrafanaRole(tenant1, userEmail, UserRole.Admin);
		assertGrafanaRole(tenant2, userEmail, null);
		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.CREATE_VALUE, tenant2, user.setRole("viewer"));
		assertGrafanaRole(tenant1, userEmail, UserRole.Admin);
		assertGrafanaRole(tenant2, userEmail, UserRole.Viewer);

		// update role

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.UPDATE_VALUE, tenant2, user.setRole("editor"));
		assertGrafanaRole(tenant1, userEmail, UserRole.Admin);
		assertGrafanaRole(tenant2, userEmail, UserRole.Editor);

		// update with no change

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.UPDATE_VALUE, tenant2, user);
		assertGrafanaRole(tenant1, userEmail, UserRole.Admin);
		assertGrafanaRole(tenant2, userEmail, UserRole.Editor);

		// update with unknown role

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.UPDATE_VALUE, tenant1, user.setRole("nope"));
		assertGrafanaRole(tenant1, userEmail, null);
		assertGrafanaRole(tenant2, userEmail, UserRole.Editor);

		// remove from tenant

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.DELETE_VALUE, tenant2, user);
		assertGrafanaRole(tenant1, userEmail, null);
		assertGrafanaRole(tenant2, userEmail, null);

		// delete again

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.DELETE_VALUE, tenant2, user);
		assertGrafanaRole(tenant1, userEmail, null);
		assertGrafanaRole(tenant2, userEmail, null);

		// re-add user with unknown role

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.CREATE_VALUE, tenant1, user.setRole("nope"));
		assertGrafanaRole(tenant1, userEmail, null);
		assertGrafanaRole(tenant2, userEmail, null);

		// create/update/delete with unknown organization / user

		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.CREATE_VALUE, "nope", user);
		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.UPDATE_VALUE, "nope", user);
		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.DELETE_VALUE, "nope", user);
		send(CloudEventTypeVO.USER_VALUE, CloudEventSubjectVO.DELETE_VALUE, tenant1, user.setEmail("nope@example.org"));
	}

	private void send(String type, String subject, String tenantId, Object payload) {
		listener.handle(tenantId, CloudEventBuilder.v1()
				.withSource(URI.create("test"))
				.withId("test")
				.withSubject(subject)
				.withType(type)
				.withDataContentType(MediaType.APPLICATION_JSON)
				.withData(PojoCloudEventData.wrap(payload, mapper::writeValueAsBytes))
				.build());
	}
}
