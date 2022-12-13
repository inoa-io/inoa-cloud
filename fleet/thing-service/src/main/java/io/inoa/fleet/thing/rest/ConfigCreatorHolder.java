package io.inoa.fleet.thing.rest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.modbus.ConfigCreator;
import io.inoa.fleet.thing.modbus.DvModbusIRBuilder;
import io.inoa.fleet.thing.modbus.InoaSatelliteModbusDVHBuilder;
import jakarta.inject.Singleton;

@Singleton
public class ConfigCreatorHolder {

	private Map<String, ConfigCreator> creators = new HashMap<>();

	public ConfigCreatorHolder(ObjectMapper objectMapper) {
		creators.put("dv_modbus_ir", new DvModbusIRBuilder(objectMapper));
		creators.put("dvh4013", new InoaSatelliteModbusDVHBuilder(objectMapper));
	}

	public ConfigCreator getConfigCreator(ThingType thingType) {
		return creators.get(thingType.getThingTypeId());
	}
}
