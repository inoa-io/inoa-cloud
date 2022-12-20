package io.inoa.fleet.thing.driver;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.driver.modbus.DvModbusIRBuilder;
import io.inoa.fleet.thing.driver.modbus.ModbusDVH4013Builder;
import io.inoa.fleet.thing.driver.rest.ShellyBuilder;
import jakarta.inject.Singleton;

@Singleton
public class ConfigCreatorHolder {

	private final Map<String, ConfigCreator> creators = new HashMap<>();

	public ConfigCreatorHolder(ObjectMapper objectMapper) {
		creators.put("dvmodbusir", new DvModbusIRBuilder(objectMapper));
		creators.put("dvh4013", new ModbusDVH4013Builder(objectMapper));
		creators.put("shplg-s", new ShellyBuilder(objectMapper));
		creators.put("shellyht", new ShellyBuilder(objectMapper));
		creators.put("s0", new S0Builder(objectMapper));
	}

	public ConfigCreator getConfigCreator(ThingType thingType) {
		return creators.get(thingType.getThingTypeReference());
	}
}
