package io.inoa.measurement.things.builder.modbus;

import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.measurement.things.builder.ConfigCreator;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ModbusMDVH4006Builder extends ModbusBuilderBase implements ConfigCreator {

	private static final short FUNCTION_CODE_POWER_IN = 0x0000;
	private static final short FUNCTION_CODE_POWER_OUT = 0x0002;
	private static final short FUNCTION_CODE_OBIS_1_8_0 = 0x4000;
	private static final short FUNCTION_CODE_OBIS_2_8_0 = 0x4100;

	private static final Map<String, RegisterSetting> MAPPINGS;

	static {
		MAPPINGS = new HashMap<>();
		MAPPINGS.put("power_in", new RegisterSetting((byte) 3, FUNCTION_CODE_POWER_IN, (short) 2));
		MAPPINGS.put("power_out", new RegisterSetting((byte) 3, FUNCTION_CODE_POWER_OUT, (short) 2));
		MAPPINGS.put("obis_1_8_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_8_0, (short) 2));
		MAPPINGS.put("obis_2_8_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_2_8_0, (short) 2));
	}

	private final ObjectMapper objectMapper;

	@Override
	public ArrayNode build(Thing thing, ThingType thingType) {
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		String hex = String.format("%02d", abs(serial + 1) % 100);
		int slaveId = Integer.parseInt(hex, 16);
		if (slaveId > 255) {
			log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, serial);
		}
		return toDatapoints(thing, thingType, slaveId, MAPPINGS);
	}

	@Override
	public ArrayNode buildLegacy(Thing thing, ThingType thingType) {
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		String hex = String.format("%02d", abs(serial + 1) % 100);
		int slaveId = Integer.parseInt(hex, 16);
		if (slaveId > 255) {
			log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, serial);
		}
		return toDatapointsLegacy(thing, thingType, slaveId, MAPPINGS);
	}

	@Override
	public ArrayNode buildRPC(Thing thing, ThingType thingType) {
		return null;
	}

	@Override
	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
