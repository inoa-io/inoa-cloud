package io.inoa.measurement.things.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import io.inoa.measurement.things.domain.ThingConfiguration;
import io.inoa.rest.ThingConfigurationVO;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface ThingConfigurationMapper {

	ThingConfigurationVO toThingConfigurationVO(ThingConfiguration thingConfiguration);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "thingType", ignore = true)
	ThingConfiguration toThingConfiguration(ThingConfigurationVO thingConfigurationVO);
}
