package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import io.inoa.fleet.api.GroupsApi;
import io.inoa.fleet.model.GroupCreateVO;
import io.inoa.fleet.model.GroupUpdateVO;
import io.inoa.fleet.model.GroupVO;
import io.inoa.fleet.registry.domain.Group;
import io.inoa.fleet.registry.domain.GroupRepository;
import io.inoa.fleet.registry.rest.mapper.GroupMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link GroupsApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class GroupsController extends AbstractManagementController implements GroupsApi {

	private final Security security;
	private final GroupMapper mapper;
	private final GroupRepository repository;

	@Override
	public HttpResponse<List<GroupVO>> findGroups() {
		return HttpResponse.ok(mapper.toGroups(repository.findByTenantInListOrderByName(security.getGrantedTenants())));
	}

	@Override
	public HttpResponse<GroupVO> findGroup(@NonNull UUID groupId, @NonNull Optional<String> tenantId) {
		return HttpResponse.ok(mapper.toGroup(getGroup(groupId, tenantId)));
	}

	@Override
	public HttpResponse<GroupVO> createGroup(@Valid GroupCreateVO vo, @NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);

		if (repository.existsByTenantAndName(tenant, vo.getName())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Name already exists.");
		}
		if (vo.getGroupId() != null && repository.existsByTenantAndGroupId(tenant, vo.getGroupId())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "GroupId already exists.");
		}

		Group group = repository.save(new Group().setTenant(tenant).setName(vo.getName())
				.setGroupId(vo.getGroupId() == null ? UUID.randomUUID() : vo.getGroupId()));

		log.info("Created group: {}", group);
		return HttpResponse.created(mapper.toGroup(group));
	}

	@Override
	public HttpResponse<GroupVO> updateGroup(@NonNull UUID groupId, @Valid GroupUpdateVO vo,
			@NonNull Optional<String> tenantId) {

		var changed = false;
		var group = getGroup(groupId, tenantId);

		if (vo.getName() != null) {
			if (group.getName().equals(vo.getName())) {
				log.trace("Group {}: skip update of name ecause not changed.", group.getName());
			} else {
				if (repository.existsByTenantAndName(group.getTenant(), vo.getName())) {
					throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
				}
				log.info("Group {}: updated name to {}.", group.getName(), vo.getName());
				changed = true;
				group.setName(vo.getName());
			}
		}

		if (changed) {
			group = repository.update(group);
		}

		return HttpResponse.ok(mapper.toGroup(group));
	}

	@Override
	public HttpResponse<Object> deleteGroup(@NonNull UUID groupId, @NonNull Optional<String> tenantId) {
		var group = getGroup(groupId, tenantId);
		repository.delete(group);
		log.info("Group {} deleted.", group.getName());
		return HttpResponse.noContent();
	}

	private Group getGroup(UUID groupId, Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		var optional = repository.findByTenantAndGroupId(tenant, groupId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
		}
		return optional.get();
	}
}
