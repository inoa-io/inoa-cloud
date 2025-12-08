package io.inoa.fleet.registry.rest.management;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import io.inoa.fleet.registry.domain.*;
import io.inoa.fleet.registry.rest.mapper.GatewayMapper;
import io.inoa.fleet.registry.rest.mapper.LocationMapper;
import io.inoa.rest.GatewayCreateVO;
import io.inoa.rest.GatewayDetailVO;
import io.inoa.rest.GatewayPageVO;
import io.inoa.rest.GatewayStatusVO;
import io.inoa.rest.GatewayUpdateVO;
import io.inoa.rest.GatewayVO;
import io.inoa.rest.GatewaysApi;
import io.inoa.rest.MoveGatewayRequestVO;
import io.inoa.rest.PageableProvider;
import io.inoa.shared.Security;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.exceptions.HttpStatusException;
import lombok.NonNull;
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
public class GatewaysController extends AbstractManagementController implements GatewaysApi {

	/** Available sort properties, see API spec for documentation. */
	public static final Set<String> SORT_ORDER_PROPERTIES = Set.of(
			GatewayVO.JSON_PROPERTY_NAME,
			GatewayVO.JSON_PROPERTY_ENABLED,
			GatewayVO.JSON_PROPERTY_CREATED,
			GatewayVO.JSON_PROPERTY_UPDATED);

	private final Security security;
	private final GatewayMapper gatewayMapper;
	private final LocationMapper locationMapper;
	private final GroupRepository groupRepository;
	private final GatewayRepository gatewayRepository;
	private final GatewayGroupRepository gatewayGroupRepository;
	private final GatewayPropertyRepository gatewayPropertyRepository;
	private final PageableProvider pageableProvider;

	@Get("/gateways")
	@Override
	public HttpResponse<GatewayPageVO> findGateways(
			@NonNull Optional<Integer> page,
			@NonNull Optional<Integer> size,
			@NonNull Optional<List<String>> sort,
			@NonNull Optional<String> filter) {
		var pageable = pageableProvider.getPageable(SORT_ORDER_PROPERTIES, GatewayVO.JSON_PROPERTY_NAME);
		var gatewayPage = gatewayRepository.findByTenantInList(security.getGrantedTenants(), filter, pageable);
		return HttpResponse.ok(gatewayMapper.toGatewayPage(gatewayPage));
	}

	@Override
	public HttpResponse<GatewayStatusVO> getStatus(String gatewayId) {
		return HttpResponse.status(500, "Not yet implemented!");
	}

	@Override
	public HttpResponse<Object> moveGateway(MoveGatewayRequestVO moveGatewayRequestVO) {
		var tenants = security.getGrantedTenants();
		if (tenants.stream()
				.noneMatch(
						tenant -> tenant.getTenantId().equals(moveGatewayRequestVO.getSourceTenantId()))
				|| tenants.stream()
						.noneMatch(
								tenant -> tenant.getTenantId().equals(moveGatewayRequestVO.getTargetTenantId()))) {
			throw new HttpStatusException(HttpStatus.FORBIDDEN, "Insufficient tenant grants.");
		}
		var gateway = getGateway(
				moveGatewayRequestVO.getGatewayId(),
				Optional.of(moveGatewayRequestVO.getSourceTenantId()));
		var targetTenant = tenants.stream()
				.filter(tenant -> tenant.getTenantId().equals(moveGatewayRequestVO.getTargetTenantId()))
				.findFirst();
		// This should never happen, but - anyway...
		if (targetTenant.isEmpty()) {
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Target tenant does not exist.");
		}
		gateway.setTenant(targetTenant.get());
		gatewayRepository.update(gateway);
		return HttpResponse.ok();
	}

	@Override
	public HttpResponse<GatewayDetailVO> findGateway(
			@NonNull String gatewayId, @NonNull Optional<String> tenantId) {
		return HttpResponse.ok(toGatewayDetail(getGateway(gatewayId, tenantId)));
	}

	@Override
	public HttpResponse<GatewayDetailVO> createGateway(
			@Valid GatewayCreateVO vo, @NonNull Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);

		// check name for uniqueness

		var gatewayIdPattern = tenant.getGatewayIdPattern();
		if (!Pattern.matches(gatewayIdPattern, vo.getGatewayId())) {
			throw new HttpStatusException(
					HttpStatus.BAD_REQUEST, "GatewayId must match " + gatewayIdPattern + ".");
		}
		if (gatewayRepository.findByGatewayId(vo.getGatewayId()).isPresent()) {
			throw new HttpStatusException(HttpStatus.CONFLICT, "GatewayId already exists.");
		}

		// check groups for existence

		var groupIds = Optional.ofNullable(vo.getGroupIds()).map(Set::copyOf).orElseGet(Set::of);
		var groups = groupIds.isEmpty() ? List.<Group>of() : getGroups(tenant, groupIds);

		// create gateway

		var gateway = gatewayRepository.save(
				new Gateway()
						.setTenant(tenant)
						.setGatewayId(vo.getGatewayId())
						.setName(vo.getName())
						.setEnabled(vo.getEnabled())
						.setLocation(locationMapper.toLocation(vo.getLocation()))
						.setGroups(groups)
						.setStatus(
								new GatewayStatus().setMqtt(new GatewayStatusMqtt().setConnected(false))));

		// create group assignments

		if (!groupIds.isEmpty()) {
			gatewayGroupRepository.saveAll(
					gateway.getGroups().stream()
							.map(group -> new GatewayGroup().setGateway(gateway).setGroup(group))
							.collect(Collectors.toSet()));
		}

		log.info("Gateway created: {}", gateway);
		return HttpResponse.created(gatewayMapper.toGatewayDetail(gateway));
	}

	@Override
	public HttpResponse<GatewayDetailVO> updateGateway(
			@NonNull String gatewayId, @Valid GatewayUpdateVO vo, @NonNull Optional<String> tenantId) {

		var gateway = getGateway(gatewayId, tenantId);
		var changed = false;

		if (vo.getName() != null) {
			if (Objects.equals(gateway.getName(), vo.getName())) {
				log.trace("Gateway {}: skip update of name because not changed.", gateway.getName());
			} else {
				log.info("Gateway {}: updated name to {}.", gateway.getName(), vo.getName());
				changed = true;
				gateway.setName(vo.getName());
			}
		}

		if (vo.getEnabled() != null) {
			if (Objects.equals(gateway.getEnabled(), vo.getEnabled())) {
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
			removedGroups.forEach(group -> gatewayGroupRepository.delete(gateway.getId(), group.getId()));

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

		// Update location data
		if (vo.getLocation() != null) {
			gateway.setLocation(locationMapper.toLocation(vo.getLocation()));
			changed = true;
		}

		if (changed) {
			gatewayRepository.update(gateway);
		}
		return HttpResponse.ok(toGatewayDetail(gateway));
	}

	@Override
	public HttpResponse<Object> deleteGateway(
			@NonNull String gatewayId, @NonNull Optional<String> tenantId) {
		var gateway = getGateway(gatewayId, tenantId);
		gatewayRepository.delete(gateway);
		log.info("Gateway {} deleted.", gateway.getName());
		return HttpResponse.noContent();
	}

	private Gateway getGateway(String gatewayId, Optional<String> tenantId) {
		var tenant = resolveAmbiguousTenant(security.getGrantedTenants(), tenantId);
		var optional = gatewayRepository.findByTenantAndGatewayId(tenant, gatewayId);
		if (optional.isEmpty()) {
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Gateway not found.");
		}
		return optional.get().setTenant(tenant);
	}

	private List<Group> getGroups(Tenant tenant, Set<UUID> groupIds) {
		return groupIds.stream()
				.map(
						groupId -> groupRepository
								.findByTenantAndGroupId(tenant, groupId)
								.orElseThrow(
										() -> new HttpStatusException(
												HttpStatus.BAD_REQUEST, "Group " + groupId + " not found.")))
				.toList();
	}

	private GatewayDetailVO toGatewayDetail(Gateway gateway) {
		return gatewayMapper.toGatewayDetail(
				gateway
						.setProperties(gatewayPropertyRepository.findByGateway(gateway))
						.setGroups(gatewayGroupRepository.findGroupByGateway(gateway)));
	}
}
