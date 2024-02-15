package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface ConfigurationApi {

	java.lang.String PATH_CREATE_CONFIGURATION_DEFINITION = "/configuration/definition/{configuration_key:48}";
	java.lang.String PATH_DELETE_CONFIGURATION_DEFINITION = "/configuration/definition/{configuration_key:48}";
	java.lang.String PATH_FIND_CONFIGURATION_DEFINITIONS = "/configuration/definition";
	java.lang.String PATH_FIND_CONFIGURATIONS = "/configuration";
	java.lang.String PATH_FIND_CONFIGURATIONS_BY_GATEWAY = "/gateways/{gateway_id:20}/configuration";
	java.lang.String PATH_FIND_CONFIGURATIONS_BY_GROUP = "/groups/{group_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}/configuration";
	java.lang.String PATH_RESET_CONFIGURATION = "/configuration/{configuration_key:48}";
	java.lang.String PATH_RESET_CONFIGURATION_BY_GATEWAY = "/gateways/{gateway_id:20}/configuration/{configuration_key:48}";
	java.lang.String PATH_RESET_CONFIGURATION_BY_GROUP = "/groups/{group_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}/configuration/{configuration_key:48}";
	java.lang.String PATH_SET_CONFIGURATION = "/configuration/{configuration_key:48}";
	java.lang.String PATH_SET_CONFIGURATION_BY_GATEWAY = "/gateways/{gateway_id:20}/configuration/{configuration_key:48}";
	java.lang.String PATH_SET_CONFIGURATION_BY_GROUP = "/groups/{group_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}/configuration/{configuration_key:48}";

	@io.micronaut.http.annotation.Post(PATH_CREATE_CONFIGURATION_DEFINITION)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<ConfigurationDefinitionVO> createConfigurationDefinition(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
			@javax.validation.constraints.Size(min = 3, max = 48)
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			ConfigurationDefinitionVO configurationDefinitionVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_CONFIGURATION_DEFINITION)
	io.micronaut.http.HttpResponse<Object> deleteConfigurationDefinition(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
			@javax.validation.constraints.Size(min = 3, max = 48)
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_CONFIGURATION_DEFINITIONS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<ConfigurationDefinitionVO>> findConfigurationDefinitions();

	@io.micronaut.http.annotation.Get(PATH_FIND_CONFIGURATIONS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<ConfigurationVO>> findConfigurations();

	@io.micronaut.http.annotation.Get(PATH_FIND_CONFIGURATIONS_BY_GATEWAY)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<ConfigurationVO>> findConfigurationsByGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId);

	@io.micronaut.http.annotation.Get(PATH_FIND_CONFIGURATIONS_BY_GROUP)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<ConfigurationVO>> findConfigurationsByGroup(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Delete(PATH_RESET_CONFIGURATION)
	io.micronaut.http.HttpResponse<Object> resetConfiguration(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
			@javax.validation.constraints.Size(min = 3, max = 48)
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Delete(PATH_RESET_CONFIGURATION_BY_GATEWAY)
	io.micronaut.http.HttpResponse<Object> resetConfigurationByGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
			@javax.validation.constraints.Size(min = 3, max = 48)
			java.lang.String configurationKey);

	@io.micronaut.http.annotation.Delete(PATH_RESET_CONFIGURATION_BY_GROUP)
	io.micronaut.http.HttpResponse<Object> resetConfigurationByGroup(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
			@javax.validation.constraints.Size(min = 3, max = 48)
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Put(PATH_SET_CONFIGURATION)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<Object> setConfiguration(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
			@javax.validation.constraints.Size(min = 3, max = 48)
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			ConfigurationSetVO configurationSetVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Put(PATH_SET_CONFIGURATION_BY_GATEWAY)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<Object> setConfigurationByGateway(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "gateway_id")
			@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
			@javax.validation.constraints.Size(min = 4, max = 20)
			java.lang.String gatewayId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
			@javax.validation.constraints.Size(min = 3, max = 48)
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			ConfigurationSetVO configurationSetVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Put(PATH_SET_CONFIGURATION_BY_GROUP)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<Object> setConfigurationByGroup(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "configuration_key")
			@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
			@javax.validation.constraints.Size(min = 3, max = 48)
			java.lang.String configurationKey,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			ConfigurationSetVO configurationSetVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);
}
