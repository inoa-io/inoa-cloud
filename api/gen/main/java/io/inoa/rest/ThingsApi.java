package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface ThingsApi {

	java.lang.String PATH_CREATE_THING = "/things";
	java.lang.String PATH_DELETE_THING = "/things/{thing_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";
	java.lang.String PATH_DOWNLOAD_CONFIG_TO_GATEWAY = "/gateways/{gateway_id:20}/things/json";
	java.lang.String PATH_DOWNLOAD_CONFIG_TO_GATEWAY_LEGACY = "/gateways/{gateway_id:20}/things/json-legacy";
	java.lang.String PATH_FIND_THING = "/things/{thing_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";
	java.lang.String PATH_FIND_THINGS = "/things";
	java.lang.String PATH_FIND_THINGS_BY_GATEWAY_ID = "/gateways/{gateway_id:20}/things";
	java.lang.String PATH_SYNC_CONFIG_TO_GATEWAY = "/gateways/{gateway_id:20}/things/upload";
	java.lang.String PATH_SYNC_CONFIG_TO_GATEWAY_SEQUENTIAL = "/gateways/{gateway_id:20}/things/upload-sequential";
	java.lang.String PATH_UPDATE_THING = "/things/{thing_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";

	@io.micronaut.http.annotation.Post(PATH_CREATE_THING)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> createThing(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			ThingCreateVO thingCreateVO);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_THING)
	io.micronaut.http.HttpResponse<Object> deleteThing(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId);

	@io.micronaut.http.annotation.Get(PATH_DOWNLOAD_CONFIG_TO_GATEWAY)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.lang.Object> downloadConfigToGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Get(PATH_DOWNLOAD_CONFIG_TO_GATEWAY_LEGACY)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.lang.Object> downloadConfigToGatewayLegacy(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Get(PATH_FIND_THING)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> findThing(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId);

	@io.micronaut.http.annotation.Get(PATH_FIND_THINGS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingPageVO> findThings(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "page")
			@javax.validation.constraints.Min(0)
			java.util.Optional<java.lang.Integer> page,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "size")
			@javax.validation.constraints.Min(1)
			@javax.validation.constraints.Max(100)
			java.util.Optional<java.lang.Integer> size,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue
			java.util.Optional<java.util.List<java.lang.String>> sort,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "filter")
			@javax.validation.constraints.Size(max = 10)
			java.util.Optional<java.lang.String> filter);

	@io.micronaut.http.annotation.Get(PATH_FIND_THINGS_BY_GATEWAY_ID)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingPageVO> findThingsByGatewayId(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "page")
			@javax.validation.constraints.Min(0)
			java.util.Optional<java.lang.Integer> page,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "size")
			@javax.validation.constraints.Min(1)
			@javax.validation.constraints.Max(100)
			java.util.Optional<java.lang.Integer> size,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue
			java.util.Optional<java.util.List<java.lang.String>> sort,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "name_filter")
			@javax.validation.constraints.Size(max = 100)
			java.util.Optional<java.lang.String> nameFilter,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "reference_filter")
			@javax.validation.constraints.Size(max = 100)
			java.util.Optional<java.lang.String> referenceFilter);

	@io.micronaut.http.annotation.Get(PATH_SYNC_CONFIG_TO_GATEWAY)
	io.micronaut.http.HttpResponse<Object> syncConfigToGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Get(PATH_SYNC_CONFIG_TO_GATEWAY_SEQUENTIAL)
	io.micronaut.http.HttpResponse<Object> syncConfigToGatewaySequential(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Put(PATH_UPDATE_THING)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> updateThing(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			ThingUpdateVO thingUpdateVO);
}
