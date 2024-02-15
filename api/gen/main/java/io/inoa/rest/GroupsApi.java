package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface GroupsApi {

	java.lang.String PATH_CREATE_GROUP = "/groups";
	java.lang.String PATH_DELETE_GROUP = "/groups/{group_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";
	java.lang.String PATH_FIND_GROUP = "/groups/{group_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";
	java.lang.String PATH_FIND_GROUPS = "/groups";
	java.lang.String PATH_UPDATE_GROUP = "/groups/{group_id:[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9]-[a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9][a-f0-9]}";

	@io.micronaut.http.annotation.Post(PATH_CREATE_GROUP)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GroupVO> createGroup(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			GroupCreateVO groupCreateVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_GROUP)
	io.micronaut.http.HttpResponse<Object> deleteGroup(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_GROUP)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GroupVO> findGroup(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);

	@io.micronaut.http.annotation.Get(PATH_FIND_GROUPS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<GroupVO>> findGroups();

	@io.micronaut.http.annotation.Patch(PATH_UPDATE_GROUP)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<GroupVO> updateGroup(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "group_id")
			java.util.UUID groupId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			GroupUpdateVO groupUpdateVO,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.QueryValue(value = "tenant_specification")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.util.Optional<java.lang.String> tenantSpecification);
}
