package io.inoa.fleet.thing.driver.modbus;

import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.driver.ConfigCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ModbusDVH4013Builder extends ModbusBuilderBase implements ConfigCreator {

	private static final short FUNCTION_CODE_POWER_IN = 0x0000;
	private static final short FUNCTION_CODE_POWER_OUT = 0x0002;
	private static final short FUNCTION_CODE_OBIS_1_8_0 = 0x4001;
	private static final short FUNCTION_CODE_OBIS_2_8_0 = 0x4101;
	private static final Map<String, RegisterSetting> MAPPINGS;
	private final ObjectMapper objectMapper;

	static {
		MAPPINGS = new HashMap<>();
		MAPPINGS.put("power_in", new RegisterSetting((byte) 3, FUNCTION_CODE_POWER_IN, (short) 2));
		MAPPINGS.put("power_out", new RegisterSetting((byte) 3, FUNCTION_CODE_POWER_OUT, (short) 2));
		MAPPINGS.put("obis_1_8_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_8_0, (short) 2));
		MAPPINGS.put("obis_2_8_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_2_8_0, (short) 2));

	}

	@Override
	public ArrayNode build(Thing thing, ThingType thingType) throws JsonProcessingException {
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		String hex = String.format("%02d", abs(serial) % 100);
		int slaveId = Integer.parseInt(hex, 16) + 1;
		if (slaveId > 255) {
			log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, serial);
		}
		return toDatapoints(thing, thingType, slaveId, MAPPINGS);
	}

	@Override
	public ArrayNode buildLegacy(Thing thing, ThingType thingType) {
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		String hex = String.format("%02d", abs(serial) % 100);
		int slaveId = Integer.parseInt(hex, 16) + 1;

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
