package io.inoa.fleet.thing.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import io.inoa.fleet.thing.domain.ThingTypeChannel;
import io.inoa.fleet.thing.rest.management.ThingTypeChannelCreateVO;
import io.inoa.fleet.thing.rest.management.ThingTypeChannelVO;

@Mapper(componentModel = MappingConstants.ComponentModel.JSR330)
public interface ThingTypeChannelMapper {

	@Mapping(source = "thingTypeChannelId", target = "id")
	ThingTypeChannelVO toThingTypeChannelVO(ThingTypeChannel thingTypeChannel);

	@Mapping(source = "id", target = "thingTypeChannelId")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "thingType", ignore = true)
	ThingTypeChannel toThingTypeChannel(ThingTypeChannelVO thingTypeChannelVO);

	ThingTypeChannel toThingTypeChannel(ThingTypeChannelCreateVO channel);
}
