package io.kokuwa.fleet.registry.rest.gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.kokuwa.fleet.registry.auth.GatewayAuthentication;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.domain.GatewayProperty.GatewayPropertyPK;
import io.kokuwa.fleet.registry.domain.GatewayPropertyRepository;
import io.kokuwa.fleet.registry.rest.RestMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link AuthApi}.
 *
 * @author Stephan Schnabel
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
@Slf4j
@RequiredArgsConstructor
public class PropertiesController implements PropertiesApi {

	private final RestMapper mapper;
	private final GatewayPropertyRepository repository;

	@Override
	public HttpResponse<Map<String, String>> getProperties() {
		return HttpResponse.ok(mapper.toMap(repository.findByGatewayId(getGatewayId())));
	}

	@Override
	public HttpResponse<Map<String, String>> setProperties(Map<String, String> body) {
		var gatewayId = getGatewayId();
		List<GatewayProperty> properties = repository.findByGatewayId(getGatewayId());
		// not updated properties
		List<GatewayProperty> notUpdated = properties.stream()
				.filter(property -> !body.containsKey(property.getPk().getKey())).collect(Collectors.toList());
		List<GatewayProperty> collect = body.entrySet().stream()
				.map(entry -> updatedProperty(gatewayId, properties, entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
		return HttpResponse.ok(mapper.toMap(properties));
	}

	@Override
	public HttpResponse<Object> setProperty(String key, String newValue) {
		var gatewayId = getGatewayId();
		Optional<GatewayProperty> optionalProperty = repository.findByGatewayIdAndKey(getGatewayId(), key);
		updatedProperty(gatewayId, optionalProperty.isEmpty() ? new ArrayList<>() : List.of(optionalProperty.get()),
				key, newValue);
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> deleteProperty(String key) {
		Optional<GatewayProperty> optionalProperty = repository.findByGatewayIdAndKey(getGatewayId(), key);
		if (optionalProperty.isEmpty()) {
			log.trace("Property {} not found.", key);
			throw new HttpStatusException(HttpStatus.NOT_FOUND, null);
		}
		log.trace("Property {} with value {} deleted.", key, optionalProperty.get().getValue());
		repository.deleteById(optionalProperty.get().getPk());
		return HttpResponse.noContent();
	}

	// internal
	private GatewayProperty updatedProperty(Long gatewayId, List<GatewayProperty> properties, String key,
			String newValue) {

		GatewayPropertyPK pk = new GatewayPropertyPK(gatewayId, key);
		Optional<GatewayProperty> optionalProperty = properties.stream()
				.filter(property -> property.getPk().getKey().equals(key)).findAny();
		if (optionalProperty.isEmpty()) {
			log.debug("Property {} set to {}.", key, newValue);
			GatewayProperty property = new GatewayProperty().setPk(pk).setValue(newValue);
			properties.add(property);
			return repository.save(property);
		}

		GatewayProperty property = optionalProperty.get();
		if (!Objects.equals(property.getValue(), newValue)) {
			log.debug("Property {} set from {} to {}.", key, property.getValue(), newValue);

			return repository.update(property.setValue(newValue));
		}

		log.trace("Property {} not updated with value {}.", key, newValue);
		return property;
	}

	private Long getGatewayId() {
		return ServerRequestContext.currentRequest().get().getUserPrincipal(GatewayAuthentication.class).get()
				.getGateway().getId();
	}
}
