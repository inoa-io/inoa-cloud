package io.inoa.fleet.registry.test;

import io.inoa.fleet.registry.rest.gateway.TokenResponseVO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.annotation.Client;

/**
 * Because client/server semantic differs.
 *
 * @author Stephan Schnabel
 * @see "https://github.com/kokuwaio/micronaut-openapi-codegen/issues/79"
 */
@Client(id = "gateway-registry")
public interface AuthApiFixedClient {

	@Get("/auth/keys")
	@Produces("application/json")
	HttpResponse<Object> getKeys();

	@Post("/auth/token")
	@Produces("application/x-www-form-urlencoded")
	@Consumes("application/json")
	HttpResponse<TokenResponseVO> getToken(
			@Part(value = "grant_type") String grantType,
			@Part(value = "assertion") String assertion);
}
