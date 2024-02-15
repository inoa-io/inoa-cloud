package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface PropertiesApi {

	java.lang.String PATH_DELETE_PROPERTY = "/gateway/properties/{key}";
	java.lang.String PATH_GET_PROPERTIES = "/gateway/properties";
	java.lang.String PATH_SET_PROPERTIES = "/gateway/properties";
	java.lang.String PATH_SET_PROPERTY = "/gateway/properties/{key}";

	@io.micronaut.http.annotation.Delete(PATH_DELETE_PROPERTY)
	io.micronaut.http.HttpResponse<Object> deleteProperty(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "key")
			@javax.validation.constraints.Pattern(regexp = "^[a-z0-9_\\-\\.]{2,100}$")
			java.lang.String key);

	@io.micronaut.http.annotation.Get(PATH_GET_PROPERTIES)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.Map<String, java.lang.String>> getProperties();

	@io.micronaut.http.annotation.Put(PATH_SET_PROPERTIES)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.Map<String, java.lang.String>> setProperties(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			java.util.Map<String, java.lang.String> requestBody);

	@io.micronaut.http.annotation.Put(PATH_SET_PROPERTY)
	@io.micronaut.http.annotation.Consumes({ "text/plain" })
	io.micronaut.http.HttpResponse<Object> setProperty(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "key")
			@javax.validation.constraints.Pattern(regexp = "^[a-z0-9_\\-\\.]{2,100}$")
			java.lang.String key,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.constraints.Size(max = 1000)
			java.lang.String body);
}
