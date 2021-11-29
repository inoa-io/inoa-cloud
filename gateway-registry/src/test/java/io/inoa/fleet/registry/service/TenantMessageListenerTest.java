package io.inoa.fleet.registry.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.kafka.CloudEventSerializer;
import io.inoa.cnpm.tenant.messaging.TenantVO;
import io.inoa.fleet.registry.AbstractTest;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.rest.management.ConfigurationTypeVO;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;

/**
 * Test for {@link TenantMessageListener}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("messaging: tenant listener")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TenantMessageListenerTest extends AbstractTest implements TestPropertyProvider {

	@Inject
	ObjectMapper cloudEventObjectMapper;
	@Inject
	Producer<String, CloudEvent> producer;

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

		send("create", vo, t -> t != null);
		send("update", vo.setEnabled(false).setName(data.tenantName()).setUpdated(Instant.now()), t -> !t.getEnabled());
		send("delete", vo.setDeleted(Instant.now()), t -> t.getDeleted() != null);
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
		var tenant = send("create", vo, t -> t != null);

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

	private Tenant send(String action, TenantVO vo, Predicate<Tenant> predicate) {

		var future = producer.send(new ProducerRecord<>("inoa.tenant", vo.getTenantId(), CloudEventBuilder.v1()
				.withSource(URI.create("/inoa/tenant/" + vo.getTenantId()))
				.withId("create@now")
				.withType("io.inoa.tenant")
				.withData(PojoCloudEventData.wrap(vo, cloudEventObjectMapper::writeValueAsBytes))
				.build()));
		Awaitility.await("send " + action + " record").until(future::isDone);

		var tenant = Awaitility.await(action + " tenant").until(() -> data.findTenant(vo.getTenantId()), predicate);
		assertEquals(vo.getTenantId(), tenant.getTenantId(), "tenantId");
		assertEquals(vo.getEnabled(), tenant.getEnabled(), "enabled");
		assertEquals(vo.getName(), tenant.getName(), "name");
		assertEquals(vo.getCreated(), tenant.getCreated(), "created");
		assertEquals(vo.getUpdated(), tenant.getUpdated(), "updated");
		assertEquals(vo.getDeleted(), tenant.getDeleted(), "deleted");

		return tenant;
	}

	@Override
	public Map<String, String> getProperties() {
		var kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.1.2"));
		kafka.start();
		return Map.of(
				"kafka.bootstrap.servers", kafka.getBootstrapServers(),
				"kafka.consumers.default.metadata.max.age.ms", "200",
				"kafka.producers.default.key.serializer", StringSerializer.class.getName(),
				"kafka.producers.default.value.serializer", CloudEventSerializer.class.getName());
	}
}
