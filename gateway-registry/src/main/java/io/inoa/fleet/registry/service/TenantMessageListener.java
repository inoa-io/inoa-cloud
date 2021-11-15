package io.inoa.fleet.registry.service;

import org.slf4j.MDC;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.rw.CloudEventDataMapper;
import io.inoa.cloud.messages.TenantVO;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Message listener for tenants.
 *
 * @author Stephan Schnabel
 */
@KafkaListener(offsetReset = OffsetReset.EARLIEST, redelivery = true)
@Slf4j
@RequiredArgsConstructor
public class TenantMessageListener {

	private final CloudEventDataMapper<PojoCloudEventData<TenantVO>> mapper;
	private final TenantRepository repository;

	@Topic("inoa.tenant")
	void receive(@KafkaKey String tenantId, CloudEvent event) {
		MDC.put("tenantId", tenantId);

		var vo = mapper.map(event.getData()).getValue();
		repository.findByTenantId(tenantId).ifPresentOrElse(tenant -> {
			repository.update(map(tenant, vo));
			log.info("Tenant {} created: {}", tenantId, tenant);
		}, () -> {
			var tenant = repository.save(map(new Tenant(), vo));
			log.info("Tenant {} updated: {}", tenantId, tenant);
		});
	}

	private Tenant map(Tenant tenant, TenantVO vo) {
		return tenant
				.setTenantId(vo.getTenantId())
				.setEnabled(vo.getEnabled())
				.setName(vo.getName())
				.setCreated(vo.getCreated())
				.setUpdated(vo.getUpdated())
				.setDeleted(vo.getDeleted());
	}
}
