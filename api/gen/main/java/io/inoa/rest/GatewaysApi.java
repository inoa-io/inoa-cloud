package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface GatewaysApi {

	java.lang.String PATH_CREATE_GATEWAY = "/gateways";
	java.lang.String PATH_DELETE_GATEWAY = "/gateways/{gateway_id:20}";
	java.lang.String PATH_FIND_GATEWAY = "/gateways/{gateway_id:20}";
	java.lang.String PATH_FIND_GATEWAYS = "/gateways";
	java.lang.String PATH_MOVE_GATEWAY = "/gateways";
	java.lang.String PATH_UPDATE_GATEWAY = "/gateways/{gateway_id:20}";

	@io.micronaut.http.annotation.Post(PATH_CREATE_GATEWAY)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayDetailVO> createGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			GatewayCreateVO gatewayCreateVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_GATEWAY)
	io.micronaut.http.HttpResponse<Object> deleteGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_GATEWAY)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayDetailVO> findGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_GATEWAYS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayPageVO> findGateways(
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
			@javax.validation.constraints.Size(max = 100)
			java.util.Optional<java.lang.String> filter);

	@io.micronaut.http.annotation.Patch(PATH_MOVE_GATEWAY)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<Object> moveGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			MoveGatewayRequestVO moveGatewayRequestVO);

	@io.micronaut.http.annotation.Patch(PATH_UPDATE_GATEWAY)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayDetailVO> updateGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			GatewayUpdateVO gatewayUpdateVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);
}
