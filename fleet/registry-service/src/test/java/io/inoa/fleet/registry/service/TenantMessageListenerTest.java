package io.inoa.fleet.registry.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.inoa.cnpm.tenant.messaging.CloudEventSubjectVO;
import io.inoa.cnpm.tenant.messaging.CloudEventTypeVO;
import io.inoa.cnpm.tenant.messaging.TenantVO;
import io.inoa.fleet.registry.AbstractTest;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.rest.management.ConfigurationTypeVO;
import jakarta.inject.Inject;

@DisplayName("messaging: tenant listener")
public class TenantMessageListenerTest extends AbstractTest {

	@Inject
	ObjectMapper cloudEventObjectMapper;
	@Inject
	TenantMessageListener listener;

	@DisplayName("lifecycle")
	@Test
	void create() {

		var tenantId = data.tenantId();
		var vo = new TenantVO()
				.setTenantId(tenantId)
				.setName(data.tenantName())
				.setEnabled(true)
				.setCreated(Instant.now())
				.setUpdated(Instant.now());

		send(CloudEventSubjectVO.CREATE, vo);
		send(CloudEventSubjectVO.UPDATE, vo.setEnabled(false).setName(data.tenantName()).setUpdated(Instant.now()));
		assertEquals(vo.getName(), data.findTenant(tenantId).getName());
		assertEquals(vo.getEnabled(), data.findTenant(tenantId).getEnabled());
		send(CloudEventSubjectVO.DELETE, vo.setDeleted(Instant.now()));
		assertEquals(vo.getDeleted(), data.findTenant(tenantId).getDeleted());
	}

	@DisplayName("configuration")
	@Test
	void configuration() {

		var vo = new TenantVO()
				.setTenantId(data.tenantId())
				.setName(data.tenantName())
				.setEnabled(true)
				.setCreated(Instant.now())
				.setUpdated(Instant.now());
		var tenant = send(CloudEventSubjectVO.CREATE, vo);

		var ntp = data.findConfigurationDefinition(tenant, "ntp.host");
		assertNotNull(ntp, "ntp definition");
		assertAll("ntp",
				() -> assertNotNull(ntp.getDescription(), "description"),
				() -> assertEquals(ConfigurationTypeVO.STRING, ntp.getType(), "type"),
				() -> assertNull(ntp.getMinimum(), "minimum"),
				() -> assertNull(ntp.getMaximum(), "maximum"),
				() -> assertEquals("[a-z0-9\\.]{8,20}", ntp.getPattern(), "pattern"),
				() -> assertEquals("pool.ntp.org", data.findConfigurationValue(ntp), "value"));

		var mqtt = data.findConfigurationDefinition(tenant, "mqtt.uri");
		assertNotNull(mqtt, "mqtt definition");
		assertAll("mqtt",
				() -> assertNotNull(mqtt.getDescription(), "description"),
				() -> assertEquals(ConfigurationTypeVO.STRING, mqtt.getType(), "type"),
				() -> assertNull(mqtt.getMinimum(), "minimum"),
				() -> assertNull(mqtt.getMaximum(), "maximum"),
				() -> assertEquals("(tcp|mqtt|ssl|mqtts)://[a-z0-9\\.\\-]+:[0-9]{3,5}", mqtt.getPattern(), "pattern"),
				() -> assertNull(data.findConfigurationValue(mqtt), "value"));
	}

	private Tenant send(CloudEventSubjectVO subject, TenantVO vo) {

		listener.receive(vo.getTenantId(), CloudEventBuilder.v1()
				.withSource(URI.create("/inoa/tenant/" + vo.getTenantId()))
				.withId("create@now")
				.withType(CloudEventTypeVO.TENANT_VALUE)
				.withSubject(subject.getValue())
				.withData(PojoCloudEventData.wrap(vo, cloudEventObjectMapper::writeValueAsBytes))
				.build());

		var tenant = data.findTenant(vo.getTenantId());
		assertEquals(vo.getTenantId(), tenant.getTenantId(), "tenantId");
		assertEquals(vo.getEnabled(), tenant.getEnabled(), "enabled");
		assertEquals(vo.getName(), tenant.getName(), "name");
		assertEquals(vo.getCreated(), tenant.getCreated(), "created");
		assertEquals(vo.getUpdated(), tenant.getUpdated(), "updated");
		assertEquals(vo.getDeleted(), tenant.getDeleted(), "deleted");
		return tenant;
	}
}
