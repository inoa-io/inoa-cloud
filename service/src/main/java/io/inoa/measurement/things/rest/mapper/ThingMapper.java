package io.inoa.measurement.things.rest.mapper;

import io.inoa.measurement.things.domain.Thing;
import io.inoa.rest.ThingVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
    uses = {ThingTypeMapper.class},
    componentModel = MappingConstants.ComponentModel.JAKARTA)
public interface ThingMapper {

  @Mapping(source = "thingId", target = "id")
  ThingVO toThingVO(Thing thing);
}
