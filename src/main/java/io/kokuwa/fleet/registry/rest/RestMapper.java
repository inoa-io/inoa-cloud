package io.kokuwa.fleet.registry.rest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.kokuwa.fleet.registry.domain.BaseEntity;
import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.domain.Group;
import io.kokuwa.fleet.registry.domain.Tenant;
import io.kokuwa.fleet.registry.rest.management.GatewayDetailVO;
import io.kokuwa.fleet.registry.rest.management.GatewayVO;
import io.kokuwa.fleet.registry.rest.management.GroupVO;
import io.kokuwa.fleet.registry.rest.management.TenantVO;

@Mapper(componentModel = "jsr330")
public interface RestMapper {

	@Mapping(target = "tenantId", source = "externalId")
	TenantVO toTenant(Tenant tenant);

	@Mapping(target = "tenantId", source = "tenant.externalId")
	@Mapping(target = "groupId", source = "externalId")
	GroupVO toGroup(Group group);

	@Mapping(target = "tenantId", source = "tenant.externalId")
	@Mapping(target = "gatewayId", source = "externalId")
	GatewayVO toGateway(Gateway gateway);

	@Mapping(target = "tenantId", source = "gateway.tenant.externalId")
	@Mapping(target = "gatewayId", source = "externalId")
	@Mapping(target = "groupIds", source = "groups")
	GatewayDetailVO toGatewayDetail(Gateway gateway);

	default UUID toId(BaseEntity entity) {
		return entity.getExternalId();
	}

	default Map<String, String> toMap(List<GatewayProperty> properties) {
		return properties == null ? Map.of()
				: properties.stream().collect(Collectors.toMap(
						property -> property.getPk().getKey(),
						property -> property.getValue(),
						(property1, property2) -> property1, TreeMap::new));
	}
}
