package io.inoa.fleet.registry.rest.mapper;

import io.inoa.fleet.registry.domain.GatewayLocationData;
import io.inoa.rest.GatewayLocationDataVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

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
