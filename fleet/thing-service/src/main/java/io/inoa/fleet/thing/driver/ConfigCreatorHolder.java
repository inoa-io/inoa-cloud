package io.inoa.fleet.thing.driver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.driver.modbus.DVZEDvModbusIRBuilder;
import io.inoa.fleet.thing.driver.modbus.DvModbusIRBuilder;
import io.inoa.fleet.thing.driver.modbus.ModbusDVH4013Builder;
import io.inoa.fleet.thing.driver.modbus.ModbusMDVH4006Builder;
import io.inoa.fleet.thing.driver.rest.ShellyBuilder;
import jakarta.inject.Singleton;

@Singleton
public class ConfigCreatorHolder {

	private final Map<String, ConfigCreator> creators = new HashMap<>();

	public ConfigCreatorHolder(ObjectMapper objectMapper) {
		creators.put("dvmodbusir-dvze", new DVZEDvModbusIRBuilder(objectMapper));
		creators.put("dvmodbusir", new DvModbusIRBuilder(objectMapper));
		creators.put("dvh4013", new ModbusDVH4013Builder(objectMapper));
		creators.put("mdvh4006", new ModbusMDVH4006Builder(objectMapper));
		creators.put("shplg-s", new ShellyBuilder(objectMapper));
		creators.put("shellyht", new ShellyBuilder(objectMapper));
		creators.put("s0", new S0Builder(objectMapper));
	}

	public Optional<ConfigCreator> getConfigCreator(ThingType thingType) {
		return Optional.of(creators.get(thingType.getThingTypeReference()));
	}
}
