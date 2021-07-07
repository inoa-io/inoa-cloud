package io.kokuwa.fleet.registry.rest.management;

import java.util.ArrayList;
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
import io.kokuwa.fleet.registry.domain.GatewayProperty;
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
	public HttpResponse<List<GatewayVO>> getGateways(Optional<UUID> tenantId, Optional<UUID> group) {
		List<Gateway> gateways;
		if (tenantId.isPresent()) {
			gateways = gatewayRepository.findByTenantExternalIdOrderByName(tenantId.get());
		} else {
			gateways = gatewayRepository.findAllOrderByName();
		}
		return HttpResponse.ok(gateways.stream().map(mapper::toGateway).collect(Collectors.toList()));
	}

	@Override
	public HttpResponse<GatewayDetailVO> getGateway(UUID gatewayId) {
		var optionalGateway = gatewayRepository.findByExternalId(gatewayId);
		if (optionalGateway.isEmpty()) {
			log.trace("Gateway not found.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}
		return HttpResponse.ok(toGatewayDetail(optionalGateway.get()));
	}

	@Override
	public HttpResponse<GatewayDetailVO> createGateway(GatewayCreateVO vo) {

		// get tenant

		var optionalTenant = tenantRepository.findByExternalId(vo.getTenantId());
		if (optionalTenant.isEmpty()) {
			log.trace("Tenant not found.");
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Tenant not found.");
		}

		// check name for uniqueness

		Boolean existsByTenantAndName = gatewayRepository.existsByTenantAndName(optionalTenant.get(), vo.getName());
		if (existsByTenantAndName) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
		}

		// check groups for existence

		var groupIds = Optional.ofNullable(vo.getGroupIds()).map(Set::copyOf).orElseGet(Set::of);
		var groups = groupIds.isEmpty() ? List.<Group>of() : getGroups(optionalTenant.get(), groupIds);

		// create gateway

		var gateway = new Gateway().setTenant(optionalTenant.get()).setName(vo.getName()).setEnabled(vo.getEnabled())
				.setGroups(groups);

		gateway = gatewayRepository.save(gateway);

		// .doOnSuccess(gateway -> log.info("Created gateway: {}", gateway));

		// create group assignments

		if (!groupIds.isEmpty()) {
			Gateway finalGateway = gateway;
			gatewayGroupRepository.saveAll(gateway.getGroups().stream()
					.map(group -> new GatewayGroup().setPk(new GatewayGroupPK(finalGateway, group)))
					.collect(Collectors.toSet()));
		}

		// return
		return HttpResponse.created(mapper.toGatewayDetail(gateway));
	}

	@Override
	public HttpResponse<GatewayDetailVO> updateGateway(UUID gatewayId, GatewayUpdateVO vo) {

		// get tenant from database

		var changed = new AtomicBoolean(false);
		var optionalGateway = gatewayRepository.findByExternalId(gatewayId);

		if (optionalGateway.isEmpty()) {
			log.trace("Skip update of non existing gateway.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}

		// update fields

		var gateway = optionalGateway.get();
		if (vo.getName() != null) {
			if (gateway.getName().equals(vo.getName())) {
				log.trace("Gateway {}: skip update of name because not changed.", gateway.getName());
			} else {
				Boolean existsByTenantAndName = gatewayRepository.existsByTenantAndName(gateway.getTenant(),
						vo.getName());

				if (existsByTenantAndName) {
					throw new HttpStatusException(HttpStatus.CONFLICT, "Already exists.");
				}
				log.info("Tenant {}: updated name to {}.", gateway.getName(), vo.getName());
				changed.set(true);
				gateway.setName(vo.getName());
			}
		}
		if (vo.getEnabled() != null) {
			if (gateway.getEnabled() == vo.getEnabled()) {
				log.trace("Skip update of enabled {} because not changed.", gateway.getEnabled());
			} else {
				log.info("Gateway {}: updated enabled to {}.", gateway.getName(), vo.getEnabled());
				changed.set(true);
				gateway.setEnabled(vo.getEnabled());
			}
		}
		if (vo.getGroupIds() != null) {
			var oldGroups = gatewayGroupRepository.findGroupsByGatewayId(gateway.getId());
			var newGroups = getGroups(gateway.getTenant(), vo.getGroupIds());

			var oldGroupIds = oldGroups.stream().map(Group::getExternalId).collect(Collectors.toSet());
			var newGroupIds = vo.getGroupIds();

			// remove group

			Gateway finalGateway = gateway;
			var removedGroups = oldGroups.stream().filter(oldGroup -> !newGroupIds.contains(oldGroup.getExternalId()))
					.map(oldGroup -> new GatewayGroupPK(finalGateway, oldGroup)).collect(Collectors.toSet());
			removedGroups.forEach(gatewayGroupRepository::deleteById);

			// add group

			var addedGroups = newGroups.stream().filter(newGroup -> !oldGroupIds.contains(newGroup.getExternalId()))
					.map(newGroup -> new GatewayGroup().setPk(new GatewayGroupPK(finalGateway, newGroup)))
					.collect(Collectors.toSet());
			if (!addedGroups.isEmpty()) {
				gatewayGroupRepository.saveAll(addedGroups);
			}

			changed.set(!addedGroups.isEmpty() || !removedGroups.isEmpty());
		}

		// return updated
		if (changed.get()) {
			gateway = gatewayRepository.update(gateway);
		}
		return HttpResponse.ok(toGatewayDetail(gateway));
	}

	@Override
	public HttpResponse<Object> deleteGateway(UUID gatewayId) {
		var optionalGateway = gatewayRepository.findByExternalId(gatewayId);
		if (optionalGateway.isEmpty()) {
			log.trace("Skip deletion of non existing gateway.");
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}
		gatewayRepository.delete(optionalGateway.get());
		log.info("Gateway {} deleted.", optionalGateway.get().getName());
		return HttpResponse.noContent();
	}

	private List<Group> getGroups(Tenant tenant, Set<UUID> groupIds) {

		var result = new ArrayList<Group>();
		for (var groupId : groupIds) {
			Optional<Group> optionalGroup = groupRepository.findByExternalId(groupId);
			if (optionalGroup.isEmpty()) {
				throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Group " + groupId + " not found.");
			}
			if (!Objects.equals(tenant.getExternalId(), optionalGroup.get().getTenant().getExternalId())) {
				throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Group " + groupId + " not found.");
			}
			result.add(optionalGroup.get());
		}
		return result;
	}

	private GatewayDetailVO toGatewayDetail(Gateway gateway) {
		List<GatewayProperty> gatewayProperties = gatewayPropertyRepository.findByGatewayId(gateway.getId());
		gateway.setProperties(gatewayProperties);
		List<Group> gatewayGroups = gatewayGroupRepository.findGroupsByGatewayId(gateway.getId());
		gateway.setGroups(gatewayGroups);
		return mapper.toGatewayDetail(gateway);
	}
}
