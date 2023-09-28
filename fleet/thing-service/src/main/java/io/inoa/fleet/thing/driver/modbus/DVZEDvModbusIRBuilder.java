package io.inoa.fleet.thing.driver.modbus;

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
public class DVZEDvModbusIRBuilder extends ModbusBuilderBase implements ConfigCreator {

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
		Integer dvModbusIrSerial = (Integer) properties.get("dvmodbusir-serial");
		int slaveId = dvModbusIrSerial % 1000;
		if (slaveId > 255) {
			log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, dvModbusIrSerial);
		}
		return toDatapoints(thing, thingType, slaveId, MAPPINGS);
	}

	@Override
	public ArrayNode buildLegacy(Thing thing, ThingType thingType) {
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer dvModbusIrSerial = (Integer) properties.get("dvmodbusir-serial");
		int slaveId = dvModbusIrSerial % 1000;
		if (slaveId > 255) {
			log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, dvModbusIrSerial);
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
