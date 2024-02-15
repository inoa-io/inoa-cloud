package io.inoa.rest;

/** Test client for {@link CredentialsApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface CredentialsApiTestClient {

	@io.micronaut.http.annotation.Post("/gateways/{gateway_id}/credentials")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<CredentialVO> createCredential(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			CredentialCreateVO credentialCreateVO,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Delete("/gateways/{gateway_id}/credentials/{credential_id}")
	io.micronaut.http.HttpResponse<?> deleteCredential(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "credential_id")
			java.util.UUID credentialId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Get("/gateways/{gateway_id}/credentials/{credential_id}")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<CredentialVO> findCredential(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "credential_id")
			java.util.UUID credentialId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Get("/gateways/{gateway_id}/credentials")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<CredentialVO>> findCredentials(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Patch("/gateways/{gateway_id}/credentials/{credential_id}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<CredentialVO> updateCredential(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "credential_id")
			java.util.UUID credentialId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			CredentialUpdateVO credentialUpdateVO,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);
}
