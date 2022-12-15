package io.inoa.fleet.thing.modbus;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;

public abstract class ModbusBuilderBase {

	protected abstract ObjectMapper getObjectMapper();

	protected ObjectNode createModbusJsonNode(Integer serial, String thingTypeReference,
			RegisterSetting registerSetting, Thing thing, Integer modbusInterface, byte slaveId) {
		ObjectNode node = getObjectMapper().createObjectNode();
		ObjectNode header = getObjectMapper().createObjectNode();
		header.put("id", Utils.buildUrn(serial.toString(), thingTypeReference,
				String.format("0x%04X", registerSetting.getRegisterOffset())));
		header.put("name", thing.getName());
		header.put("type", "RS485");
		header.put("interval", 30000);
		node.set("header", header);

		node.put("interface", modbusInterface);
		node.put("frame", Utils.toBase64(Utils.buildFrame(slaveId, registerSetting.getFunctionCode(),
				registerSetting.getRegisterOffset(), registerSetting.getRegisterLength())));
		node.put("timeout", 500);
		return node;
	}

	protected Optional<ObjectNode> checkObis(Map<String, Object> channels, Thing thing, ThingType thingType, String key,
			RegisterSetting registerSetting, Integer serial, int slaveId, Integer modbusInterface) {
		if (channels.containsKey(key)) {
			Boolean value = (Boolean) channels.get(key);
			if (value) {
				ObjectNode node = createModbusJsonNode(serial, thingType.getThingTypeReference(), registerSetting,
						thing, modbusInterface, (byte) slaveId);
				return Optional.of(node);
			}
		}
		return Optional.empty();
	}

}
