package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface ThingsApi {
	java.lang.String PATH_CREATE_THING = "/things";
	java.lang.String PATH_DELETE_THING = "/things/{thing_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";
	java.lang.String PATH_FIND_THING = "/things/{thing_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";
	java.lang.String PATH_FIND_THINGS_BY_GATEWAY_ID = "/gateways/{gateway_id:20}/things";
	java.lang.String PATH_SYNC_THINGS_TO_GATEWAY = "/gateways/{gateway_id:20}/things/upload";
	java.lang.String PATH_UPDATE_THING = "/things/{thing_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";

	@io.micronaut.http.annotation.Post(PATH_CREATE_THING)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> createThing(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@jakarta.validation.Valid
			ThingCreateVO thingCreateVO);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_THING)
	io.micronaut.http.HttpResponse<Object> deleteThing(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId);

	@io.micronaut.http.annotation.Get(PATH_FIND_THING)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> findThing(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId);

	@io.micronaut.http.annotation.Get(PATH_FIND_THINGS_BY_GATEWAY_ID)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<ThingVO>> findThingsByGatewayId(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Get(PATH_SYNC_THINGS_TO_GATEWAY)
	io.micronaut.http.HttpResponse<Object> syncThingsToGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Patch(PATH_UPDATE_THING)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> updateThing(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@jakarta.validation.Valid
			ThingUpdateVO thingUpdateVO);
}
