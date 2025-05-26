package io.inoa.fleet.registry.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.registry.domain.GatewayLocationData;
import io.inoa.rest.GatewayLocationDataVO;

/**
 * Mapper for {@link GatewayLocationData}.
 *
 * @author Ronny Schlegel
 */
@Mapper(componentModel = ComponentModel.JAKARTA)
public interface LocationMapper {

	GatewayLocationDataVO toLocationVO(GatewayLocationData location);

	GatewayLocationData toLocation(GatewayLocationDataVO vo);
}
