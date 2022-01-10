package io.inoa.cnpm.tenant.messaging;

import java.net.URI;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.inoa.cnpm.tenant.domain.Assignment;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.MediaType;

/**
 * Client for publishing events to Kafka.
 */
@KafkaClient
public abstract class MessagingClient {

	public static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

	public void send(Tenant tenant, CloudEventSubjectVO subject) {
		var tenantId = tenant.getTenantId();
		var payload = new TenantVO()
				.setTenantId(tenantId)
				.setName(tenant.getName())
				.setEnabled(tenant.getEnabled())
				.setCreated(tenant.getCreated())
				.setUpdated(tenant.getUpdated())
				.setDeleted(tenant.getDeleted());
		send(tenantId, event(subject, "/inoa/tenant/" + tenantId, CloudEventTypeVO.TENANT, payload));
	}

	public void send(Assignment assignement, CloudEventSubjectVO subject) {
		var tenantId = assignement.getTenant().getTenantId();
		var userId = assignement.getUser().getUserId();
		var payload = new UserVO()
				.setTenantId(tenantId)
				.setUserId(userId)
				.setEmail(assignement.getUser().getEmail())
				.setRole(assignement.getRole().getValue())
				.setCreated(assignement.getCreated())
				.setUpdated(assignement.getUpdated());
		send(tenantId, event(subject, "/inoa/tenant/" + tenantId + "/user/" + userId, CloudEventTypeVO.USER, payload));
	}

	@Topic("inoa.cnpm")
	abstract void send(@KafkaKey String tenantId, CloudEvent tenant);

	private CloudEvent event(CloudEventSubjectVO subject, String uri, CloudEventTypeVO type, Object payload) {
		var now = OffsetDateTime.now();
		return CloudEventBuilder.v1()
				.withSource(URI.create(uri))
				.withId(subject.getValue() + "@" + now)
				.withSubject(subject.getValue())
				.withType(type.getValue())
				.withTime(now)
				.withDataContentType(MediaType.APPLICATION_JSON)
				.withData(PojoCloudEventData.wrap(payload, MAPPER::writeValueAsBytes))
				.build();
	}
}
