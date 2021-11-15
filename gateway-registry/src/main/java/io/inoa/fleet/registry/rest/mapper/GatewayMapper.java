package io.inoa.fleet.registry.rest.mapper;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayGroup;
import io.inoa.fleet.registry.domain.GatewayProperty;
import io.inoa.fleet.registry.rest.management.GatewayDetailVO;
import io.inoa.fleet.registry.rest.management.GatewayPageVO;
import io.micronaut.data.model.Page;

/**
 * Mapper for {@link Gateway}, {@link GatewayProperty} and {@link GatewayGroup}.
 *
 * @author Stephan Schnabel
 */
@Mapper(uses = GroupMapper.class)
public interface GatewayMapper {

	GatewayPageVO toGatewayPage(Page<Gateway> gateways);

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
