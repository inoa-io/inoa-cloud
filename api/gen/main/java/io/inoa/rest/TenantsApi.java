package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface TenantsApi {

	java.lang.String PATH_CREATE_TENANT = "/tenants";
	java.lang.String PATH_DELETE_TENANT = "/tenants/{tenant_id:30}";
	java.lang.String PATH_FIND_TENANT = "/tenants/{tenant_id:30}";
	java.lang.String PATH_FIND_TENANTS = "/tenants";
	java.lang.String PATH_UPDATE_TENANT = "/tenants/{tenant_id:30}";

	@io.micronaut.http.annotation.Post(PATH_CREATE_TENANT)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<TenantVO> createTenant(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			TenantCreateVO tenantCreateVO);

	@io.micronaut.http.annotation.Delete(PATH_DELETE_TENANT)
	io.micronaut.http.HttpResponse<Object> deleteTenant(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "tenant_id")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.lang.String tenantId);

	@io.micronaut.http.annotation.Get(PATH_FIND_TENANT)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<TenantVO> findTenant(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "tenant_id")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.lang.String tenantId);

	@io.micronaut.http.annotation.Get(PATH_FIND_TENANTS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<TenantVO>> findTenants();

	@io.micronaut.http.annotation.Patch(PATH_UPDATE_TENANT)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<TenantVO> updateTenant(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "tenant_id")
			@javax.validation.constraints.Size(min = 1, max = 30)
			java.lang.String tenantId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			@javax.validation.Valid
			TenantUpdateVO tenantUpdateVO);
}
