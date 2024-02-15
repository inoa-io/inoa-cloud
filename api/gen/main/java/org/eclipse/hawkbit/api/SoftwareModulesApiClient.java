package org.eclipse.hawkbit.api;

import org.eclipse.hawkbit.model.*;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client(id = "hawkbit")
public interface SoftwareModulesApiClient {

	java.lang.String PATH_CREATE_SOFTWAREMODULE = "/rest/v1/softwaremodules";
	java.lang.String PATH_GET_SOFTWAREMODULES = "/rest/v1/softwaremodules";
	java.lang.String PATH_UPLOAD_ARTIFACT = "/rest/v1/softwaremodules/{software_module_id}/artifacts";

	@io.micronaut.http.annotation.Post(PATH_CREATE_SOFTWAREMODULE)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<SoftwaremoduleCreatetionResponsePartVO>> createSoftwaremodule(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			java.util.List<SoftwaremoduleCreatetionRequestPartVO> softwaremoduleCreatetionRequestPartVO);

	@io.micronaut.http.annotation.Get(PATH_GET_SOFTWAREMODULES)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<SoftwaremoduleSearchResponseVO> getSoftwaremodules(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "limit")
			java.lang.Integer limit,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "sort")
			java.lang.String sort,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "offset")
			java.lang.Integer offset,
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.QueryValue(value = "query")
			java.lang.String query);

	@io.micronaut.http.annotation.Post(PATH_UPLOAD_ARTIFACT)
	@io.micronaut.http.annotation.Produces({ "multipart/form-data" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<ArtifactUploadResponseVO> uploadArtifact(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "software_module_id")
			java.lang.Integer softwareModuleId,
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.Body
			io.micronaut.http.client.multipart.MultipartBody multipart);
}
