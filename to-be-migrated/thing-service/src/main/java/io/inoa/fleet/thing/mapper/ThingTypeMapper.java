package io.inoa.fleet.thing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.rest.management.ThingTypeCreateVO;
import io.inoa.fleet.thing.rest.management.ThingTypeDetailVO;
import io.inoa.fleet.thing.rest.management.ThingTypePageVO;
import io.inoa.fleet.thing.rest.management.ThingTypeVO;
import io.micronaut.data.model.Page;

@Mapper(componentModel = ComponentModel.JAKARTA)
public interface ThingTypeMapper {

	@Mapping(source = "thingTypeId", target = "id")
	ThingTypeVO toThingTypeVO(ThingType thingType);

	@Mapping(source = "thingTypeId", target = "id")
	ThingTypeDetailVO toThingTypeDetailVO(ThingType thingType);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "updated", ignore = true)
	@Mapping(target = "thingTypeId", ignore = true)
	ThingType toThingType(ThingTypeCreateVO thingTypeVO);

	@Mapping(source = "id", target = "thingTypeId")
	@Mapping(target = "id", ignore = true)
	ThingType toThingType(ThingTypeVO thingTypeVO);

	ThingTypePageVO toThingTypePage(Page<ThingType> page);
}
