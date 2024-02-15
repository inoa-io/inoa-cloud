package org.eclipse.hawkbit.api;

import org.eclipse.hawkbit.model.*;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.http.client.annotation.Client(id = "hawkbit")
public interface DistributionSetsApiClient {

	java.lang.String PATH_CREATE_DISTRIBUTION_SET = "/rest/v1/distributionsets";

	@io.micronaut.http.annotation.Post(PATH_CREATE_DISTRIBUTION_SET)
	@io.micronaut.http.annotation.Produces({ "application/json" })
	@io.micronaut.http.annotation.Consumes({ "application/json" })
	io.micronaut.http.HttpResponse<java.util.List<DistributionSetCreationResponsePartVO>> createDistributionSet(
			@io.micronaut.core.annotation.Nullable
			@io.micronaut.http.annotation.Body
			java.util.List<DistributionSetCreationRequestPartVO> distributionSetCreationRequestPartVO);
}
