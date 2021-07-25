package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.UUID;

import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.GroupRepository;
import io.kokuwa.fleet.registry.rest.mapper.GroupMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
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
public class GroupsController implements GroupsApi {

	private final SecurityManagement security;
	private final GroupMapper mapper;
	private final GroupRepository repository;

	@Override
	public HttpResponse<List<GroupVO>> findGroups() {
		return HttpResponse.ok(mapper.toGroups(repository.findByTenantOrderByName(security.getTenant())));
	}

	@Override
	public HttpResponse<GroupVO> findGroup(UUID groupId) {
		return HttpResponse.ok(mapper.toGroup(getGroup(groupId)));
	}

	@Override
	public HttpResponse<GroupVO> createGroup(GroupCreateVO vo) {
		var tenant = security.getTenant();

		if (repository.existsByTenantAndName(tenant, vo.getName())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Name already exists.");
		}
		if (vo.getGroupId() != null && repository.existsByTenantAndGroupId(tenant, vo.getGroupId())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "GroupId already exists.");
		}

		var group = repository.save(new Group()
				.setTenant(tenant)
				.setName(vo.getName())
				.setGroupId(vo.getGroupId() == null ? UUID.randomUUID() : vo.getGroupId()));

		log.info("Created group: {}", group);
		return HttpResponse.created(mapper.toGroup(group));
	}

	@Override
	public HttpResponse<GroupVO> updateGroup(UUID groupId, GroupUpdateVO vo) {

		var changed = false;
		var group = getGroup(groupId);

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
	public HttpResponse<Object> deleteGroup(UUID groupId) {
		var group = getGroup(groupId);
		repository.delete(group);
		log.info("Group {} deleted.", group.getName());
		return HttpResponse.noContent();
	}

	private Group getGroup(UUID groupId) {
		var optional = repository.findByTenantAndGroupId(security.getTenant(), groupId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
		}
		return optional.get();
	}
}
