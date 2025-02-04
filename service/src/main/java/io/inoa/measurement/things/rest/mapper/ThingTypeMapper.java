package io.inoa.measurement.things.rest.mapper;

import io.inoa.measurement.things.domain.ThingType;
import io.inoa.rest.ThingTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    uses = {MeasurandTypeMapper.class, ThingConfigurationMapper.class},
    componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface ThingTypeMapper {

  @Mapping(source = "measurandTypes", target = "measurands")
  @Mapping(source = "thingConfigurations", target = "configurations")
  @Mapping(target = "removeMeasurandsItem", ignore = true)
  @Mapping(target = "removeConfigurationsItem", ignore = true)
  ThingTypeVO toThingTypeVO(ThingType thingType);
}
