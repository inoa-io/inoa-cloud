package io.inoa.fleet.registry.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Singleton;

import io.inoa.fleet.FleetProperties;
import io.inoa.fleet.registry.domain.Configuration;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayConfigurationRepository;
import io.inoa.fleet.registry.domain.GatewayGroupRepository;
import io.inoa.fleet.registry.domain.GroupConfigurationRepository;
import io.inoa.fleet.registry.domain.TenantConfigurationRepository;
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
	private final FleetProperties fleetProperties;

	public List<Configuration> findByGateway(Gateway gateway) {
		var configurations = new ArrayList<Configuration>();
		configurations.addAll(fleetProperties.getTenant().getConfigurations());
		configurations.addAll(tenantConfigurationRepository.findByTenant(gateway.getTenant()));
		var groups = gatewayGroupRepository.findGroupByGateway(gateway);
		if (!groups.isEmpty()) {
			configurations.addAll(groupConfigurationRepository.findByGroupIn(groups));
		}
		configurations.addAll(gatewayConfigurationRepository.findByGateway(gateway));
		return configurations;
	}
}
