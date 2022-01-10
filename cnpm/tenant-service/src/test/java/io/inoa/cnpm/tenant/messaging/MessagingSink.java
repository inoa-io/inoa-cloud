package io.inoa.cnpm.tenant.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.inoa.cnpm.tenant.domain.Assignment;
import io.inoa.cnpm.tenant.domain.TenantTestRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.MediaType;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * Message sink for tests.
 */
@Replaces(MessagingClient.class)
@Singleton
@RequiredArgsConstructor
public class MessagingSink extends MessagingClient {

	private final List<Entry<String, CloudEvent>> records = new ArrayList<>();
	private final TenantTestRepository tenantRepository;

	public void deleteAll() {
		records.clear();
	}

	public void assertTenant(String tenantId, CloudEventSubjectVO subject) {
		var actual = await(tenantId, CloudEventTypeVO.TENANT, subject, TenantVO.class);
		var expected = tenantRepository.findByTenantId(tenantId).get();
		assertEquals(expected.getTenantId(), actual.getTenantId(), "event.data.tenantId");
		assertEquals(expected.getName(), actual.getName(), "event.data.name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "event.data.enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "event.data.created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "event.data.updated");
	}

	public void assertAssignment(Assignment expected, CloudEventSubjectVO subject) {
		var actual = await(expected.getTenant().getTenantId(), CloudEventTypeVO.USER, subject, UserVO.class);
		assertEquals(expected.getTenant().getTenantId(), actual.getTenantId(), "event.data.tenantId");
		assertEquals(expected.getUser().getUserId(), actual.getUserId(), "event.data.userId");
		assertEquals(expected.getUser().getEmail(), actual.getEmail(), "event.data.email");
		assertEquals(expected.getRole().getValue(), actual.getRole(), "event.data.enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "event.data.created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "event.data.updated");
	}

	<T> T await(String tenantId, CloudEventTypeVO type, CloudEventSubjectVO subject, Class<T> payload) {
		var record = records.stream()
				.filter(r -> r.getKey().equals(tenantId))
				.filter(r -> r.getValue().getType().equals(type.getValue()))
				.filter(r -> r.getValue().getSubject().equals(subject.getValue()))
				.findAny().orElse(null);
		assertNotNull(record, "record");
		assertNotNull(record.getValue().getTime(), "event.time");
		assertEquals(subject.getValue(), record.getValue().getSubject(), "event.subject");
		assertEquals(MediaType.APPLICATION_JSON, record.getValue().getDataContentType(), "event.datatype");
		return PojoCloudEventDataMapper
				.from(MessagingClient.MAPPER, payload)
				.map(record.getValue().getData())
				.getValue();
	}

	@Override
	void send(String tenantId, CloudEvent tenant) {
		records.add(Map.entry(tenantId, tenant));
	}
}
