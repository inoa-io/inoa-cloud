package io.inoa.measurement.things.rest.mapper;

import io.inoa.measurement.things.domain.ThingConfigurationValue;
import io.inoa.rest.ThingConfigurationsInnerVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    uses = {ThingConfigurationMapper.class},
    componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface ThingConfigurationValueMapper {

  @Mapping(source = "thingConfiguration", target = "configuration")
  @Mapping(source = "thingConfiguration", target = "_configuration")
  ThingConfigurationsInnerVO toThingConfigurationsInnerVO(
      ThingConfigurationValue thingConfigurationValue);

  @Mapping(target = "thing", ignore = true)
  @Mapping(source = "configuration", target = "thingConfiguration")
  ThingConfigurationValue toThingConfigurationValue(
      ThingConfigurationsInnerVO thingConfigurationsInnerVO);
}
