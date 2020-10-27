package io.kokuwa.fleet.registry.rest;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.kokuwa.fleet.registry.auth.GatewayAuthentication;
import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.domain.GatewayPropertyRepository;
import io.kokuwa.fleet.registry.rest.gateway.AuthApi;
import io.kokuwa.fleet.registry.rest.gateway.PropertiesApi;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.context.ServerRequestContext;
import io.reactivex.Completable;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link AuthApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class PropertiesController implements PropertiesApi {

	private final GatewayPropertyRepository propertyRepository;

	@Override
	public Single<HttpResponse<Map<String, String>>> getProperties() {
		return Single.just(HttpResponse.ok(toMap(getGateway())));
	}

	@Override
	public Single<HttpResponse<Map<String, String>>> setProperties(Map<String, String> body) {
		Gateway gateway = getGateway();
		return Completable
				.merge(body.entrySet().stream()
						.flatMap(e -> updatedProperty(gateway, e.getKey(), e.getValue()).stream())
						.map(propertyRepository::store)
						.collect(Collectors.toSet()))
				.toSingle(() -> HttpResponse.ok(toMap(gateway)));
	}

	@Override
	public Single<HttpResponse<Object>> setProperty(String key, String newValue) {
		Gateway gateway = getGateway();
		Optional<GatewayProperty> updatedProperty = updatedProperty(gateway, key, newValue);
		if (updatedProperty.isEmpty()) {
			return Single.just(HttpResponse.noContent());
		}
		return propertyRepository.store(updatedProperty.get()).toSingle(HttpResponse::noContent);
	}

	@Override
	public Single<HttpResponse<Object>> deleteProperty(String key) {
		Gateway gateway = getGateway();

		Optional<GatewayProperty> optionalProperty = gateway.getProperties().stream()
				.filter(property -> property.getKey().equals(key))
				.findAny();
		if (optionalProperty.isEmpty()) {
			log.trace("Property {} not found.", key);
			return Single.just(HttpResponse.notFound());
		}

		GatewayProperty property = optionalProperty.get();
		log.trace("Property {} with value {} deleted.", key, property.getValue());
		return propertyRepository.deleteById(property.getId()).toSingleDefault(HttpResponse.noContent());
	}

	// internal

	private Optional<GatewayProperty> updatedProperty(Gateway gateway, String key, String newValue) {

		Set<GatewayProperty> properties = gateway.getProperties();
		Optional<GatewayProperty> optionalProperty = properties.stream()
				.filter(property -> property.getKey().equals(key))
				.findAny();
		if (optionalProperty.isEmpty()) {
			log.debug("Property {} set to {}.", key, newValue);
			GatewayProperty property = new GatewayProperty()
					.setGateway((Gateway) new Gateway().setId(gateway.getId()))
					.setKey(key)
					.setValue(newValue);
			properties.add(property);
			return Optional.of(property);
		}

		String oldValue = optionalProperty.get().getValue();
		if (!Objects.equals(oldValue, newValue)) {
			log.debug("Property {} set from {} to {}.", key, oldValue, newValue);
			return optionalProperty.map(property -> property.setValue(newValue));
		}

		log.trace("Property {} not set to {}.", key, newValue);
		return Optional.empty();
	}

	private Map<String, String> toMap(Gateway gateway) {
		return gateway.getProperties().stream().collect(
				Collectors.toMap(GatewayProperty::getKey, GatewayProperty::getValue, (k1, k2) -> k1, TreeMap::new));
	}

	private Gateway getGateway() {
		return ServerRequestContext
				.currentRequest().get()
				.getUserPrincipal(GatewayAuthentication.class).get()
				.getGateway();
	}
}
