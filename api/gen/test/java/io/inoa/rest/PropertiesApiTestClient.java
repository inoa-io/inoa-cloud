package io.inoa.rest;

/** Test client for {@link PropertiesApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface PropertiesApiTestClient {

	@io.micronaut.http.annotation.Delete("/gateway/properties/{key}")
	io.micronaut.http.HttpResponse<?> deleteProperty(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "key")
			java.lang.String key);

	@io.micronaut.http.annotation.Get("/gateway/properties")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.Map<String, java.lang.String>> getProperties(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization);

	@io.micronaut.http.annotation.Put("/gateway/properties")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.Map<String, java.lang.String>> setProperties(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			java.util.Map<String, java.lang.String> requestBody);

	@io.micronaut.http.annotation.Put("/gateway/properties/{key}")
	@io.micronaut.http.annotation.Produces({ "text/plain" })
	io.micronaut.http.HttpResponse<?> setProperty(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "key")
			java.lang.String key,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			java.lang.String body);
}
