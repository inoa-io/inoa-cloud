package io.kokuwa.fleet.registry.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import io.kokuwa.fleet.registry.domain.Configuration;
import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayConfigurationRepository;
import io.kokuwa.fleet.registry.domain.GatewayGroupRepository;
import io.kokuwa.fleet.registry.domain.GroupConfigurationRepository;
import io.kokuwa.fleet.registry.domain.TenantConfigurationRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service for configuration.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
public class ConfigurationService {

	private final GatewayGroupRepository gatewayGroupRepository;
	private final TenantConfigurationRepository tenantConfigurationRepository;
	private final GroupConfigurationRepository groupConfigurationRepository;
	private final GatewayConfigurationRepository gatewayConfigurationRepository;

	public List<Configuration> findByGateway(Gateway gateway) {
		var configurations = new ArrayList<Configuration>();
		configurations.addAll(tenantConfigurationRepository.findByDefinitionTenant(gateway.getTenant()));
		var groups = gatewayGroupRepository.findGroupByGateway(gateway);
		if (!groups.isEmpty()) {
			configurations.addAll(groupConfigurationRepository.findByGroupIn(groups));
		}
		configurations.addAll(gatewayConfigurationRepository.findByGateway(gateway));
		return configurations;
	}
}
