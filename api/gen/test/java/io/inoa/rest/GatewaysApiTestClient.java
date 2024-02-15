package io.inoa.rest;

/** Test client for {@link GatewaysApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface GatewaysApiTestClient {

	@io.micronaut.http.annotation.Post("/gateways")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayDetailVO> createGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			GatewayCreateVO gatewayCreateVO,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Delete("/gateways/{gateway_id}")
	io.micronaut.http.HttpResponse<?> deleteGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Get("/gateways/{gateway_id}")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayDetailVO> findGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Get("/gateways?{&sort*}")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayPageVO> findGateways(
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
			@io.micronaut.http.annotation.QueryValue(value = "filter")
			java.lang.String filter);

	@io.micronaut.http.annotation.Patch("/gateways")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<?> moveGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			MoveGatewayRequestVO moveGatewayRequestVO);

	@io.micronaut.http.annotation.Patch("/gateways/{gateway_id}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayDetailVO> updateGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			GatewayUpdateVO gatewayUpdateVO,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);
}
