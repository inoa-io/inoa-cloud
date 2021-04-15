package io.kokuwa.fleet.registry;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayGroup;
import io.kokuwa.fleet.registry.domain.GatewayGroup.GatewayGroupPK;
import io.kokuwa.fleet.registry.domain.GatewayGroupRepository;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.domain.GatewayProperty.GatewayPropertyPK;
import io.kokuwa.fleet.registry.domain.GatewayPropertyRepository;
import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.GroupRepository;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.domain.TenantRepository;
import lombok.RequiredArgsConstructor;

/**
 * Class to create testdata.
 *
 * @author Stephan Schnabel
 */
@Singleton
@Transactional(TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class Data {

	private final TenantRepository tenantRepository;
	private final GroupRepository groupRepository;
	private final GatewayRepository gatewayRepository;
	private final GatewayGroupRepository gatewayGroupRepository;
	private final GatewayPropertyRepository gatewayPropertyRepository;

	void deleteAll() {
		gatewayRepository.deleteAll().blockingAwait();
		tenantRepository.deleteAll().blockingAwait();
	}

	// manipulation

	public String tenantName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Tenant tenant() {
		return tenant(tenantName(), true);
	}

	public Tenant tenant(String name) {
		return tenant(name, true);
	}

	public Tenant tenant(String name, boolean enabled) {
		return tenantRepository
				.save(new Tenant()
						.setName(name)
						.setEnabled(enabled))
				.blockingGet();
	}

	public String groupName() {
		return UUID.randomUUID().toString().substring(0, 20);
	}

	public Group group() {
		return group(tenant(), groupName());
	}

	public Group group(Tenant tenant) {
		return group(tenant, groupName());
	}

	public Group group(Tenant tenant, String name) {
		return groupRepository
				.save(new Group()
						.setTenant(tenant)
						.setName(name))
				.blockingGet();
	}

	public String gatewayName() {
		return UUID.randomUUID().toString().substring(0, 32);
	}

	public Gateway gateway() {
		return gateway(tenant());
	}

	public Gateway gateway(Map<String, String> properties) {
		return gateway(tenant(), List.of(), properties);
	}

	public Gateway gateway(Group group) {
		return gateway(group.getTenant(), gatewayName(), true, List.of(group));
	}

	public Gateway gateway(Tenant tenant) {
		return gateway(tenant, gatewayName(), true, List.of());
	}

	public Gateway gateway(Tenant tenant, String name) {
		return gateway(tenant, name, true, List.of());
	}

	public Gateway gateway(Tenant tenant, boolean enabled) {
		return gateway(tenant, gatewayName(), enabled, List.of());
	}

	public Gateway gateway(Tenant tenant, List<Group> groups, Map<String, String> properties) {
		var gateway = gateway(tenant, gatewayName(), true, groups);
		properties.forEach((key, value) -> property(gateway, key, value));
		return gateway;
	}

	public Gateway gateway(Tenant tenant, String name, boolean enabled, List<Group> groups) {
		Gateway gateway = gatewayRepository
				.save(new Gateway()
						.setTenant(tenant)
						.setName(name)
						.setEnabled(enabled))
				.blockingGet();
		gatewayGroupRepository
				.saveAll(groups.stream()
						.map(group -> new GatewayGroup().setPk(new GatewayGroupPK(gateway.getId(), group.getId())))
						.collect(Collectors.toSet()))
				.ignoreElements()
				.blockingGet();
		return gateway;
	}

	public GatewayProperty property(Gateway gateway, String key, String value) {
		return gatewayPropertyRepository
				.save(new GatewayProperty()
						.setPk(new GatewayPropertyPK(gateway.getId(), key))
						.setValue(value))
				.blockingGet();
	}

	// read

	public Long countTenants() {
		return tenantRepository.count().blockingGet();
	}

	public Long countGroups() {
		return groupRepository.count().blockingGet();
	}

	public Long countGateways() {
		return gatewayRepository.count().blockingGet();
	}

	public Tenant find(Tenant tenant) {
		return tenantRepository.findById(tenant.getId()).blockingGet();
	}

	public Group find(Group group) {
		return groupRepository.findById(group.getId()).blockingGet().setTenant(group.getTenant());
	}

	public Gateway find(Gateway gateway) {
		return gatewayRepository.findByExternalId(gateway.getExternalId()).blockingGet();
	}

	public List<GatewayProperty> findProperties(Gateway gateway) {
		return gatewayPropertyRepository.findByGatewayId(gateway.getId()).toList().blockingGet();
	}
}
