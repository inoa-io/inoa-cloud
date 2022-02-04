package io.inoa.fleet.thing;

import org.mapstruct.factory.Mappers;

import io.inoa.fleet.thing.mapper.ThingMapper;
import io.inoa.fleet.thing.mapper.ThingTypeChannelMapper;
import io.inoa.fleet.thing.mapper.ThingTypeMapper;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

/**
 * Factory for application.
 */
@Factory
public class ApplicationFactory {

	@Singleton
	ThingMapper thingMapper() {
		return Mappers.getMapper(ThingMapper.class);
	}

	@Singleton
	ThingTypeChannelMapper thingTypeChannelMapper() {
		return Mappers.getMapper(ThingTypeChannelMapper.class);
	}

	@Singleton
	ThingTypeMapper thingTypeMapper() {
		return Mappers.getMapper(ThingTypeMapper.class);
	}
}
