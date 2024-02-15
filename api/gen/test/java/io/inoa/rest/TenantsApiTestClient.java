package io.inoa.rest;

/** Test client for {@link TenantsApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface TenantsApiTestClient {

	@io.micronaut.http.annotation.Post("/tenants")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<TenantVO> createTenant(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			TenantCreateVO tenantCreateVO);

	@io.micronaut.http.annotation.Delete("/tenants/{tenant_id}")
	io.micronaut.http.HttpResponse<?> deleteTenant(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "tenant_id")
			java.lang.String tenantId);

	@io.micronaut.http.annotation.Get("/tenants/{tenant_id}")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<TenantVO> findTenant(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "tenant_id")
			java.lang.String tenantId);

	@io.micronaut.http.annotation.Get("/tenants")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<TenantVO>> findTenants(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization);

	@io.micronaut.http.annotation.Patch("/tenants/{tenant_id}")
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<TenantVO> updateTenant(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "tenant_id")
			java.lang.String tenantId,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			TenantUpdateVO tenantUpdateVO);
}
