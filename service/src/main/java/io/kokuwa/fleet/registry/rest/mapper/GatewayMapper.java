package io.kokuwa.fleet.registry.rest.mapper;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import io.kokuwa.fleet.registry.domain.Gateway;
import io.kokuwa.fleet.registry.domain.GatewayGroup;
import io.kokuwa.fleet.registry.domain.GatewayProperty;
import io.kokuwa.fleet.registry.rest.management.GatewayDetailVO;
import io.kokuwa.fleet.registry.rest.management.GatewayVO;

/**
 * Mapper for {@link Gateway}, {@link GatewayProperty} and {@link GatewayGroup}.
 *
 * @author Stephan Schnabel
 */
@Mapper(componentModel = ComponentModel.JSR330, uses = GroupMapper.class)
public interface GatewayMapper {

	List<GatewayVO> toGateways(List<Gateway> gateways);

	@Mapping(target = "groupIds", source = "groups")
	GatewayDetailVO toGatewayDetail(Gateway gateway);

	default Map<String, String> toMap(List<GatewayProperty> properties) {
		return properties == null ? Map.of()
				: properties.stream().collect(Collectors.toMap(
						GatewayProperty::getKey,
						GatewayProperty::getValue,
						(property1, property2) -> property1, TreeMap::new));
	}
}
