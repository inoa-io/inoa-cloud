package io.inoa.rest;

/** Test client for {@link ConfigurationApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface ConfigurationApiTestClient {

	@io.micronaut.http.annotation.Post("/configuration/definition/{configuration_key}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ConfigurationDefinitionVO> createConfigurationDefinition(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			ConfigurationDefinitionVO configurationDefinitionVO,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Delete("/configuration/definition/{configuration_key}")
	io.micronaut.http.HttpResponse<?> deleteConfigurationDefinition(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Get("/configuration/definition")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<@javax.validation.constraints.NotNull @javax.validation.Valid ConfigurationDefinitionVO>> findConfigurationDefinitions(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization);

	@io.micronaut.http.annotation.Get("/configuration")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<@javax.validation.constraints.NotNull @javax.validation.Valid ConfigurationVO>> findConfigurations(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization);

	@io.micronaut.http.annotation.Get("/gateways/{gateway_id}/configuration")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<@javax.validation.constraints.NotNull @javax.validation.Valid ConfigurationVO>> findConfigurationsByGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Get("/groups/{group_id}/configuration")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<@javax.validation.constraints.NotNull @javax.validation.Valid ConfigurationVO>> findConfigurationsByGroup(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Delete("/configuration/{configuration_key}")
	io.micronaut.http.HttpResponse<?> resetConfiguration(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Delete("/gateways/{gateway_id}/configuration/{configuration_key}")
	io.micronaut.http.HttpResponse<?> resetConfigurationByGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			java.lang.String configurationKey);

	@io.micronaut.http.annotation.Delete("/groups/{group_id}/configuration/{configuration_key}")
	io.micronaut.http.HttpResponse<?> resetConfigurationByGroup(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Put("/configuration/{configuration_key}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<?> setConfiguration(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			ConfigurationSetVO configurationSetVO,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Put("/gateways/{gateway_id}/configuration/{configuration_key}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<?> setConfigurationByGateway(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			ConfigurationSetVO configurationSetVO,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);

	@io.micronaut.http.annotation.Put("/groups/{group_id}/configuration/{configuration_key}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<?> setConfigurationByGroup(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			ConfigurationSetVO configurationSetVO,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			java.lang.String tenantSpecification);
}
