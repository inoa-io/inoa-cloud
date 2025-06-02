package io.inoa.measurement.things.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import io.inoa.measurement.things.domain.Thing;
import io.inoa.rest.ThingCreateVO;
import io.inoa.rest.ThingVO;

@Mapper(uses = { ThingTypeMapper.class,
		MeasurandMapper.class }, componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface ThingMapper {

	@Mapping(source = "thingId", target = "id")
	@Mapping(source = "gateway.gatewayId", target = "gatewayId")
	@Mapping(source = "thingType.identifier", target = "thingTypeId")
	@Mapping(target = "removeMeasurandsItem", ignore = true)
	@Mapping(target = "removeConfigurationsItem", ignore = true)
	@Mapping(target = "configurations", expression = "java(thing.getThingConfigurationValues() == null ? null :"
			+ " thing.getThingConfigurationValues().stream().collect(java.util.stream.Collectors.toMap(t"
			+ " -> t.getThingConfiguration().getName(),"
			+ " io.inoa.measurement.things.domain.ThingConfigurationValue::getValue)))")
	ThingVO toThingVO(Thing thing);

	List<ThingVO> toThingVOs(List<Thing> things);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "thingId", ignore = true)
	@Mapping(target = "gateway", ignore = true)
	@Mapping(target = "thingType", ignore = true)
	@Mapping(target = "thingConfigurationValues", ignore = true)
	Thing toThing(ThingCreateVO thingVO);
}
