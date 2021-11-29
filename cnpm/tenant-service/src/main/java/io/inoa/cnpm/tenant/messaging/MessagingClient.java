package io.inoa.cnpm.tenant.messaging;

import java.net.URI;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.MediaType;

/**
 * Client for publishing tenant events to Kafka.
 *
 * @author Stephan Schnabel
 */
@KafkaClient
public abstract class MessagingClient {

	public static final String ACTION_CREATE = "create";
	public static final String ACTION_UPDATE = "update";
	public static final String ACTION_DELETE = "delete";
	public static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

	public void sendCloudEvent(Tenant tenant, String action) {
		var payload = new TenantVO()
				.setTenantId(tenant.getTenantId())
				.setName(tenant.getName())
				.setEnabled(tenant.getEnabled())
				.setCreated(tenant.getCreated())
				.setUpdated(tenant.getUpdated())
				.setDeleted(tenant.getDeleted());
		var now = OffsetDateTime.now();
		sendCloudEvent(tenant.getTenantId(), CloudEventBuilder.v1()
				.withSource(URI.create("/inoa/tenant/" + tenant.getTenantId()))
				.withId(action + "@" + now)
				.withSubject(action)
				.withType("io.inoa.cnpm.tenant")
				.withTime(OffsetDateTime.now())
				.withDataContentType(MediaType.APPLICATION_JSON)
				.withData(PojoCloudEventData.wrap(payload, MAPPER::writeValueAsBytes))
				.build());
	}

	@Topic("inoa.tenant")
	abstract void sendCloudEvent(@KafkaKey String tenantId, CloudEvent tenant);
}
