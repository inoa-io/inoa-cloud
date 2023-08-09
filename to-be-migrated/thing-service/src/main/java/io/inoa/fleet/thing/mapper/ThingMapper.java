package io.inoa.fleet.thing.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.fleet.thing.domain.Property;
import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.rest.management.PropertyVO;
import io.inoa.fleet.thing.rest.management.ThingCreateVO;
import io.inoa.fleet.thing.rest.management.ThingDetailVO;
import io.inoa.fleet.thing.rest.management.ThingPageVO;
import io.inoa.fleet.thing.rest.management.ThingVO;
import io.micronaut.data.model.Page;

@Mapper(componentModel = ComponentModel.JAKARTA)
public interface ThingMapper {

	@Mapping(source = "thingId", target = "id")
	@Mapping(source = "thingType.thingTypeReference", target = "thingTypeReference")
	ThingVO toThingVO(Thing thing);

	@Mapping(source = "thingId", target = "id")
	@Mapping(source = "thingType.thingTypeReference", target = "thingTypeReference")
	ThingDetailVO toThingDetailVO(Thing thing);

	ThingPageVO toThingPage(Page<Thing> page);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "created", ignore = true)
	@Mapping(target = "updated", ignore = true)
	@Mapping(target = "thingId", ignore = true)
	Thing toThing(ThingCreateVO thingCreateVO);

	Property toProperty(PropertyVO property);

	List<Property> toPropertyList(List<PropertyVO> properties);
}
