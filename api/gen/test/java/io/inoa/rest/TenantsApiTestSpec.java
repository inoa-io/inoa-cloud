package io.inoa.rest;

/** Test for {@link TenantsApi}. */
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
public interface TenantsApiTestSpec { 

	// createTenant

	void createTenant201() throws java.lang.Exception;

	void createTenant400() throws java.lang.Exception;

	void createTenant401() throws java.lang.Exception;

	void createTenant409() throws java.lang.Exception;

	// deleteTenant

	void deleteTenant204() throws java.lang.Exception;

	void deleteTenant400() throws java.lang.Exception;

	void deleteTenant401() throws java.lang.Exception;

	void deleteTenant404() throws java.lang.Exception;

	// findTenant

	void findTenant200() throws java.lang.Exception;

	void findTenant401() throws java.lang.Exception;

	void findTenant404() throws java.lang.Exception;

	// findTenants

	void findTenants200() throws java.lang.Exception;

	void findTenants401() throws java.lang.Exception;

	// updateTenant

	void updateTenant200() throws java.lang.Exception;

	void updateTenant400() throws java.lang.Exception;

	void updateTenant401() throws java.lang.Exception;

	void updateTenant404() throws java.lang.Exception;
}
