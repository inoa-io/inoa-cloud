package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;

import io.inoa.fleet.registry.domain.ConfigurationDefinition;
import io.inoa.fleet.registry.domain.ConfigurationDefinitionRepository;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayConfiguration;
import io.inoa.fleet.registry.domain.GatewayConfigurationRepository;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.Group;
import io.inoa.fleet.registry.domain.GroupConfiguration;
import io.inoa.fleet.registry.domain.GroupConfigurationRepository;
import io.inoa.fleet.registry.domain.GroupRepository;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantConfiguration;
import io.inoa.fleet.registry.domain.TenantConfigurationRepository;
import io.inoa.fleet.registry.rest.mapper.ConfigurationMapper;
import io.inoa.fleet.registry.rest.validation.ConfigurationDefinitionValid;
import io.inoa.rest.ConfigurationApi;
import io.inoa.rest.ConfigurationDefinitionVO;
import io.inoa.rest.ConfigurationSetVO;
import io.inoa.rest.ConfigurationVO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link ConfigurationApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@RequiredArgsConstructor
public class ConfigurationController extends AbstractManagementController
		implements ConfigurationApi {

	private final Security security;
	private final ConfigurationMapper mapper;
	private final ConfigurationDefinitionRepository definitionRepository;
	private final TenantConfigurationRepository tenantConfigurationRepository;
	private final GroupRepository groupRepository;
	private final GroupConfigurationRepository groupConfigurationRepository;
	private final GatewayRepository gatewayRepository;
	private final GatewayConfigurationRepository gatewayConfigurationRepository;

	// definition

	@Override
	public HttpResponse<List<ConfigurationDefinitionVO>> findConfigurationDefinitions() {
		return HttpResponse.ok(mapper.toDefinitions(definitionRepository.findAll()));
	}

	@Override
	public HttpResponse<ConfigurationDefinitionVO> createConfigurationDefinition(
			@NonNull String key,
			@Valid @ConfigurationDefinitionValid ConfigurationDefinitionVO vo,
			@NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		if (!Objects.equals(key, vo.getKey())) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Key in path differs from model.");
		}
		if (definitionRepository.existsByKey(vo.getKey())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Key already exists.");
		}
		ConfigurationDefinition definition = definitionRepository.save(mapper.toDefinition(vo));
		return HttpResponse.created(mapper.toDefinition(definition));
	}

	@Override
	public HttpResponse<Object> deleteConfigurationDefinition(
			@NonNull String key, @NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		var optional = definitionRepository.findByKey(key);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Definition not found.");
		}
		definitionRepository.delete(optional.get());
		return HttpResponse.noContent();
	}

	// configuration

	@Override
	public HttpResponse<List<ConfigurationVO>> findConfigurations() {
		var tenants = security.getGrantedTenants();
		var configurations = tenantConfigurationRepository.findByTenantInList(tenants);
		return HttpResponse.ok(mapper.toConfigurations(configurations));
	}

	@Override
	public HttpResponse<List<ConfigurationVO>> findConfigurationsByGroup(
			@NonNull UUID groupId, @NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		var group = getGroup(tenant, groupId);
		var configurations = groupConfigurationRepository.findByGroup(group);
		return HttpResponse.ok(mapper.toConfigurations(configurations));
	}

	@Override
	public HttpResponse<List<ConfigurationVO>> findConfigurationsByGateway(
			@NonNull String gatewayId) {
		var tenants = security.getGrantedTenants();
		var gateway = getGateway(tenants, gatewayId);
		var configurations = gatewayConfigurationRepository.findByGateway(gateway);
		return HttpResponse.ok(mapper.toConfigurations(configurations));
	}

	@Override
	public HttpResponse<Object> setConfiguration(
			@NonNull String key, @Valid ConfigurationSetVO vo, @NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		var definition = getConfigurationDefinition(tenant, key);
		var value = mapper.toString(definition, vo.getValue());
		tenantConfigurationRepository
				.findByDefinition(definition)
				.ifPresentOrElse(
						c -> tenantConfigurationRepository.update(definition.getId(), value),
						() -> tenantConfigurationRepository.save(
								new TenantConfiguration()
										.setDefinition(definition)
										.setValue(value)
										.setTenant(tenant)));
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> setConfigurationByGroup(
			@NonNull UUID groupId,
			@NonNull String key,
			@Valid ConfigurationSetVO vo,
			@NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		var group = getGroup(tenant, groupId);
		var definition = getConfigurationDefinition(tenant, key);
		var value = mapper.toString(definition, vo.getValue());
		groupConfigurationRepository
				.findByGroupAndDefinition(group, definition)
				.ifPresentOrElse(
						c -> groupConfigurationRepository.update(group.getId(), definition.getId(), value),
						() -> groupConfigurationRepository.save(
								new GroupConfiguration()
										.setGroup(group)
										.setDefinition(definition)
										.setValue(value)));
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> setConfigurationByGateway(
			@NonNull String gatewayId,
			@NonNull String key,
			@Valid ConfigurationSetVO vo,
			@NonNull Optional<String> tenantId) {
		var tenants = security.getGrantedTenants();
		var gateway = getGateway(tenants, gatewayId);
		var definition = getConfigurationDefinition(gateway.getTenant(), key);
		var value = mapper.toString(definition, vo.getValue());
		gatewayConfigurationRepository
				.findByGatewayAndDefinition(gateway, definition)
				.ifPresentOrElse(
						c -> gatewayConfigurationRepository.update(gateway.getId(), definition.getId(), value),
						() -> gatewayConfigurationRepository.save(
								new GatewayConfiguration()
										.setGateway(gateway)
										.setDefinition(definition)
										.setValue(value)));
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> resetConfiguration(
			@NonNull String key, @NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		var definition = getConfigurationDefinition(tenant, key);
		var configuration = tenantConfigurationRepository.findByDefinition(definition);
		if (configuration.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Configuration not found.");
		}
		tenantConfigurationRepository.delete(definition.getId());
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> resetConfigurationByGroup(
			@NonNull UUID groupId, @NonNull String key, @NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		var group = getGroup(tenant, groupId);
		var definition = getConfigurationDefinition(tenant, key);
		var configuration = groupConfigurationRepository.findByGroupAndDefinition(group, definition);
		if (configuration.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Configuration not found.");
		}
		groupConfigurationRepository.delete(group.getId(), definition.getId());
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> resetConfigurationByGateway(
			@NonNull String gatewayId, @NonNull String key) {
		var tenants = security.getGrantedTenants();
		var gateway = getGateway(tenants, gatewayId);
		var definition = getConfigurationDefinition(gateway.getTenant(), key);
		var configuration = gatewayConfigurationRepository.findByGatewayAndDefinition(gateway, definition);
		if (configuration.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Configuration not found.");
		}
		gatewayConfigurationRepository.delete(gateway.getId(), definition.getId());
		return HttpResponse.noContent();
	}

	private ConfigurationDefinition getConfigurationDefinition(Tenant tenant, String key) {
		var optional = definitionRepository.findByKey(key);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Definition not found.");
		}
		return optional.get();
	}

	private Group getGroup(Tenant tenant, UUID groupId) {
		var optional = groupRepository.findByTenantAndGroupId(tenant, groupId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
		}
		return optional.get();
	}

	private Gateway getGateway(List<Tenant> tenants, String gatewayId) {
		var optional = gatewayRepository.findByTenantInListAndGatewayId(tenants, gatewayId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}
		return optional.get();
	}
}
