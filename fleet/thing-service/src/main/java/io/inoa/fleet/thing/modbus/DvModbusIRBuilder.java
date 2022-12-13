package io.inoa.fleet.thing.modbus;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DvModbusIRBuilder {

	private static short FUNCTION_CODE_SERIAL_NUMBER = 0x000B;
	private static short FUNCTION_CODE_OBIS_1_8_0 = 0x000D;
	private static short FUNCTION_CODE_OBIS_1_8_1 = 0x000D;
	private static short FUNCTION_CODE_OBIS_1_8_2 = 0x0017;
	private static short FUNCTION_CODE_OBIS_2_8_0 = 0x001C;
	private static short FUNCTION_CODE_OBIS_2_8_1 = 0x0021;
	private static short FUNCTION_CODE_OBIS_2_8_2 = 0x0026;
	private static short FUNCTION_CODE_OBIS_1_7_0 = 0x002B;
	private final ObjectMapper objectMapper;

	public ArrayNode build(Thing thing, ThingType thingType) {
		ArrayNode datapoints = objectMapper.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		Integer modbusInterface = (Integer) properties.get("modbus_interface");
		Map<String, Object> channels = (Map<String, Object>) thing.getConfig().get("channels");
		if (channels.containsKey("obis_1_8_0")) {
			Boolean obis_1_8_0 = (Boolean) channels.get("obis_1_8_0");
			int slaveId = serial % 1000;
			if (slaveId > 255) {
				log.warn("slaveId greater then 255 id: {} -- serial: {}", slaveId, serial);
			}
			if (obis_1_8_0) {
				ObjectNode node = objectMapper.createObjectNode();
				ObjectNode header = objectMapper.createObjectNode();
				header.put("id", Utils.buildUrn(serial.toString(), thingType.getThingTypeReference(),
						String.format("0x%04X", FUNCTION_CODE_OBIS_1_8_0)));
				header.put("name", thing.getName());
				header.put("type", "RS485");
				header.put("interval", 30000);
				node.set("header", header);

				node.put("interface", modbusInterface);
				node.put("frame", Utils
						.toBase64(Utils.buildFrame((byte) slaveId, (byte) 3, FUNCTION_CODE_OBIS_1_8_0, (short) 4)));
				node.put("timeout", 500);
				datapoints.add(node);
			}
		}
		return datapoints;
	}
}
