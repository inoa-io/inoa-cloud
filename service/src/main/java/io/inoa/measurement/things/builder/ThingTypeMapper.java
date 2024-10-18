package io.inoa.measurement.things.builder;

import io.inoa.measurement.things.domain.ThingType;
import io.inoa.rest.ThingTypeCreateVO;
import io.inoa.rest.ThingTypePageVO;
import io.inoa.rest.ThingTypeVO;
import io.micronaut.data.model.Page;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.JAKARTA)
public interface ThingTypeMapper {

  @Mapping(target = "removeJsonSchemaItem", ignore = true)
  @Mapping(target = "channels", ignore = true)
  @Mapping(target = "removeChannelsItem", ignore = true)
  @Mapping(target = "properties", ignore = true)
  @Mapping(target = "removePropertiesItem", ignore = true)
  @Mapping(target = "id", source = "thingTypeId")
  ThingTypeVO toThingTypeVO(ThingType thingType);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "updated", ignore = true)
  ThingType toThingType(ThingTypeCreateVO thingTypeVO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "thingTypeId", ignore = true)
  ThingType toThingType(ThingTypeVO thingTypeVO);

  @Mapping(target = "removeContentItem", ignore = true)
  ThingTypePageVO toThingTypePage(Page<ThingType> page);
}
