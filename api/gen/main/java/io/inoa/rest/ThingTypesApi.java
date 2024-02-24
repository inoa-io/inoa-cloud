package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface ThingTypesApi {
	java.lang.String PATH_CREATE_THING_TYPE = "/thing-types";
	java.lang.String PATH_DELETE_THING_TYPE = "/thing-types/{thing_type_id}";
	java.lang.String PATH_FIND_THING_TYPE = "/thing-types/{thing_type_id}";
	java.lang.String PATH_FIND_THING_TYPES = "/thing-types";
	java.lang.String PATH_UPDATE_THING_TYPE = "/thing-types/{thing_type_id}";

	@io.micronaut.http.annotation.Post(PATH_CREATE_THING_TYPE)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypeVO> createThingType(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@jakarta.validation.Valid
			ThingTypeCreateVO thingTypeCreateVO);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_THING_TYPE)
	io.micronaut.http.HttpResponse<Object> deleteThingType(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_type_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
			java.lang.String thingTypeId);

	@io.micronaut.http.annotation.Get(PATH_FIND_THING_TYPE)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypeVO> findThingType(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_type_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
			java.lang.String thingTypeId);

	@io.micronaut.http.annotation.Get(PATH_FIND_THING_TYPES)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypePageVO> findThingTypes(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "page")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Min(0) Integer> page,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "size")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(100) Integer> size,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "sort")
			java.util.Optional<java.util.List<java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z_]{2,10}(,(asc|desc|ASC|DESC))?$") String>> sort,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "name_filter")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Size(max = 100) String> nameFilter,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "reference_filter")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Size(max = 100) String> referenceFilter);

	@io.micronaut.http.annotation.Post(PATH_UPDATE_THING_TYPE)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypeVO> updateThingType(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_type_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
			java.lang.String thingTypeId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@jakarta.validation.Valid
			ThingTypeUpdateVO thingTypeUpdateVO);
}
