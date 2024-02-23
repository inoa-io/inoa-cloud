package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface GatewayApi {
	java.lang.String PATH_GET_CONFIGURATION = "/gateway/configuration";
	java.lang.String PATH_REGISTER = "/gateway";

	@io.micronaut.http.annotation.Get(PATH_GET_CONFIGURATION)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.Map<String, java.lang.Object>> getConfiguration();

	@io.micronaut.http.annotation.Post(PATH_REGISTER)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<Object> register(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@jakarta.validation.Valid
			RegisterVO registerVO);
}
