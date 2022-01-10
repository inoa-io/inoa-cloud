package io.inoa.cnpm.tenant.rest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;

import io.inoa.cnpm.tenant.domain.Assignment;
import io.inoa.cnpm.tenant.domain.Tenant;
import io.inoa.cnpm.tenant.messaging.CloudEventSubjectVO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for {@link TenantsApi}.
 */
@Controller
@Slf4j
public class UserController extends RestController implements UsersApi {

	/** Default sort properties, see API spec for documentation. */
	public static final String SORT_ORDER_DEFAULT = "user." + UserVO.JSON_PROPERTY_EMAIL;
	/** Available sort properties, see API spec for documentation. */
	public static final Map<String, String> SORT_ORDER_PROPERTIES = Map.of(
			UserVO.JSON_PROPERTY_EMAIL, "user." + UserVO.JSON_PROPERTY_EMAIL,
			UserVO.JSON_PROPERTY_CREATED, "user." + UserVO.JSON_PROPERTY_CREATED);

	@Get("/tenants/{tenant_id}/users")
	@Override
	public HttpResponse<UserPageVO> findUsers(
			String tenantId,
			Optional<Integer> page,
			Optional<Integer> size,
			Optional<List<String>> sort,
			Optional<String> optionalFilter) {
		var tenant = isService() ? getTenantAsService(tenantId) : getTenant(tenantId);
		var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES, SORT_ORDER_DEFAULT);
		var assignments = optionalFilter
				.map(filter -> "%" + filter.replace("*", "%") + "%")
				.map(filter -> assignmentRepository.findByTenantAndUserEmailIlikeFilter(tenant, filter, pageable))
				.orElseGet(() -> assignmentRepository.findByTenant(tenant, pageable));
		return HttpResponse.ok(mapper.toUsers(assignments));
	}

	@Override
	public HttpResponse<UserVO> findUser(String tenantId, UUID userId) {
		var tenant = isService() ? getTenantAsService(tenantId) : getTenant(tenantId);
		return HttpResponse.ok(mapper.toUser(getAssignment(tenant, userId)));
	}

	@Transactional
	@Override
	public HttpResponse<UserVO> createUser(String tenantId, @Valid UserCreateVO vo) {

		var tenant = getTenantAsAdmin(tenantId);
		var email = vo.getEmail();
		if (assignmentRepository.findByTenantTenantIdAndUserEmail(tenantId, email).isPresent()) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}

		var user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(email));
		var role = vo.getRole();
		var assignment = new Assignment().setTenant(tenant).setUser(user).setRole(role);

		assignmentRepository.save(assignment);
		messaging.send(assignment, CloudEventSubjectVO.CREATE);

		log.info("Tenant {} assigned user {} in role {}.", tenantId, user.getUserId(), role);

		return HttpResponse.created(mapper.toUser(assignment));
	}

	@Transactional
	@Override
	public HttpResponse<UserVO> updateUser(String tenantId, UUID userId, @Valid UserUpdateVO vo) {
		var assignment = getAssignment(getTenantAsAdmin(tenantId), userId);
		var changed = false;

		var oldRole = assignment.getRole();
		var newRole = vo.getRole();
		if (newRole != null) {
			if (Objects.equals(oldRole, newRole)) {
				log.trace("Tenant {} user {}: skip update of role {} because not changed", tenantId, userId, newRole);
			} else {
				log.info("Tenant {} user {}: updated role from {} to {}.", tenantId, userId, oldRole, newRole);
				changed = true;
				assignment.setRole(newRole);
			}
		}

		if (changed) {
			assignmentRepository.update(assignment);
			messaging.send(assignment, CloudEventSubjectVO.UPDATE);
		}

		return HttpResponse.ok(mapper.toUser(assignment));
	}

	@Transactional
	@Override
	public HttpResponse<Object> deleteUser(String tenantId, UUID userId) {
		var assignment = getAssignment(getTenantAsAdmin(tenantId), userId);
		assignmentRepository.delete(assignment);
		messaging.send(assignment, CloudEventSubjectVO.DELETE);
		log.info("Tenant {} user {} deleted.", tenantId, userId);
		return HttpResponse.noContent();

	}

	private Assignment getAssignment(Tenant tenant, UUID userId) {
		return assignmentRepository
				.findByTenantAndUserUserId(tenant, userId)
				.orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND, "User not found."))
				.setTenant(tenant);
	}
}
