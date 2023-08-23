package io.inoa.measurement.things.builder;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.measurement.model.ThingTypeCreateVO;
import io.inoa.measurement.model.ThingTypePageVO;
import io.inoa.measurement.model.ThingTypeVO;
import io.inoa.measurement.things.domain.ThingType;
import io.micronaut.data.model.Page;

@Mapper(componentModel = ComponentModel.JAKARTA)
public interface ThingTypeMapper {

	ThingTypeVO toThingTypeVO(ThingType thingType);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "updated", ignore = true)
	ThingType toThingType(ThingTypeCreateVO thingTypeVO);

	@Mapping(target = "id", ignore = true)
	ThingType toThingType(ThingTypeVO thingTypeVO);

	ThingTypePageVO toThingTypePage(Page<ThingType> page);
}
