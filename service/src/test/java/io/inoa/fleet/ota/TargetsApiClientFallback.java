package io.inoa.fleet.ota;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Singleton;

import org.eclipse.hawkbit.api.TargetsApiClient;
import org.eclipse.hawkbit.model.TargetSearchResponseItemVO;
import org.eclipse.hawkbit.model.TargetsCreationRequestPartVO;
import org.eclipse.hawkbit.model.TargetsCreationResponsePartVO;
import org.eclipse.hawkbit.model.TargetsSearchResponseVO;
import org.jetbrains.annotations.NotNull;

import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.retry.annotation.Fallback;

@Primary
@Fallback
@Singleton
public class TargetsApiClientFallback implements TargetsApiClient {

	private final Map<String, TargetsCreationRequestPartVO> targets = new HashMap<>();

	@Override
	public HttpResponse<List<TargetsCreationResponsePartVO>> createTargets(
			List<TargetsCreationRequestPartVO> targetsCreationRequestPartVO) {
		if (targetsCreationRequestPartVO == null) {
			return HttpResponse.badRequest();
		}
		List<TargetsCreationResponsePartVO> responses = new ArrayList<>();
		for (TargetsCreationRequestPartVO targetsCreationRequest : targetsCreationRequestPartVO) {
			if (targets.containsKey(targetsCreationRequest.getControllerId())) {
				return HttpResponse.status(HttpStatus.CONFLICT);
			}
			targets.put(targetsCreationRequest.getControllerId(), targetsCreationRequest);
			responses.add(
					new TargetsCreationResponsePartVO()
							.controllerId(targetsCreationRequest.getControllerId())
							.name(targetsCreationRequest.getName())
							.securityToken(targetsCreationRequest.getSecurityToken()));
		}
		return HttpResponse.created(responses);
	}

	@Override
	public HttpResponse<TargetSearchResponseItemVO> getTarget(@NotNull String targetId) {
		return targets.containsKey(targetId)
				? HttpResponse.status(HttpStatus.OK)
						.body(
								new TargetSearchResponseItemVO()
										.controllerId(targets.get(targetId).getControllerId())
										.name(targets.get(targetId).getName())
										.securityToken(targets.get(targetId).getSecurityToken()))
				: HttpResponse.notFound();
	}

	@Override
	public HttpResponse<TargetsSearchResponseVO> getTargets(
			Integer limit, String sort, Integer offset, String query) {
		var searchResultItems = targets.values().stream()
				.map(
						targetsCreationRequestPartVO -> new TargetSearchResponseItemVO()
								.controllerId(targetsCreationRequestPartVO.getControllerId())
								.name(targetsCreationRequestPartVO.getName())
								.securityToken(targetsCreationRequestPartVO.getSecurityToken()))
				.toList();
		return HttpResponse.status(HttpStatus.OK)
				.body(new TargetsSearchResponseVO().content(searchResultItems));
	}
}
