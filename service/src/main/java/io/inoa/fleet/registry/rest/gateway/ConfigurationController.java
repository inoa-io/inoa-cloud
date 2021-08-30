package io.inoa.fleet.registry.rest.gateway;

import java.util.Map;

import io.inoa.fleet.registry.rest.mapper.ConfigurationMapper;
import io.inoa.fleet.registry.service.ConfigurationService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link ConfigurationApi}.
 *
 * @author Stephan Schnabel
 */
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller
@RequiredArgsConstructor
public class ConfigurationController implements ConfigurationApi {

	private final Security security;
	private final ConfigurationService service;
	private final ConfigurationMapper mapper;

	@Override
	public HttpResponse<Map<String, Object>> getConfiguration() {
		return HttpResponse.ok(mapper.toConfigurationMap(service.findByGateway(security.getGateway())));
	}
}
