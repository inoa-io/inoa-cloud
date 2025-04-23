package io.inoa.rest;

/** Test client for {@link ThingsApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface ThingsApiTestClient {

	@io.micronaut.http.annotation.Post("/things")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> createThing(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			ThingCreateVO thingCreateVO);

	@io.micronaut.http.annotation.Delete("/things/{thing_id}")
	io.micronaut.http.HttpResponse<?> deleteThing(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId);

	@io.micronaut.http.annotation.Get("/things/{thing_id}")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> findThing(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId);

	@io.micronaut.http.annotation.Get("/gateways/{gateway_id}/things")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<ThingVO>> findThingsByGatewayId(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Get("/gateways/{gateway_id}/things/upload")
	io.micronaut.http.HttpResponse<?> syncThingsToGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Patch("/things/{thing_id}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ThingVO> updateThing(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "thing_id")
			java.util.UUID thingId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			ThingUpdateVO thingUpdateVO);
}
