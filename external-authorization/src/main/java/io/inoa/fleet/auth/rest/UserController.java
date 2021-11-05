package io.inoa.fleet.auth.rest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import io.inoa.fleet.auth.domain.Tenant;
import io.inoa.fleet.auth.domain.TenantRepository;
import io.inoa.fleet.auth.domain.TenantUser;
import io.inoa.fleet.auth.domain.TenantUserRepositoryImpl;
import io.inoa.fleet.auth.domain.User;
import io.inoa.fleet.auth.domain.UserRepository;
import io.inoa.fleet.auth.mapper.UserMapper;
import io.inoa.fleet.auth.rest.management.UserCreateVO;
import io.inoa.fleet.auth.rest.management.UserPageVO;
import io.inoa.fleet.auth.rest.management.UserUpdateVO;
import io.inoa.fleet.auth.rest.management.UserVO;
import io.inoa.fleet.auth.rest.management.UsersApi;
import io.micronaut.data.model.Page;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController implements UsersApi {

	/** Available sort properties, see API spec for documentation. */
	public static final Set<String> SORT_ORDER_PROPERTIES = Set.of(
			String.format("users.%s", UserVO.JSON_PROPERTY_EMAIL),
			String.format("users.%s", UserVO.JSON_PROPERTY_CREATED));

	private final UserMapper mapper;
	private final TenantRepository tenantRepository;
	private final UserRepository userRepository;
	private final TenantUserRepositoryImpl tenantUserRepository;
	private final SecurityService securityService;
	private final PageableProvider pageableProvider;

	@Override
	public HttpResponse<UserVO> createUser(String tenantId, UserCreateVO userCreateVO) {
		Optional<Authentication> authentication = securityService.getAuthentication();
		if (authentication.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authorized.");
		}
		Object email = authentication.get().getAttributes().get("email");

		Optional<User> optionalCurrentUser = userRepository.findByEmail(email.toString());
		if (optionalCurrentUser.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authorized.");
		}
		Optional<Tenant> optionalTenant = tenantRepository.findByTenantId(tenantId);
		if (optionalTenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Not found.");
		}
		Optional<TenantUser> byTenantAndUser = tenantUserRepository.findByTenantAndUser(optionalTenant.get(),
				optionalCurrentUser.get());
		if (byTenantAndUser.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authorized.");
		}
		// we are good to go

		Optional<User> optionalUser = userRepository.findByEmail(userCreateVO.getEmail());
		User user;
		if (optionalUser.isEmpty()) {
			user = userRepository.save(new User().setUserId(UUID.randomUUID()).setEmail(userCreateVO.getEmail()));
		} else {
			user = optionalUser.get();
		}

		tenantUserRepository.save(new TenantUser().setTenant(optionalTenant.get()).setUser(user));

		// return
		return HttpResponse.created(mapper.toUser(user));
	}

	@Override
	public HttpResponse<Object> deleteUser(String tenantId, UUID userId) {
		return null;
	}

	@Override
	public HttpResponse<UserVO> findUser(String tenantId, UUID userId) {
		return null;
	}

	@Get("/tenants/{tenant_id}/users")
	@Override
	public HttpResponse<UserPageVO> findUsers(@PathVariable(name = "tenant_id") String tenantId, Optional<Integer> page,
			Optional<Integer> size, Optional<List<String>> sort, Optional<String> filter) {

		var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES,
				String.format("user.%s", UserVO.JSON_PROPERTY_EMAIL));

		Optional<Authentication> authentication = securityService.getAuthentication();
		if (authentication.isEmpty()) {
			throw new HttpStatusException(HttpStatus.UNAUTHORIZED, "Not authorized.");
		}
		Object email = authentication.get().getAttributes().get("email");

		Optional<Tenant> optionalTenant = tenantUserRepository.findTenantByTenantTenantIdAndUserEmail(tenantId,
				email.toString());
		if (optionalTenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "No tenant found.");
		}
		Page<User> userByTenant = tenantUserRepository.findUserForTenant(optionalTenant.get(), filter, pageable);
		return HttpResponse.ok(mapper.toUserPage(userByTenant));
	}

	@Override
	public HttpResponse<UserVO> updateUser(String tenantId, UUID userId, UserUpdateVO userUpdateVO) {
		return null;
	}
}
