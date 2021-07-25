package io.kokuwa.fleet.registry.rest.gateway;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.kokuwa.fleet.registry.auth.GatewayAuthentication;
import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.domain.GatewayPropertyRepository;
import io.kokuwa.fleet.registry.rest.mapper.GatewayMapper;
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

	private final GatewayMapper mapper;
	private final GatewayPropertyRepository repository;

	@Override
	public HttpResponse<Map<String, String>> getProperties() {
		return HttpResponse.ok(mapper.toMap(repository.findByGateway(getGateway())));
	}

	@Override
	public HttpResponse<Map<String, String>> setProperties(Map<String, String> body) {
		var gateway = getGateway();
		var properties = repository.findByGateway(getGateway());
		return HttpResponse.ok(mapper.toMap(Stream
				.concat(
						properties.stream().filter(property -> !body.containsKey(property.getKey())),
						body.entrySet().stream()
								.map(entry -> updatedProperty(gateway, properties, entry.getKey(), entry.getValue())))
				.collect(Collectors.toList())));
	}

	@Override
	public HttpResponse<Object> setProperty(String key, String newValue) {
		var gateway = getGateway();
		var properties = repository.findByGatewayAndKey(gateway, key).stream().collect(Collectors.toList());
		updatedProperty(gateway, properties, key, newValue);
		return HttpResponse.noContent();
	}

	@Override
	public HttpResponse<Object> deleteProperty(String key) {
		var gateway = getGateway();
		var optionalProperty = repository.findByGatewayAndKey(gateway, key);
		if (optionalProperty.isEmpty()) {
			log.trace("Property {} not found.", key);
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Property not found.");
		}
		log.trace("Property {} with value {} deleted.", key, optionalProperty.get().getValue());
		repository.deleteByGatewayAndKey(gateway, key);
		return HttpResponse.noContent();
	}

	// internal

	private GatewayProperty updatedProperty(
			Gateway gateway,
			List<GatewayProperty> properties,
			String key,
			String newValue) {

		var optionalProperty = properties.stream()
				.filter(property -> property.getKey().equals(key))
				.findAny();
		if (optionalProperty.isEmpty()) {
			log.debug("Property {} set to {}.", key, newValue);
			var property = new GatewayProperty().setGateway(gateway).setKey(key).setValue(newValue);
			properties.add(property);
			return repository.save(property);
		}

		var property = optionalProperty.get();
		if (!Objects.equals(property.getValue(), newValue)) {
			log.debug("Property {} set from {} to {}.", key, property.getValue(), newValue);
			repository.update(gateway.getId(), key, newValue);
			return property.setValue(newValue);
		}

		log.trace("Property {} not updated with value {}.", key, newValue);
		return property;
	}

	private Gateway getGateway() {
		return ServerRequestContext
				.currentRequest().get()
				.getUserPrincipal(GatewayAuthentication.class).get()
				.getGateway();
	}
}
