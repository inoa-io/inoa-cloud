package io.inoa.rest;

/** Test client for {@link RemoteApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface RemoteApiTestClient {

	@io.micronaut.http.annotation.Post("/gateways/{gateway_id}/rpc")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<RpcResponseVO> sendRpcCommand(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			RpcCommandVO rpcCommandVO);
}
