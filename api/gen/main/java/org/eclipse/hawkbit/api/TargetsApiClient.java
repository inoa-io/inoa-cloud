package org.eclipse.hawkbit.api;

import org.eclipse.hawkbit.model.*;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client(id = "hawkbit")
public interface TargetsApiClient {

	java.lang.String PATH_CREATE_TARGETS = "/rest/v1/targets";
	java.lang.String PATH_GET_TARGET = "/rest/v1/targets/{target_id}";
	java.lang.String PATH_GET_TARGETS = "/rest/v1/targets";

	@io.micronaut.http.annotation.Post(PATH_CREATE_TARGETS)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<TargetsCreationResponsePartVO>> createTargets(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			java.util.List<TargetsCreationRequestPartVO> targetsCreationRequestPartVO);

	@io.micronaut.http.annotation.Get(PATH_GET_TARGET)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<TargetSearchResponseItemVO> getTarget(
			@io.micronaut.core.annotation.NonNull
			@io.micronaut.http.annotation.PathVariable(value = "target_id")
			java.lang.String targetId);

	@io.micronaut.http.annotation.Get(PATH_GET_TARGETS)
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<TargetsSearchResponseVO> getTargets(
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
}
