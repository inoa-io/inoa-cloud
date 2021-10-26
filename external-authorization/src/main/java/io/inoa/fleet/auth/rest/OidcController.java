package io.inoa.fleet.auth.rest;

import io.inoa.fleet.auth.InoaAuthProperties;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller
@RequiredArgsConstructor
public class OidcController {

	private final InoaAuthProperties properties;

	@Get("/.well-known/openid-configuration")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse<OpenidConfigurationVO> getOpenidConfiguration() {
		var config = new OpenidConfigurationVO();
		config.setIssuer(properties.getIssuer());
		config.setJwksUri(String.format("%s/protocol/openid-connect/certs", properties.getIssuer()));
		config.setTokenEndpoint(String.format("%s/protocol/openid-connect/token", properties.getIssuer()));
		return HttpResponse.ok(config);
	}
}
