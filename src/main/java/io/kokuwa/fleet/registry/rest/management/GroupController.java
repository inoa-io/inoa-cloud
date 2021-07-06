package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.GroupRepository;
import io.kokuwa.fleet.registry.domain.TenantRepository;
import io.kokuwa.fleet.registry.rest.RestMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link GroupApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class GroupController implements GroupApi {

	private final RestMapper mapper;
	private final TenantRepository tenantRepository;
	private final GroupRepository groupRepository;

	@Override
	public HttpResponse<List<GroupVO>> getGroups(Optional<UUID> tenantId) {
		List<Group> groups;
		if (tenantId.isPresent()) {
			groups = groupRepository.findByTenantExternalIdOrderByName(tenantId.get());
		} else {
			groups = groupRepository.findAllOrderByName();
		}
		return HttpResponse.ok(groups.stream().map(mapper::toGroup).collect(Collectors.toList()));
	}

	@Override
	public HttpResponse<GroupVO> getGroup(UUID groupId) {
		Optional<Group> optionalGroup = groupRepository.findByExternalId(groupId);
		if (optionalGroup.isEmpty()) {
			log.trace("Group not found.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
		}
		return HttpResponse.ok(mapper.toGroup(optionalGroup.get()));
	}

	@Override
	public HttpResponse<GroupVO> createGroup(GroupCreateVO vo) {

		// get tenant
		var optionalTenant = tenantRepository.findByExternalId(vo.getTenantId());
		if (optionalTenant.isEmpty()) {
			log.trace("Tenant not found.");
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tenant not found.");
		}

		// check name for uniqueness
		Boolean existsByTenantAndName = groupRepository.existsByTenantAndName(optionalTenant.get(), vo.getName());
		if (existsByTenantAndName) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}

		// create group

		var group = new Group().setTenant(optionalTenant.get()).setName(vo.getName());
		groupRepository.save(group);
		log.info("Created group: {}", group.getName());

		// return
		return HttpResponse.created(mapper.toGroup(group));
	}

	@Override
	public HttpResponse<GroupVO> updateGroup(UUID groupId, GroupUpdateVO vo) {

		// get tenant from database

		var changed = new AtomicBoolean(false);
		var optionalGroup = groupRepository.findByExternalId(groupId);
		if (optionalGroup.isEmpty()) {
			log.trace("Skip update of non existing group.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
		}

		// update fields
		var group = optionalGroup.get();
		if (vo.getName() != null) {
			if (group.getName().equals(vo.getName())) {
				log.trace("Group {}: skip update of name ecause not changed.", group.getName());
			} else {
				Boolean existsByTenantAndName = groupRepository.existsByTenantAndName(group.getTenant(), vo.getName());
				if (existsByTenantAndName) {
					throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
				}
				log.info("Group {}: updated name to {}.", group.getName(), vo.getName());
				changed.set(true);
				group.setName(vo.getName());
			}
		}

		// return updated
		if (changed.get()) {
			group = groupRepository.update(group);
		}
		return HttpResponse.ok(mapper.toGroup(group));
	}

	@Override
	public HttpResponse<Object> deleteGroup(UUID groupId) {
		Optional<Group> optionalGroup = groupRepository.findByExternalId(groupId);
		if (optionalGroup.isEmpty()) {
			log.trace("Skip deletion of non existing group.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
		}
		groupRepository.delete(optionalGroup.get());
		log.info("Group {} deleted.", optionalGroup.get().getName());
		return HttpResponse.noContent();
	}
}
