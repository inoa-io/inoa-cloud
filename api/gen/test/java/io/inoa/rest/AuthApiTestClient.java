package io.inoa.rest;

/** Test client for {@link AuthApi}. **/
@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client("${micronaut.http.services.test.clientId:/}")
public interface AuthApiTestClient {

	@io.micronaut.http.annotation.Get("/whoami")
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<UserVO> whoami(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Header(io.micronaut.http.HttpHeaders.AUTHORIZATION)
			java.lang.String authorization);
}
