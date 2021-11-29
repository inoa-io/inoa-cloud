package io.inoa.cnpm.tenant.management;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.mapstruct.factory.Mappers;
import org.slf4j.MDC;

import io.inoa.cnpm.tenant.PageableProvider;
import io.inoa.cnpm.tenant.domain.User;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;

/**
 * Controller for {@link TenantsApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@RequiredArgsConstructor
public class UserController implements UsersApi {

	/** Default sort properties, see API spec for documentation. */
	public static final String SORT_ORDER_DEFAULT = "user." + UserVO.JSON_PROPERTY_EMAIL;
	/** Available sort properties, see API spec for documentation. */
	public static final Map<String, String> SORT_ORDER_PROPERTIES = Map.of(
			UserVO.JSON_PROPERTY_EMAIL, "user." + UserVO.JSON_PROPERTY_EMAIL,
			UserVO.JSON_PROPERTY_CREATED, "user." + UserVO.JSON_PROPERTY_CREATED);

	private final ManagementMapper mapper = Mappers.getMapper(ManagementMapper.class);
	private final ManagementService service;
	private final PageableProvider pageableProvider;

	@Get("/tenants/{tenant_id}/users")
	@Override
	public HttpResponse<UserPageVO> findUsers(
			String tenantId,
			Optional<Integer> page,
			Optional<Integer> size,
			Optional<List<String>> sort,
			Optional<String> filter) {
		try (var mdc = MDC.putCloseable("tenantId", tenantId)) {
			var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES, SORT_ORDER_DEFAULT);
			return service.findUsers(tenantId, filter, pageable)
					.map(mapper::toUserPage)
					.map(HttpResponse::ok)
					.orElseGet(HttpResponse::notFound);
		}
	}

	@Override
	public HttpResponse<UserVO> findUser(String tenantId, UUID userId) {
		try {
			MDC.put("tenantId", tenantId);
			MDC.put("userId", userId.toString());
			return service.findUser(tenantId, userId)
					.map(mapper::toUser)
					.map(HttpResponse::ok)
					.orElseGet(HttpResponse::notFound);
		} finally {
			MDC.remove("tenantId");
			MDC.remove("userId");
		}
	}

	@Override
	public HttpResponse<UserVO> createUser(String tenantId, @Valid UserCreateVO vo) {
		try {
			MDC.put("tenantId", tenantId);
			if (service.existsUserByEmail(tenantId, vo.getEmail())) {
				throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
			}
			return service.createUser(tenantId, new User().setEmail(vo.getEmail()))
					.map(mapper::toUser)
					.map(HttpResponse::created)
					.orElseGet(HttpResponse::notFound);
		} finally {
			MDC.remove("tenantId");
			MDC.remove("userId");
		}
	}

	@Override
	public HttpResponse<Object> deleteUser(String tenantId, UUID userId) {
		try {
			MDC.put("tenantId", tenantId);
			MDC.put("userId", userId.toString());
			return service.deleteUser(tenantId, userId) ? HttpResponse.noContent() : HttpResponse.notFound();
		} finally {
			MDC.remove("tenantId");
			MDC.remove("userId");
		}
	}
}
