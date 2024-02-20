package io.inoa.measurement.things.builder.modbus;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.measurement.things.builder.ConfigCreator;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@Slf4j
@RequiredArgsConstructor
public class DvModbusIRBuilder extends ModbusBuilderBase implements ConfigCreator {

	private static final short FUNCTION_CODE_SERIAL_NUMBER = 0x000B;
	private static final short FUNCTION_CODE_OBIS_1_8_0 = 0x000D;
	private static final short FUNCTION_CODE_OBIS_1_8_1 = 0x0012;
	private static final short FUNCTION_CODE_OBIS_1_8_2 = 0x0017;
	private static final short FUNCTION_CODE_OBIS_2_8_0 = 0x001C;
	private static final short FUNCTION_CODE_OBIS_2_8_1 = 0x0021;
	private static final short FUNCTION_CODE_OBIS_2_8_2 = 0x0026;
	private static final short FUNCTION_CODE_OBIS_1_7_0 = 0x002B;
	private final ObjectMapper objectMapper;

	private static final Map<String, RegisterSetting> MAPPINGS;

	static {
		MAPPINGS = new HashMap<>();
		MAPPINGS.put("obis_1_8_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_8_0, (short) 4));
		MAPPINGS.put("obis_1_8_1", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_8_1, (short) 4));
		MAPPINGS.put("obis_1_8_2", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_8_2, (short) 4));
		MAPPINGS.put("obis_2_8_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_2_8_0, (short) 4));
		MAPPINGS.put("obis_2_8_1", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_2_8_1, (short) 4));
		MAPPINGS.put("obis_2_8_2", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_2_8_2, (short) 4));
		MAPPINGS.put("obis_1_7_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_7_0, (short) 2));
	}

	@Override
	public ArrayNode build(Thing thing, ThingType thingType) throws JsonProcessingException {
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		int slaveId = serial % 1000;
		if (slaveId > 255) {
			log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, serial);
		}
		return toDatapoints(thing, thingType, slaveId, MAPPINGS);
	}

	@SuppressWarnings("removal")
	@Override
	public ArrayNode buildLegacy(Thing thing, ThingType thingType) {
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		int slaveId = serial % 1000;
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
