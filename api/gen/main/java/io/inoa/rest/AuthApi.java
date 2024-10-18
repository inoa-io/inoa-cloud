package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.validation.Validated
public interface AuthApi {
	java.lang.String PATH_WHOAMI = "/whoami";

	@io.micronaut.http.annotation.Get(PATH_WHOAMI)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	io.micronaut.http.HttpResponse<UserVO> whoami();
}
