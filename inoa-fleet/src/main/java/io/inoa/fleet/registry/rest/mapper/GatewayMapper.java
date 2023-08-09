package io.inoa.fleet.registry.rest.mapper;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.model.GatewayDetailVO;
import io.inoa.fleet.model.GatewayPageVO;
import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayGroup;
import io.inoa.fleet.registry.domain.GatewayProperty;
import io.micronaut.data.model.Page;

/**
 * Mapper for {@link Gateway}, {@link GatewayProperty} and {@link GatewayGroup}.
 *
 * @author Stephan Schnabel
 */
@Mapper(uses = GroupMapper.class, componentModel = ComponentModel.JAKARTA)
public interface GatewayMapper {

	@Mapping(target = "removeContentItem", ignore = true)
	GatewayPageVO toGatewayPage(Page<Gateway> gateways);

	@Mapping(target = "groupIds", source = "groups")
	@Mapping(target = "removeGroupIdsItem", ignore = true)
	@Mapping(target = "removePropertiesItem", ignore = true)
	GatewayDetailVO toGatewayDetail(Gateway gateway);

	default Map<String, String> toMap(List<GatewayProperty> properties) {
		return properties == null
				? Map.of()
				: properties.stream().collect(Collectors.toMap(
						GatewayProperty::getKey,
						GatewayProperty::getValue,
						(property1, property2) -> property1, TreeMap::new));
	}
}
