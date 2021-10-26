package io.inoa.fleet.auth.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.inoa.fleet.auth.JwtService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/protocol/openid-connect")
@RequiredArgsConstructor
public class CertController {

	private final JwtService jwtService;

	@Get("/certs")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse<CertResult> certs() {
		var result = new CertResult();
		result.getKeys().add(jwtService.getJwk().toJSONObject());
		return HttpResponse.ok(result);
	}

	@Data
	private static class CertResult {
		List<Map<String, Object>> keys = new ArrayList<>();
	}
}
