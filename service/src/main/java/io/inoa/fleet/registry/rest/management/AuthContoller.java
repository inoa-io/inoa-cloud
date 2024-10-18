package io.inoa.fleet.registry.rest.management;

import io.inoa.measurement.things.rest.Security;
import io.inoa.rest.AuthApi;
import io.inoa.rest.UserVO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;

/**
 * Controller for {@link AuthApi}.
 *
 * @author roy
 */
@Controller
@RequiredArgsConstructor
public class AuthContoller implements AuthApi {
	private final Security security;

	@Override
	public HttpResponse<UserVO> whoami() {
		return HttpResponse.ok(security.getUser());
	}
}
