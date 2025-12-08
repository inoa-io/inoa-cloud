package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface RemoteApi {
	java.lang.String PATH_GET_CONFIG_FROM_GATEWAY = "/rpc/{gateway_id:20}/config";
	java.lang.String PATH_GET_THINGS_FROM_GATEWAY = "/rpc/{gateway_id:20}/things";
	java.lang.String PATH_REBOOT = "/rpc/{gateway_id:20}/reboot";
	java.lang.String PATH_SYNC_CONFIG_TO_GATEWAY = "/rpc/{gateway_id:20}/config";
	java.lang.String PATH_SYNC_THINGS_TO_GATEWAY = "/rpc/{gateway_id:20}/things";
	java.lang.String PATH_WINK = "/rpc/{gateway_id:20}/wink";

	@io.micronaut.http.annotation.Get(PATH_GET_CONFIG_FROM_GATEWAY)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.lang.Object> getConfigFromGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Get(PATH_GET_THINGS_FROM_GATEWAY)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.lang.Object> getThingsFromGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Post(PATH_REBOOT)
	io.micronaut.http.HttpResponse<Object> reboot(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Post(PATH_SYNC_CONFIG_TO_GATEWAY)
	io.micronaut.http.HttpResponse<Object> syncConfigToGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Post(PATH_SYNC_THINGS_TO_GATEWAY)
	io.micronaut.http.HttpResponse<Object> syncThingsToGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Post(PATH_WINK)
	io.micronaut.http.HttpResponse<Object> wink(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@jakarta.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);
}
