package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface CredentialsApi {

	java.lang.String PATH_CREATE_CREDENTIAL = "/gateways/{gateway_id:20}/credentials";
	java.lang.String PATH_DELETE_CREDENTIAL = "/gateways/{gateway_id:20}/credentials/{credential_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";
	java.lang.String PATH_FIND_CREDENTIAL = "/gateways/{gateway_id:20}/credentials/{credential_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";
	java.lang.String PATH_FIND_CREDENTIALS = "/gateways/{gateway_id:20}/credentials";
	java.lang.String PATH_UPDATE_CREDENTIAL = "/gateways/{gateway_id:20}/credentials/{credential_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";

	@io.micronaut.http.annotation.Post(PATH_CREATE_CREDENTIAL)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<CredentialVO> createCredential(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			CredentialCreateVO credentialCreateVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_CREDENTIAL)
	io.micronaut.http.HttpResponse<Object> deleteCredential(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "credential_id")
			java.util.UUID credentialId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_CREDENTIAL)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<CredentialVO> findCredential(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "credential_id")
			java.util.UUID credentialId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_CREDENTIALS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<CredentialVO>> findCredentials(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Patch(PATH_UPDATE_CREDENTIAL)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<CredentialVO> updateCredential(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "credential_id")
			java.util.UUID credentialId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			CredentialUpdateVO credentialUpdateVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);
}
