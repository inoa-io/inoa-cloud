package io.inoa.fleet.thing.rest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.modbus.ConfigCreator;
import io.inoa.fleet.thing.modbus.DvModbusIRBuilder;
import io.inoa.fleet.thing.modbus.ModbusDVH4013Builder;
import jakarta.inject.Singleton;

@Singleton
public class ConfigCreatorHolder {

	private Map<String, ConfigCreator> creators = new HashMap<>();

	public ConfigCreatorHolder(ObjectMapper objectMapper) {
		creators.put("dvmodbusir", new DvModbusIRBuilder(objectMapper));
		creators.put("dvh4013", new ModbusDVH4013Builder(objectMapper));
		// shplg-s
		// shellyht
		// s0
	}

	public ConfigCreator getConfigCreator(ThingType thingType) {
		return creators.get(thingType.getThingTypeId());
	}
}
