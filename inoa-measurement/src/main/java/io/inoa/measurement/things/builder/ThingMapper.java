package io.inoa.measurement.things.builder;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

import io.inoa.measurement.model.PropertyVO;
import io.inoa.measurement.model.ThingCreateVO;
import io.inoa.measurement.model.ThingDetailVO;
import io.inoa.measurement.model.ThingPageVO;
import io.inoa.measurement.model.ThingVO;
import io.inoa.measurement.things.domain.Property;
import io.inoa.measurement.things.domain.Thing;
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
