package io.kokuwa.fleet.registry.rest.management;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayGroup;
import io.kokuwa.fleet.registry.domain.GatewayGroupRepository;
import io.kokuwa.fleet.registry.domain.GatewayPropertyRepository;
import io.kokuwa.fleet.registry.domain.GatewayRepository;
import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.GroupRepository;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.rest.RestMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link GatewaysApi}.
 *
 * @author Stephan Schnabel
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class GatewaysController implements GatewaysApi {

	private final SecurityManagement security;
	private final RestMapper mapper;
	private final GroupRepository groupRepository;
	private final GatewayRepository gatewayRepository;
	private final GatewayGroupRepository gatewayGroupRepository;
	private final GatewayPropertyRepository gatewayPropertyRepository;

	@Override
	public HttpResponse<List<GatewayVO>> findGateways() {
		return HttpResponse.ok(mapper.toGateways(gatewayRepository.findByTenantOrderByName(security.getTenant())));
	}

	@Override
	public HttpResponse<GatewayDetailVO> findGateway(UUID gatewayId) {
		return HttpResponse.ok(toGatewayDetail(getGateway(gatewayId)));
	}

	@Override
	public HttpResponse<GatewayDetailVO> createGateway(GatewayCreateVO vo) {
		var tenant = security.getTenant();

		// check name for uniqueness

		if (gatewayRepository.existsByTenantAndName(tenant, vo.getName())) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "Name already exists.");
		}

		// check groups for existence

		var groupIds = Optional.ofNullable(vo.getGroupIds()).map(Set::copyOf).orElseGet(Set::of);
		var groups = groupIds.isEmpty() ? List.<Group>of() : getGroups(tenant, groupIds);

		// create gateway

		var gateway = gatewayRepository.save(new Gateway()
				.setTenant(tenant)
				.setGatewayId(UUID.randomUUID())
				.setName(vo.getName())
				.setEnabled(vo.getEnabled())
				.setGroups(groups));

		// create group assignments

		if (!groupIds.isEmpty()) {
			gatewayGroupRepository.saveAll(gateway.getGroups().stream()
					.map(group -> new GatewayGroup().setGateway(gateway).setGroup(group))
					.collect(Collectors.toSet()));
		}

		log.info("Gateway created: {}", gateway);
		return HttpResponse.created(mapper.toGatewayDetail(gateway));
	}

	@Override
	public HttpResponse<GatewayDetailVO> updateGateway(UUID gatewayId, GatewayUpdateVO vo) {

		var gateway = getGateway(gatewayId);
		var changed = false;

		if (vo.getName() != null) {
			if (gateway.getName().equals(vo.getName())) {
				log.trace("Gateway {}: skip update of name because not changed.", gateway.getName());
			} else {
				if (gatewayRepository.existsByTenantAndName(gateway.getTenant(), vo.getName())) {
					throw new HttpStatusException(HttpStatus.CONFLICT, "Name already exists.");
				}
				log.info("Tenant {}: updated name to {}.", gateway.getName(), vo.getName());
				changed = true;
				gateway.setName(vo.getName());
			}
		}

		if (vo.getEnabled() != null) {
			if (gateway.getEnabled() == vo.getEnabled()) {
				log.trace("Skip update of enabled {} because not changed.", gateway.getEnabled());
			} else {
				log.info("Gateway {}: updated enabled to {}.", gateway.getName(), vo.getEnabled());
				changed = true;
				gateway.setEnabled(vo.getEnabled());
			}
		}

		if (vo.getGroupIds() != null) {

			var oldGroups = gatewayGroupRepository.findGroupByGateway(gateway);
			var newGroups = getGroups(gateway.getTenant(), vo.getGroupIds());
			var oldGroupIds = oldGroups.stream().map(Group::getGroupId).collect(Collectors.toSet());
			var newGroupIds = vo.getGroupIds();

			// remove group

			var removedGroups = oldGroups.stream()
					.filter(oldGroup -> !newGroupIds.contains(oldGroup.getGroupId()))
					.collect(Collectors.toSet());
			removedGroups.forEach(group -> gatewayGroupRepository.deleteByGatewayAndGroup(gateway, group));

			// add group

			var addedGroups = newGroups.stream()
					.filter(newGroup -> !oldGroupIds.contains(newGroup.getGroupId()))
					.map(newGroup -> new GatewayGroup().setGateway(gateway).setGroup(newGroup))
					.collect(Collectors.toSet());
			if (!addedGroups.isEmpty()) {
				gatewayGroupRepository.saveAll(addedGroups);
			}

			changed |= !addedGroups.isEmpty() || !removedGroups.isEmpty();
		}

		if (changed) {
			gatewayRepository.update(gateway);
		}
		return HttpResponse.ok(toGatewayDetail(gateway));
	}

	@Override
	public HttpResponse<Object> deleteGateway(UUID gatewayId) {
		var gateway = getGateway(gatewayId);
		gatewayRepository.delete(gateway);
		log.info("Gateway {} deleted.", gateway.getName());
		return HttpResponse.noContent();
	}

	private Gateway getGateway(UUID gatewayId) {
		var optional = gatewayRepository.findByTenantAndGatewayId(security.getTenant(), gatewayId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}
		return optional.get();
	}

	private List<Group> getGroups(Tenant tenant, Set<UUID> groupIds) {
		return groupIds.stream()
				.map(groupId -> groupRepository
						.findByTenantAndGroupId(tenant, groupId)
						.orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST,
								"Group " + groupId + " not found.")))
				.collect(Collectors.toList());
	}

	private GatewayDetailVO toGatewayDetail(Gateway gateway) {
		return mapper.toGatewayDetail(gateway
				.setProperties(gatewayPropertyRepository.findByGateway(gateway))
				.setGroups(gatewayGroupRepository.findGroupByGateway(gateway)));
	}
}
