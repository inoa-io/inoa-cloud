package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayGroup;
import io.kokuwa.fleet.registry.domain.GatewayGroup.GatewayGroupPK;
import io.kokuwa.fleet.registry.domain.GatewayGroupRepository;
import io.kokuwa.fleet.registry.domain.GatewayPropertyRepository;
import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.GroupRepository;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.domain.TenantRepository;
import io.kokuwa.fleet.registry.rest.RestMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link GatewayApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class GatewayController implements GatewayApi {

	private final RestMapper mapper;
	private final TenantRepository tenantRepository;
	private final GroupRepository groupRepository;
	private final GatewayRepository gatewayRepository;
	private final GatewayGroupRepository gatewayGroupRepository;
	private final GatewayPropertyRepository gatewayPropertyRepository;

	@Override
	public Single<HttpResponse<List<GatewayVO>>> getGateways(Optional<UUID> tenantId, Optional<UUID> group) {
		return tenantId
				.map(gatewayRepository::findByTenantExternalIdOrderByName)
				.orElseGet(gatewayRepository::findAllOrderByName)
				.map(mapper::toGateway).toList().map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<GatewayDetailVO>> getGateway(UUID gatewayId) {
		return gatewayRepository.findByExternalId(gatewayId)
				.doOnComplete(() -> {
					log.trace("Gateway not found.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
				})
				.toSingle().flatMap(this::toGatewayDetail).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<GatewayDetailVO>> createGateway(GatewayCreateVO vo) {

		// get tenant

		var tenantSingle = tenantRepository.findByExternalId(vo.getTenantId())
				.doOnComplete(() -> {
					log.trace("Tenant not found.");
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tenant not found.");
				})
				.toSingle();

		// check id/name for uniqueness

		var uniqueIdCompletable = vo.getGatewayId() == null ? Completable.complete()
				: gatewayRepository
						.existsByExternalId(vo.getGatewayId())
						.doOnSuccess(exists -> {
							if (exists) {
								throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
							}
						})
						.ignoreElement();
		var uniqueNameCompletable = tenantSingle.flatMap(tenant -> gatewayRepository
				.existsByTenantAndName(tenant, vo.getName())
				.flatMap(exists -> {
					if (exists) {
						throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
					}
					return Single.just(tenant);
				}))
				.ignoreElement();
		tenantSingle = Completable.mergeArray(uniqueNameCompletable, uniqueIdCompletable).andThen(tenantSingle);

		// check groups for existence

		var groupIds = Optional.ofNullable(vo.getGroupIds()).orElseGet(Set::of);
		var groupsSingle = groupIds.isEmpty()
				? Single.just(List.<Group>of())
				: tenantSingle.flatMap(tenant -> getGroups(tenant, groupIds));

		// create gateway

		var gatewaySingle = Single
				.zip(tenantSingle, groupsSingle, (tenant, groups) -> (Gateway) new Gateway()
						.setTenant(tenant)
						.setName(vo.getName())
						.setEnabled(vo.getEnabled())
						.setGroups(groups)
						.setExternalId(vo.getGatewayId() == null ? UUID.randomUUID() : vo.getGatewayId()))
				.flatMap(gatewayRepository::save)
				.doOnSuccess(gateway -> log.info("Created gateway: {}", gateway));

		// create group assignments

		if (!groupIds.isEmpty()) {
			gatewaySingle = gatewaySingle.flatMap(gateway -> gatewayGroupRepository
					.saveAll(gateway.getGroups().stream()
							.map(group -> new GatewayGroup().setPk(new GatewayGroupPK(gateway, group)))
							.collect(Collectors.toSet()))
					.ignoreElements()
					.toSingleDefault(gateway));
		}

		// return

		return gatewaySingle.map(mapper::toGatewayDetail).map(HttpResponse::created);
	}

	@Override
	public Single<HttpResponse<GatewayDetailVO>> updateGateway(UUID gatewayId, GatewayUpdateVO vo) {

		// get tenant from database

		var changed = new AtomicBoolean(false);
		var gatewaySingle = gatewayRepository.findByExternalId(gatewayId)
				.doOnComplete(() -> {
					log.trace("Skip update of non existing gateway.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
				})
				.toSingle();

		// update fields

		if (vo.getName() != null) {
			gatewaySingle = gatewaySingle.flatMap(gateway -> {
				if (gateway.getName().equals(vo.getName())) {
					log.trace("Gateway {}: skip update of name because not changed.", gateway.getName());
					return Single.just(gateway);
				} else {
					return gatewayRepository.existsByTenantAndName(gateway.getTenant(), vo.getName())
							.flatMap(exists -> {
								if (exists) {
									throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
								}
								log.info("Tenant {}: updated name to {}.", gateway.getName(), vo.getName());
								changed.set(true);
								return Single.just(gateway.setName(vo.getName()));
							});
				}
			});
		}
		if (vo.getEnabled() != null) {
			gatewaySingle = gatewaySingle.flatMap(gateway -> {
				if (gateway.getEnabled() == vo.getEnabled()) {
					log.trace("Skip update of enabled {} because not changed.", gateway.getEnabled());
					return Single.just(gateway);
				} else {
					log.info("Gateway {}: updated enabled to {}.", gateway.getName(), vo.getEnabled());
					changed.set(true);
					return Single.just(gateway.setEnabled(vo.getEnabled()));
				}
			});
		}
		if (vo.getGroupIds() != null) {
			var oldGroupsSingle = gatewaySingle
					.flatMap(gateway -> gatewayGroupRepository.findGroupsByGatewayId(gateway.getId()).toList());
			var newGroupsSingle = gatewaySingle
					.flatMap(gateway -> getGroups(gateway.getTenant(), vo.getGroupIds()));
			gatewaySingle = Single.zip(gatewaySingle, oldGroupsSingle, newGroupsSingle,
					(gateway, oldGroups, newGroups) -> {

						var oldGroupIds = oldGroups.stream().map(Group::getExternalId).collect(Collectors.toSet());
						var newGroupIds = vo.getGroupIds();

						// remove group

						var removedGroups = oldGroups.stream()
								.filter(oldGroup -> !newGroupIds.contains(oldGroup.getExternalId()))
								.map(oldGroup -> new GatewayGroupPK(gateway, oldGroup))
								.collect(Collectors.toSet());
						var removeCompletable = Completable.concat(removedGroups.stream()
								.map(gatewayGroupRepository::deleteById)
								.collect(Collectors.toSet()));

						// add group

						var addedGroups = newGroups.stream()
								.filter(newGroup -> !oldGroupIds.contains(newGroup.getExternalId()))
								.map(newGroup -> new GatewayGroup().setPk(new GatewayGroupPK(gateway, newGroup)))
								.collect(Collectors.toSet());
						var addCompletable = addedGroups.isEmpty()
								? Completable.complete()
								: gatewayGroupRepository.saveAll(addedGroups).ignoreElements();

						changed.set(!addedGroups.isEmpty() || !removedGroups.isEmpty());
						return removeCompletable.andThen(addCompletable).toSingleDefault(gateway);
					}).flatMap(s -> s);
		}

		// return updated

		return gatewaySingle
				.flatMap(gateway -> changed.get() ? gatewayRepository.update(gateway) : Single.just(gateway))
				.flatMap(this::toGatewayDetail)
				.map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<Object>> deleteGateway(UUID gatewayId) {
		return gatewayRepository.findByExternalId(gatewayId)
				.doOnComplete(() -> {
					log.trace("Skip deletion of non existing gateway.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
				})
				.flatMapSingle(gateway -> gatewayRepository.delete(gateway).toSingle(() -> {
					log.info("Gateway {} deleted.", gateway.getName());
					return HttpResponse.noContent();
				}));
	}

	private Single<List<Group>> getGroups(Tenant tenant, Set<UUID> groupIds) {
		return Flowable.fromIterable(groupIds).flatMapSingle(groupId -> groupRepository.findByExternalId(groupId)
				.doOnComplete(() -> {
					log.trace("Group " + groupId + " not found.");
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Group " + groupId + " not found.");
				})
				.doOnSuccess(group -> {
					if (!Objects.equals(tenant.getExternalId(), group.getTenant().getExternalId())) {
						throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Group " + groupId + " not found.");
					}
				}).toSingle()).toList();
	}

	private Single<GatewayDetailVO> toGatewayDetail(Gateway gateway) {
		return Completable
				.mergeArray(
						gatewayPropertyRepository
								.findByGatewayId(gateway.getId())
								.toList().map(gateway::setProperties).ignoreElement(),
						gatewayGroupRepository
								.findGroupsByGatewayId(gateway.getId())
								.toList().map(gateway::setGroups).ignoreElement())
				.toSingleDefault(gateway)
				.map(mapper::toGatewayDetail);
	}
}
