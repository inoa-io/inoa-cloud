package io.inoa.measurement.things.rest.mapper;

import io.inoa.measurement.things.domain.Thing;
import io.inoa.rest.ThingVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    uses = {ThingTypeMapper.class, MeasurandMapper.class, ThingConfigurationValueMapper.class},
    componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface ThingMapper {

  @Mapping(source = "thingId", target = "id")
  @Mapping(source = "gateway.gatewayId", target = "gatewayId")
  @Mapping(source = "thingType.identifier", target = "thingTypeId")
  @Mapping(source = "thingConfigurationValues", target = "configurations")
  @Mapping(target = "removeMeasurandsItem", ignore = true)
  @Mapping(target = "removeConfigurationsItem", ignore = true)
  ThingVO toThingVO(Thing thing);
}
