package io.kokuwa.fleet.registry.rest;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

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

	TenantVO toTenant(Tenant tenant);

	GroupVO toGroup(Group group);

	GatewayVO toGateway(Gateway gateway);

	GatewayDetailVO toGatewayDetail(Gateway gateway, List<GatewayProperty> properties, List<UUID> groups);

	default UUID toId(BaseEntity entity) {
		return entity.getId();
	}

	default Map<String, String> toMap(List<GatewayProperty> properties) {
		return properties.stream().collect(Collectors.toMap(
				property -> property.getPk().getKey(),
				property -> property.getValue(),
				(property1, property2) -> property1, TreeMap::new));
	}
}
