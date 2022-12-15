package io.inoa.fleet.thing.driver.modbus;

import static java.lang.Math.abs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.driver.ConfigCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ModbusDVH4013Builder extends ModbusBuilderBase implements ConfigCreator {

	private static short FUNCTION_CODE_POWER_IN = 0x0000;
	private static short FUNCTION_CODE_POWER_OUT = 0x0002;
	private static short FUNCTION_CODE_OBIS_1_8_0 = 0x4001;
	private static short FUNCTION_CODE_OBIS_2_8_0 = 0x4101;
	private final ObjectMapper objectMapper;

	@Override
	public ArrayNode build(Thing thing, ThingType thingType) {

		ArrayNode datapoints = objectMapper.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		Integer modbusInterface = (Integer) properties.get("modbus_interface");
		Map<String, Object> channels = (Map<String, Object>) thing.getConfig().get("channels");

		String hex = String.format("%02d", abs(serial + 1) % 100);
		int slaveId = Integer.parseInt(hex, 16);
		if (slaveId > 255) {
			log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, serial);
		}
		Map<String, RegisterSetting> mappings = new HashMap<>() {
			{
				put("power_in", new RegisterSetting((byte) 3, FUNCTION_CODE_POWER_IN, (short) 2));
				put("power_out", new RegisterSetting((byte) 3, FUNCTION_CODE_POWER_OUT, (short) 2));
				put("obis_1_8_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_1_8_0, (short) 2));
				put("obis_2_8_0", new RegisterSetting((byte) 3, FUNCTION_CODE_OBIS_2_8_0, (short) 2));
			}
		};
		for (var mapping : mappings.entrySet()) {
			Optional<ObjectNode> optionalValue = checkObis(channels, thing, thingType, mapping.getKey(),
					mapping.getValue(), serial, slaveId, modbusInterface);
			optionalValue.ifPresent(datapoints::add);
		}
		return datapoints;
	}

	@Override
	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
