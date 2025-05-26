package io.inoa.fleet.registry.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.io.IOUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.runtime.context.scope.Refreshable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Publish spec with patched server url.
 *
 * @author Stephan Schnabel
 */
@Requires(property = "registry.openapi.enabled", notEquals = StringUtils.FALSE)
@Controller
@Refreshable
@Slf4j
@RequiredArgsConstructor
public class OpenApiController {

	private final Environment env;

	@Secured(SecurityRule.IS_ANONYMOUS)
	@Get("/openapi/{spec:gateway|management}.yaml")
	@Produces(MediaType.APPLICATION_YAML)
	String management(@PathVariable String spec) {

		String specString = null;
		String specResourceName = "/openapi/" + spec + ".yaml";
		try (var reader = new InputStreamReader(OpenApiController.class.getResourceAsStream(specResourceName))) {
			specString = IOUtils.readText(new BufferedReader(reader));
		} catch (IOException e) {
			log.error("Failed to serve openapi spec.", e);
			throw new HttpStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse spec " + spec + ".");
		}

		Optional<String> contextPath = env.get("micronaut.server.context-path", String.class);
		Optional<String> openapiUrl = env.get("registry.openapi.url", String.class);
		Optional<String> url = openapiUrl.or(() -> contextPath);
		if (url.isPresent()) {
			// TODO OpenAPI: parse spec with SnakeYAML and operate on object model
			specString = specString.replace("servers: []", "servers:\n- url: " + url.get());
		}

		return specString;
	}
}
