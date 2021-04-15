package io.kokuwa.fleet.registry.rest.gateway;

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
import io.reactivex.Flowable;
import io.reactivex.Single;
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
	public Single<HttpResponse<Map<String, String>>> getProperties() {
		return repository.findByGatewayId(getGatewayId()).toList().map(mapper::toMap).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<Map<String, String>>> setProperties(Map<String, String> body) {
		var gatewayId = getGatewayId();
		return repository.findByGatewayId(getGatewayId()).toList()
				.flatMap(properties -> Flowable.merge(
						// not updated properties
						Flowable.fromIterable(properties.stream()
								.filter(property -> !body.containsKey(property.getPk().getKey()))
								.collect(Collectors.toList())),
						// created or updated properties
						Single.concat(body.entrySet().stream()
								.map(entry -> updatedProperty(gatewayId, properties, entry.getKey(), entry.getValue()))
								.collect(Collectors.toList())))
						.toList())
				.map(properties -> HttpResponse.ok(mapper.toMap(properties)));
	}

	@Override
	public Single<HttpResponse<Object>> setProperty(String key, String newValue) {
		var gatewayId = getGatewayId();
		return repository.findByGatewayIdAndKey(getGatewayId(), key).toFlowable().toList()
				.flatMap(properties -> updatedProperty(gatewayId, properties, key, newValue))
				.ignoreElement().toSingle(HttpResponse::noContent);
	}

	@Override
	public Single<HttpResponse<Object>> deleteProperty(String key) {
		return repository.findByGatewayIdAndKey(getGatewayId(), key)
				.doOnComplete(() -> {
					log.trace("Property {} not found.", key);
					throw new HttpStatusException(HttpStatus.NOT_FOUND, null);
				})
				.flatMapSingle(property -> {
					log.trace("Property {} with value {} deleted.", key, property.getValue());
					return repository.deleteById(property.getPk()).toSingle(HttpResponse::noContent);
				});
	}

	// internal

	private Single<GatewayProperty> updatedProperty(
			Long gatewayId,
			List<GatewayProperty> properties,
			String key,
			String newValue) {

		GatewayPropertyPK pk = new GatewayPropertyPK(gatewayId, key);
		Optional<GatewayProperty> optionalProperty = properties.stream()
				.filter(property -> property.getPk().getKey().equals(key))
				.findAny();
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
		return Single.just(property);
	}

	private Long getGatewayId() {
		return ServerRequestContext
				.currentRequest().get()
				.getUserPrincipal(GatewayAuthentication.class).get()
				.getGateway()
				.getId();
	}
}
