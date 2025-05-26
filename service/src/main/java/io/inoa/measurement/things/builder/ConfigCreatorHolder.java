package io.inoa.measurement.things.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.measurement.things.builder.http.ShellyBuilder;
import io.inoa.measurement.things.builder.modbus.DvModbusIRBuilder;
import io.inoa.measurement.things.builder.modbus.ModbusDVH4013Builder;
import io.inoa.measurement.things.builder.modbus.ModbusMDVH4006Builder;
import io.inoa.measurement.things.builder.s0.S0Builder;
import io.inoa.measurement.things.domain.ThingType;

@Singleton
public class ConfigCreatorHolder {

	private final Map<String, ConfigCreator> creators = new HashMap<>();

	public ConfigCreatorHolder(ObjectMapper objectMapper) {
		creators.put("dvmodbusir", new DvModbusIRBuilder(objectMapper));
		creators.put("dvh4013", new ModbusDVH4013Builder(objectMapper));
		creators.put("mdvh4006", new ModbusMDVH4006Builder(objectMapper));
		creators.put("shplg-s", new ShellyBuilder(objectMapper));
		creators.put("shellyht", new ShellyBuilder(objectMapper));
		creators.put("s0", new S0Builder(objectMapper));
	}

	public Optional<ConfigCreator> getConfigCreator(ThingType thingType) {
		return Optional.of(creators.get(thingType.getThingTypeId()));
	}
}
