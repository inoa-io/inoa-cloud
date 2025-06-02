package io.inoa.measurement.things.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import io.inoa.measurement.things.domain.ThingType;
import io.inoa.rest.ThingTypeCreateVO;
import io.inoa.rest.ThingTypeUpdateVO;
import io.inoa.rest.ThingTypeVO;

@Mapper(uses = { MeasurandTypeMapper.class,
		ThingConfigurationMapper.class }, componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface ThingTypeMapper {

	@Mapping(source = "measurandTypes", target = "measurands")
	@Mapping(source = "thingConfigurations", target = "configurations")
	@Mapping(target = "removeMeasurandsItem", ignore = true)
	@Mapping(target = "removeConfigurationsItem", ignore = true)
	ThingTypeVO toThingTypeVO(ThingType thingType);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "measurandTypes", ignore = true)
	@Mapping(source = "configurations", target = "thingConfigurations")
	ThingType toThingType(ThingTypeCreateVO thingTypeCreateVO);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "identifier", ignore = true)
	@Mapping(target = "measurandTypes", ignore = true)
	@Mapping(source = "configurations", target = "thingConfigurations")
	ThingType toThingType(ThingTypeUpdateVO thingTypeUpdateVO);
}
