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
			@jakarta.validation.Valid
			GatewayCreateVO gatewayCreateVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Size(min = 1, max = 30)String> tenantSpecification);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_GATEWAY)
	io.micronaut.http.HttpResponse<Object> deleteGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Size(min = 1, max = 30)String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_GATEWAY)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayDetailVO> findGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Size(min = 1, max = 30)String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_GATEWAYS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayPageVO> findGateways(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "page")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Min(0)Integer> page,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "size")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Min(1)@jakarta.validation.constraints.Max(100)Integer> size,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue
			java.util.Optional<java.util.List<java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z_]{2,10}(,(asc|desc|ASC|DESC))?$") String>> sort,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "filter")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Size(max = 100)String> filter);

	@io.micronaut.http.annotation.Patch(PATH_MOVE_GATEWAY)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<Object> moveGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@jakarta.validation.Valid
			MoveGatewayRequestVO moveGatewayRequestVO);

	@io.micronaut.http.annotation.Patch(PATH_UPDATE_GATEWAY)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GatewayDetailVO> updateGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@jakarta.validation.Valid
			GatewayUpdateVO gatewayUpdateVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.util.Optional<java.lang.@jakarta.validation.constraints.Size(min = 1, max = 30)String> tenantSpecification);
}
