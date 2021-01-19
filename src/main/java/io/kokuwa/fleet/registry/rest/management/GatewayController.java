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
	public Single<HttpResponse<List<GatewayVO>>> getGateways(Optional<UUID> tenant, Optional<UUID> group) {
		return tenant
				.map(gatewayRepository::findByTenantIdOrderByName).orElseGet(gatewayRepository::findAllOrderByName)
				.map(mapper::toGateway).toList().map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<GatewayDetailVO>> getGateway(UUID id) {
		return gatewayRepository.findById(id)
				.doOnComplete(() -> {
					log.trace("Gateway not found.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
				})
				.toSingle().flatMap(this::toGatewayDetail).map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<GatewayDetailVO>> createGateway(GatewayCreateVO vo) {

		// get tenant

		var tenantSingle = tenantRepository.findById(vo.getTenant())
				.doOnComplete(() -> {
					log.trace("Tenant not found.");
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tenant not found.");
				})
				.toSingle();

		// check name for uniqueness

		tenantSingle = tenantSingle.flatMap(tenant -> gatewayRepository.existsByTenantAndName(tenant, vo.getName())
				.flatMap(exists -> {
					if (exists) {
						throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
					}
					return Single.just(tenant);
				}));

		// check groups for existence

		var groupIds = Optional.ofNullable(vo.getGroups()).map(Set::copyOf).orElseGet(Set::of);
		var groupsCompletable = groupIds.isEmpty() ? Completable.complete()
				: tenantSingle.flatMapCompletable(tenant -> validateGroups(tenant, groupIds));

		// create gateway

		var gatewaySingle = groupsCompletable.andThen(tenantSingle)
				.map(tenant -> new Gateway()
						.setTenant(tenant)
						.setName(vo.getName())
						.setEnabled(vo.getEnabled()))
				.flatMap(gatewayRepository::save)
				.doOnSuccess(gateway -> log.info("Created gateway: {}", gateway));

		// create group assignments

		if (!groupIds.isEmpty()) {
			gatewaySingle = gatewaySingle.flatMap(gateway -> gatewayGroupRepository
					.saveAll(groupIds.stream()
							.map(groupId -> new GatewayGroup().setPk(new GatewayGroupPK(gateway.getId(), groupId)))
							.collect(Collectors.toSet()))
					.ignoreElements()
					.toSingleDefault(gateway));
		}

		// return

		return gatewaySingle.flatMap(this::toGatewayDetail).map(HttpResponse::created);
	}

	@Override
	public Single<HttpResponse<GatewayDetailVO>> updateGateway(UUID id, GatewayUpdateVO vo) {

		// get tenant from database

		var changed = new AtomicBoolean(false);
		var gatewaySingle = gatewayRepository.findById(id)
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
		if (vo.getGroups() != null) {
			var newGroupIds = Set.copyOf(vo.getGroups());
			gatewaySingle = gatewaySingle.flatMap(gateway -> gatewayGroupRepository
					.findGroupIdByGatewayIdOrderByGatewayId(id)
					.toList()
					.flatMap(oldGroupIds -> {

						// remove group

						var removedGroups = oldGroupIds.stream()
								.filter(groupId -> !newGroupIds.contains(groupId))
								.map(groupId -> new GatewayGroupPK(id, groupId))
								.collect(Collectors.toSet());
						var removeCompletable = Completable.concat(removedGroups.stream()
								.map(gatewayGroupRepository::deleteById)
								.collect(Collectors.toList()));

						// assign group

						var addedGroups = newGroupIds.stream()
								.filter(groupId -> !oldGroupIds.contains(groupId))
								.map(groupId -> new GatewayGroup().setPk(new GatewayGroupPK(id, groupId)))
								.collect(Collectors.toSet());
						var addCompletable = addedGroups.isEmpty()
								? Completable.complete()
								: validateGroups(gateway.getTenant(), newGroupIds)
										.andThen(gatewayGroupRepository.saveAll(addedGroups).ignoreElements());

						changed.set(!addedGroups.isEmpty() || !removedGroups.isEmpty());

						return removeCompletable.andThen(addCompletable).toSingleDefault(gateway);
					}));
		}

		// return updated

		return gatewaySingle
				.flatMap(gateway -> changed.get() ? gatewayRepository.update(gateway) : Single.just(gateway))
				.flatMap(this::toGatewayDetail)
				.map(HttpResponse::ok);
	}

	@Override
	public Single<HttpResponse<Object>> deleteGateway(UUID id) {
		return gatewayRepository.findById(id)
				.doOnComplete(() -> {
					log.trace("Skip deletion of non existing gateway.");
					throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
				})
				.flatMapSingle(gateway -> gatewayRepository.deleteById(id).toSingle(() -> {
					log.info("Gateway {} deleted.", gateway.getName());
					return HttpResponse.noContent();
				}));
	}

	private Completable validateGroups(Tenant tenant, Set<UUID> groups) {
		return Flowable.fromIterable(groups).flatMapSingle(id -> groupRepository.findById(id)
				.doOnComplete(() -> {
					log.trace("Group " + id + " not found.");
					throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Group " + id + " not found.");
				})
				.doOnSuccess(group -> {
					if (!Objects.equals(tenant.getId(), group.getTenant().getId())) {
						throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Group " + id + " not found.");
					}
				})
				.toSingle()).ignoreElements();
	}

	private Single<GatewayDetailVO> toGatewayDetail(Gateway gateway) {
		return gatewayPropertyRepository
				.findByGatewayId(gateway.getId()).toList()
				.flatMap(properties -> gatewayGroupRepository
						.findGroupIdByGatewayIdOrderByGatewayId(gateway.getId())
						.toList()
						.map(groups -> mapper.toGatewayDetail(gateway, properties, groups)));
	}
}
