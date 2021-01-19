package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.GroupRepository;
import io.kokuwa.fleet.registry.domain.TenantRepository;
import io.kokuwa.fleet.registry.rest.RestMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.reactivex.Single;
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
	public Single<HttpResponse<List<GroupVO>>> getGroups(Optional<UUID> tenant) {
		return tenant
				.map(groupRepository::findByTenantIdOrderByName).orElseGet(groupRepository::findAllOrderByName)
				.map(mapper::toGroup).toList().map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<GroupVO>> getGroup(UUID id) {
		return groupRepository.findById(id)
				.doOnComplete(() -> {
					log.trace("Group not found.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
				})
				.toSingle().map(mapper::toGroup).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<GroupVO>> createGroup(GroupCreateVO vo) {

		// get tenant

		var tenantSingle = tenantRepository.findById(vo.getTenant())
				.doOnComplete(() -> {
					log.trace("Tenant not found.");
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tenant not found.");
				})
				.toSingle();

		// check name for uniqueness

		tenantSingle = tenantSingle.flatMap(tenant -> groupRepository.existsByTenantAndName(tenant, vo.getName())
				.flatMap(exists -> {
					if (exists) {
						throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
					}
					return Single.just(tenant);
				}));

		// create group

		var groupSingle = tenantSingle
				.map(tenant -> new Group().setTenant(tenant).setName(vo.getName()))
				.flatMap(groupRepository::save)
				.doOnSuccess(group -> log.info("Created group: {}", group));

		// return

		return groupSingle.map(mapper::toGroup).map(HttpResponse::created);
	}

	@Override
	public Single<HttpResponse<GroupVO>> updateGroup(UUID id, GroupUpdateVO vo) {

		// get tenant from database

		var changed = new AtomicBoolean(false);
		var groupSingle = groupRepository.findById(id)
				.doOnComplete(() -> {
					log.trace("Skip update of non existing group.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
				})
				.toSingle();

		// update fields

		if (vo.getName() != null) {
			groupSingle = groupSingle.flatMap(group -> {
				if (group.getName().equals(vo.getName())) {
					log.trace("Group {}: skip update of name ecause not changed.", group.getName());
					return Single.just(group);
				} else {
					return groupRepository.existsByTenantAndName(group.getTenant(), vo.getName()).flatMap(exists -> {
						if (exists) {
							throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
						}
						log.info("Group {}: updated name to {}.", group.getName(), vo.getName());
						changed.set(true);
						return Single.just(group.setName(vo.getName()));
					});
				}
			});
		}

		// return updated

		return groupSingle
				.flatMap(group -> changed.get() ? groupRepository.update(group) : Single.just(group))
				.map(mapper::toGroup).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<Object>> deleteGroup(UUID id) {
		return groupRepository.findById(id)
				.doOnComplete(() -> {
					log.trace("Skip deletion of non existing group.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Group not found.");
				})
				.flatMapSingle(group -> groupRepository.deleteById(id).toSingle(() -> {
					log.info("Group {} deleted.", group.getName());
					return HttpResponse.noContent();
				}));
	}
}
