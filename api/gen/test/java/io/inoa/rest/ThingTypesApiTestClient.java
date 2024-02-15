package io.inoa.rest;

/** Test client for {@link ThingTypesApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface ThingTypesApiTestClient {

	@io.micronaut.http.annotation.Post("/thing-types")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypeVO> createThingType(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			ThingTypeCreateVO thingTypeCreateVO);

	@io.micronaut.http.annotation.Delete("/thing-types/{thing_type_id}")
	io.micronaut.http.HttpResponse<?> deleteThingType(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_type_id")
			java.lang.String thingTypeId);

	@io.micronaut.http.annotation.Get("/thing-types/{thing_type_id}")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypeVO> findThingType(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_type_id")
			java.lang.String thingTypeId);

	@io.micronaut.http.annotation.Get("/thing-types?{&sort*}")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypePageVO> findThingTypes(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "page")
			java.lang.Integer page,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "size")
			java.lang.Integer size,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "sort")
			java.util.List<java.lang.String> sort,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "name_filter")
			java.lang.String nameFilter,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "reference_filter")
			java.lang.String referenceFilter);

	@io.micronaut.http.annotation.Post("/thing-types/{thing_type_id}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypeVO> updateThingType(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_type_id")
			java.lang.String thingTypeId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			ThingTypeUpdateVO thingTypeUpdateVO);
}
