package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface ThingTypesApi {
	java.lang.String PATH_CREATE_THING_TYPE = "/thing-types";
	java.lang.String PATH_DELETE_THING_TYPE = "/thing-types/{thing_type_id}";
	java.lang.String PATH_FIND_MEASURAND_TYPES = "/measurand-types";
	java.lang.String PATH_FIND_THING_TYPE = "/thing-types/{thing_type_id}";
	java.lang.String PATH_GET_THING_TYPES = "/thing-types";
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

	@io.micronaut.http.annotation.Get(PATH_FIND_MEASURAND_TYPES)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<MeasurandTypeVO>> findMeasurandTypes();

	@io.micronaut.http.annotation.Get(PATH_FIND_THING_TYPE)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingTypeVO> findThingType(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_type_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
			java.lang.String thingTypeId);

	@io.micronaut.http.annotation.Get(PATH_GET_THING_TYPES)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<ThingTypeVO>> getThingTypes();

	@io.micronaut.http.annotation.Patch(PATH_UPDATE_THING_TYPE)
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
