package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;

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
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link CredentialsApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@RequiredArgsConstructor
public class ConfigurationController implements ConfigurationApi {

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
		return HttpResponse.ok(mapper.toDefinitions(definitionRepository.findByTenantOrderByKey(security.getTenant())));
	}

	@Override
	public HttpResponse<ConfigurationDefinitionVO> createConfigurationDefinition(String key,
			@Valid @ConfigurationDefinitionValid ConfigurationDefinitionVO vo) {
		var tenant = security.getTenant();
		if (!Objects.equals(key, vo.getKey())) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Key in path differs from model.");
		}
		if (definitionRepository.existsByTenantAndKey(tenant, vo.getKey())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Key already exists.");
		}
		var definition = definitionRepository.save(mapper.toDefinition(vo).setTenant(tenant));
		return HttpResponse.created(mapper.toDefinition(definition));
	}

	@Override
	public HttpResponse<Object> deleteConfigurationDefinition(String key) {
		var optional = definitionRepository.findByTenantAndKey(security.getTenant(), key);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Definition not found.");
		}
		definitionRepository.delete(optional.get());
		return HttpResponse.noContent();
	}

	// configuration

	@Override
	public HttpResponse<List<ConfigurationVO>> findConfigurations() {
		var tenant = security.getTenant();
		var configurations = tenantConfigurationRepository.findByDefinitionTenant(tenant);
		return HttpResponse.ok(mapper.toConfigurations(configurations));
	}

	@Override
	public HttpResponse<List<ConfigurationVO>> findConfigurationsByGroup(UUID groupId) {
		var tenant = security.getTenant();
		var group = getGroup(tenant, groupId);
		var configurations = groupConfigurationRepository.findByGroup(group);
		return HttpResponse.ok(mapper.toConfigurations(configurations));
	}

	@Override
	public HttpResponse<List<ConfigurationVO>> findConfigurationsByGateway(UUID gatewayId) {
		var tenant = security.getTenant();
		var gateway = getGateway(tenant, gatewayId);
		var configurations = gatewayConfigurationRepository.findByGateway(gateway);
		return HttpResponse.ok(mapper.toConfigurations(configurations));
	}

	@Override
	public HttpResponse<Object> setConfiguration(String key, @Valid ConfigurationSetVO vo) {
		var tenant = security.getTenant();
		var definition = getConfigurationDefinition(tenant, key);
		var value = mapper.toString(definition, vo.getValue());
		tenantConfigurationRepository.findByDefinition(definition).ifPresentOrElse(
				c -> tenantConfigurationRepository.updateByDefinition(definition.getId(), value),
				() -> tenantConfigurationRepository.save(new TenantConfiguration()
						.setDefinition(definition)
						.setValue(value)));
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> setConfigurationByGroup(UUID groupId, String key, @Valid ConfigurationSetVO vo) {
		var tenant = security.getTenant();
		var group = getGroup(tenant, groupId);
		var definition = getConfigurationDefinition(tenant, key);
		var value = mapper.toString(definition, vo.getValue());
		groupConfigurationRepository.findByGroupAndDefinition(group, definition).ifPresentOrElse(
				c -> groupConfigurationRepository.updateByGroupAndDefinition(group.getId(), definition.getId(), value),
				() -> groupConfigurationRepository.save(new GroupConfiguration()
						.setGroup(group)
						.setDefinition(definition)
						.setValue(value)));
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> setConfigurationByGateway(UUID gatewayId, String key, @Valid ConfigurationSetVO vo) {
		var tenant = security.getTenant();
		var gateway = getGateway(tenant, gatewayId);
		var definition = getConfigurationDefinition(tenant, key);
		var value = mapper.toString(definition, vo.getValue());
		gatewayConfigurationRepository.findByGatewayAndDefinition(gateway, definition).ifPresentOrElse(
				c -> gatewayConfigurationRepository.updateByGatewayAndDefinition(gateway.getId(), definition.getId(),
						value),
				() -> gatewayConfigurationRepository.save(new GatewayConfiguration()
						.setGateway(gateway)
						.setDefinition(definition)
						.setValue(value)));
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> resetConfiguration(String key) {
		var tenant = security.getTenant();
		var definition = getConfigurationDefinition(tenant, key);
		var configuration = tenantConfigurationRepository.findByDefinition(definition);
		if (configuration.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Configuration not found.");
		}
		tenantConfigurationRepository.deleteByDefinition(definition);
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> resetConfigurationByGroup(UUID groupId, String key) {
		var tenant = security.getTenant();
		var group = getGroup(tenant, groupId);
		var definition = getConfigurationDefinition(tenant, key);
		var configuration = groupConfigurationRepository.findByGroupAndDefinition(group, definition);
		if (configuration.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Configuration not found.");
		}
		groupConfigurationRepository.deleteByGroupAndDefinition(group, definition);
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> resetConfigurationByGateway(UUID gatewayId, String key) {
		var tenant = security.getTenant();
		var gateway = getGateway(tenant, gatewayId);
		var definition = getConfigurationDefinition(tenant, key);
		var configuration = gatewayConfigurationRepository.findByGatewayAndDefinition(gateway, definition);
		if (configuration.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Configuration not found.");
		}
		gatewayConfigurationRepository.deleteByGatewayAndDefinition(gateway, definition);
		return HttpResponse.noContent();
	}

	private ConfigurationDefinition getConfigurationDefinition(Tenant tenant, String key) {
		var optional = definitionRepository.findByTenantAndKey(tenant, key);
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

	private Gateway getGateway(Tenant tenant, UUID gatewayId) {
		var optional = gatewayRepository.findByTenantAndGatewayId(tenant, gatewayId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}
		return optional.get();
	}
}
