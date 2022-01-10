package io.inoa.measurement.provisioner.messaging;

import java.util.Base64;
import java.util.Objects;
import java.util.Set;

import org.slf4j.MDC;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;
import io.inoa.cnpm.tenant.messaging.CloudEventSubjectVO;
import io.inoa.cnpm.tenant.messaging.CloudEventTypeVO;
import io.inoa.cnpm.tenant.messaging.TenantVO;
import io.inoa.cnpm.tenant.messaging.UserVO;
import io.inoa.measurement.provisioner.service.TenantService;
import io.inoa.measurement.provisioner.service.UserService;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Requires;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Message listener for CNPM.
 */
@Requires(classes = CloudEventDataMapper.class)
@KafkaListener(offsetReset = OffsetReset.EARLIEST, redelivery = true)
@Slf4j
@RequiredArgsConstructor
public class EventListener {

	private static final Set<String> SUPPORTED_SUBJECTS = Set.of(
			CloudEventSubjectVO.CREATE_VALUE,
			CloudEventSubjectVO.UPDATE_VALUE,
			CloudEventSubjectVO.DELETE_VALUE);

	private final ObjectMapper mapper;
	private final TenantService tenantService;
	private final UserService userService;

	@Topic("inoa.cnpm")
	void handle(@KafkaKey String tenantId, CloudEvent event) {
		try (var mdc = MDC.putCloseable("tenantId", tenantId)) {

			var type = event.getType();
			var subject = event.getSubject();
			log.trace("Got event with type {} and subject {}", type, subject);

			if (!SUPPORTED_SUBJECTS.contains(subject)) {
				log.info("Ignore unsupported subject {}", subject);
				return;
			}

			switch (type) {
				case CloudEventTypeVO.TENANT_VALUE:
					handleTenant(event);
					break;
				case CloudEventTypeVO.USER_VALUE:
					handleUser(tenantId, event);
					break;
				default:
					log.info("Ignore unsupported type {}", type);
			}
		}
	}

	void handleTenant(CloudEvent event) {

		TenantVO tenant;
		try {
			tenant = PojoCloudEventDataMapper.from(mapper, TenantVO.class).map(event.getData()).getValue();
		} catch (CloudEventRWException e) {
			log.warn("Failed to parse event payload to tenant: {}", e.getMessage());
			log.warn("Invalid payload: {}", Base64.getEncoder().encodeToString(event.getData().toBytes()));
			return;
		}

		if (Objects.equals(CloudEventSubjectVO.DELETE_VALUE, event.getSubject())) {
			tenantService.delete(tenant);
		} else {
			tenantService.save(tenant);
		}
	}

	void handleUser(String tenantId, CloudEvent event) {

		UserVO user;
		try {
			user = PojoCloudEventDataMapper.from(mapper, UserVO.class).map(event.getData()).getValue();
		} catch (CloudEventRWException e) {
			log.warn("Failed to parse event payload to user: {}", e.getMessage());
			log.warn("Invalid payload: {}", Base64.getEncoder().encodeToString(event.getData().toBytes()));
			return;
		}

		try (var mdc = MDC.putCloseable("userId", user.getUserId().toString())) {
			if (Objects.equals(CloudEventSubjectVO.DELETE_VALUE, event.getSubject())) {
				userService.delete(tenantId, user);
			} else {
				userService.save(tenantId, user);
			}
		}
	}
}
