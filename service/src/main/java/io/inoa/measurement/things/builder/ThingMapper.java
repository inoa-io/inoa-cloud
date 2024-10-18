package io.inoa.measurement.things.builder;

import io.inoa.measurement.things.domain.Property;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.rest.PropertyVO;
import io.inoa.rest.ThingCreateVO;
import io.inoa.rest.ThingPageVO;
import io.inoa.rest.ThingVO;
import io.micronaut.data.model.Page;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.JAKARTA)
public interface ThingMapper {

  @Mapping(target = "removeConfigItem", ignore = true)
  @Mapping(source = "thingId", target = "id")
  @Mapping(source = "thingType.thingTypeId", target = "thingTypeId")
  ThingVO toThingVO(Thing thing);

  @Mapping(target = "removeContentItem", ignore = true)
  ThingPageVO toThingPage(Page<Thing> page);

  @Mapping(target = "tenantId", ignore = true)
  @Mapping(target = "thingType", ignore = true)
  @Mapping(target = "urn", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "updated", ignore = true)
  @Mapping(target = "thingId", ignore = true)
  Thing toThing(ThingCreateVO thingCreateVO);

  Property toProperty(PropertyVO property);

  List<Property> toPropertyList(List<PropertyVO> properties);
}
