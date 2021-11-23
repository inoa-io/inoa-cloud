package io.inoa.fleet.registry.service;

import javax.transaction.Transactional;

import org.slf4j.MDC;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.rw.CloudEventDataMapper;
import io.inoa.cloud.messages.TenantVO;
import io.inoa.fleet.registry.ApplicationProperties;
import io.inoa.fleet.registry.domain.ConfigurationDefinitionRepository;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantConfiguration;
import io.inoa.fleet.registry.domain.TenantConfigurationRepository;
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
	private final TenantRepository tenantRepository;
	private final ConfigurationDefinitionRepository definitionRepository;
	private final TenantConfigurationRepository configurationRepository;
	private final ApplicationProperties properties;

	@Transactional
	@Topic("inoa.tenant")
	void receive(@KafkaKey String tenantId, CloudEvent event) {
		MDC.put("tenantId", tenantId);
		var vo = mapper.map(event.getData()).getValue();
		tenantRepository.findByTenantId(tenantId).ifPresentOrElse(tenant -> update(tenant, vo), () -> create(vo));
	}

	private void create(TenantVO vo) {

		// create tenant

		var tenant = tenantRepository.save(map(new Tenant(), vo));
		log.info("Tenant {} created: {}", vo.getTenantId(), tenant);

		// save default configuration

		var configurations = properties.getTenant().getConfigurations();
		log.trace("Found {} default configurations to create.", configurations.size());
		for (var configuration : configurations) {
			var definition = definitionRepository.save(configuration.toConfigurationDefinition(tenant));
			var value = configuration.getValue();
			if (value != null) {
				configurationRepository.save(new TenantConfiguration().setDefinition(definition).setValue(value));
			}
			log.trace("Created definition {} with value {}.", configuration.getKey(), value);
		}
	}

	private void update(Tenant tenant, TenantVO vo) {
		tenantRepository.update(map(tenant, vo));
		log.info("Tenant {} updated: {}", vo.getTenantId(), tenant);
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
