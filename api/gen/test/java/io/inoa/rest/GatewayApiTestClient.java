package io.inoa.rest;

/** Test client for {@link GatewayApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface GatewayApiTestClient {

	@io.micronaut.http.annotation.Get("/gateway/configuration")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.Map<String, java.lang.Object>> getConfiguration(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization);

	@io.micronaut.http.annotation.Post("/gateway")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<?> register(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			RegisterVO registerVO);
}
