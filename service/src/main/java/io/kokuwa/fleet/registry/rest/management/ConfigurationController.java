package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.Objects;

import io.kokuwa.fleet.registry.domain.ConfigurationDefinitionRepository;
import io.kokuwa.fleet.registry.rest.mapper.ConfigurationMapper;
import io.kokuwa.fleet.registry.rest.validation.ConfigurationDefinitionValid;
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

	private final SecurityManagement security;
	private final ConfigurationMapper mapper;
	private final ConfigurationDefinitionRepository definitionRepository;

	@Override
	public HttpResponse<List<ConfigurationDefinitionVO>> findConfigurationDefinitions() {
		return HttpResponse.ok(mapper.toDefinitions(definitionRepository.findByTenantOrderByKey(security.getTenant())));
	}

	@Override
	public HttpResponse<ConfigurationDefinitionVO> createConfigurationDefinition(String key,
			@ConfigurationDefinitionValid ConfigurationDefinitionVO vo) {
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
}
