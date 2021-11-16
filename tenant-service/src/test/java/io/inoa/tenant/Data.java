package io.inoa.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.PojoCloudEventDataMapper;
import io.inoa.cloud.messages.TenantVO;
import io.inoa.tenant.domain.Tenant;
import io.inoa.tenant.domain.TenantTestRepository;
import io.inoa.tenant.domain.TenantUser;
import io.inoa.tenant.domain.TenantUserRepository;
import io.inoa.tenant.domain.User;
import io.inoa.tenant.domain.UserTestRepository;
import io.inoa.tenant.management.MessagingClient;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.http.MediaType;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
@Singleton
@RequiredArgsConstructor
public class Data {

	private final List<ConsumerRecord<String, CloudEvent>> records = new ArrayList<>();
	private final PojoCloudEventDataMapper<TenantVO> mapper = PojoCloudEventDataMapper
			.from(MessagingClient.MAPPER, TenantVO.class);
	private final TenantTestRepository tenantRepository;
	private final TenantUserRepository tenantUserRepository;
	private final UserTestRepository userRepository;

	void deleteAll() {
		tenantRepository.deleteAll();
		userRepository.deleteAll();
		records.clear();
	}

	// kafka

	@Topic("inoa.tenant")
	void receive(ConsumerRecord<String, CloudEvent> record) {
		records.add(record);
	}

	public void assertEvent(String tenantId, String action) {

		var record = Awaitility
				.await("retrieve message")
				.until(() -> records.isEmpty() ? null : records.get(0), r -> r != null);
		assertEquals(tenantId, record.key(), "event.tenantId");
		assertNotNull(record.value().getTime(), "event.time");
		assertEquals(action, record.value().getSubject(), "event.subject");
		assertEquals(MediaType.APPLICATION_JSON, record.value().getDataContentType(), "event.datatype");

		var actual = mapper.map(record.value().getData()).getValue();
		var expected = tenantRepository.findByTenantId(tenantId).get();
		assertEquals(expected.getTenantId(), actual.getTenantId(), "event.data.tenantId");
		assertEquals(expected.getName(), actual.getName(), "event.data.name");
		assertEquals(expected.getEnabled(), actual.getEnabled(), "event.data.enabled");
		assertEquals(expected.getCreated(), actual.getCreated(), "event.data.created");
		assertEquals(expected.getUpdated(), actual.getUpdated(), "event.data.updated");
	}

	// create data

	public String tenantId() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public String tenantName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Tenant tenant() {
		return tenant(tenantId(), tenantName(), true, false);
	}

	public Tenant tenantDeleted() {
		return tenant(tenantId(), tenantName(), true, true);
	}

	public Tenant tenant(String tenantId, String name, boolean enabled, boolean deleted) {
		return tenantRepository.save(new Tenant()
				.setTenantId(tenantId)
				.setName(name)
				.setEnabled(enabled)
				.setDeleted(deleted ? Instant.now() : null));
	}

	public String userEmail() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public UUID userId() {
		return UUID.randomUUID();
	}

	public User user(Tenant... tenants) {
		return user(userEmail(), tenants);
	}

	public User user(String email, Tenant... tenants) {
		var user = userRepository.save(new User().setUserId(UUID.randomUUID()).setEmail(email));
		Stream.of(tenants).forEach(tenant -> tenantUserRepository.save(new TenantUser(tenant, user)));
		return user;
	}

	// check

	public Optional<Tenant> findTenant(String tenantId) {
		return tenantRepository.findByTenantId(tenantId);
	}

	public void assertAssignment(String email, String... expectedTenantIds) {
		assertTrue(StreamSupport
				.stream(userRepository.findAll().spliterator(), false)
				.anyMatch(user -> user.getEmail().equals(email)), "user not found");
		assertEquals(Set.of(expectedTenantIds), tenantUserRepository
				.findTenantByUserEmail(email).stream().map(Tenant::getTenantId).collect(Collectors.toSet()),
				"assignments");
	}

	public void assertTenantSoftDelete(String tenantId) {
		assertNotNull(tenantRepository.findByTenantId(tenantId).map(Tenant::getDeleted).get(), "assignments");
	}
}
