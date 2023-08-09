package io.inoa.measurement.things.builder.modbus;

import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.measurement.model.DatapointVO;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingType;

public abstract class ModbusBuilderBase {

	protected abstract ObjectMapper getObjectMapper();

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Deprecated(forRemoval = true)
	protected ObjectNode createModbusJsonNodeLegacy(Integer serial, String thingTypeReference,
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

	protected ObjectNode createModbusJsonNode(Integer serial, String thingTypeReference,
		RegisterSetting registerSetting, Thing thing, Integer modbusInterface, byte slaveId) {
		return new ObjectMapper()
			.valueToTree(
				new DatapointVO()
					.id(Utils.buildUrn(serial.toString(), thingTypeReference,
						String.format("0x%04X", registerSetting.getRegisterOffset())))
					.name(thing.getName())
					.enabled(true)
					.type(DatapointVO.Type.RS485)
					.interval(30000)
					._interface(modbusInterface)
					.frame(HexFormat.of().formatHex(Utils.buildFrame(slaveId, registerSetting.getFunctionCode(),
						registerSetting.getRegisterOffset(), registerSetting.getRegisterLength())))
					.timeout(1000));
	}

	@Deprecated(forRemoval = true)
	protected Optional<ObjectNode> obisCodeToDatapointJSONLegacy(Map<String, Object> channels, Thing thing,
		ThingType thingType, String obisCodeKey, RegisterSetting registerSetting, Integer serial, int slaveId,
		Integer modbusInterface) {
		if (channels.containsKey(obisCodeKey) && (boolean) channels.get(obisCodeKey)) {
			ObjectNode node = createModbusJsonNodeLegacy(serial, thingType.getThingTypeReference(), registerSetting,
				thing, modbusInterface, (byte) slaveId);
			return Optional.of(node);
		}
		return Optional.empty();
	}

	protected Optional<ObjectNode> obisCodeToDatapointJSON(Map<String, Object> channels, Thing thing,
		ThingType thingType, String obisCodeKey, RegisterSetting registerSetting, Integer serial, int slaveId,
		Integer modbusInterface) {
		if (channels.containsKey(obisCodeKey) && (boolean) channels.get(obisCodeKey)) {
			ObjectNode node = createModbusJsonNode(serial, thingType.getThingTypeReference(), registerSetting,
				thing, modbusInterface, (byte) slaveId);
			return Optional.of(node);
		}
		return Optional.empty();
	}

	@Deprecated(forRemoval = true)
	protected ArrayNode toDatapointsLegacy(Thing thing, ThingType thingType, int slaveId, Map<String,
		RegisterSetting> mappings) {
		ArrayNode datapoints = OBJECT_MAPPER.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		Integer modbusInterface = (Integer) properties.get("modbus_interface");
		Map<String, Object> channels = (Map<String, Object>) thing.getConfig().get("channels");
		for (var mapping : mappings.entrySet()) {
			Optional<ObjectNode> optionalValue = obisCodeToDatapointJSONLegacy(channels, thing, thingType,
				mapping.getKey(), mapping.getValue(), serial, slaveId, modbusInterface);
			optionalValue.ifPresent(datapoints::add);
		}
		return datapoints;
	}

	protected ArrayNode toDatapoints(Thing thing, ThingType thingType, int slaveId, Map<String,
		RegisterSetting> mappings) {
		ArrayNode datapoints = OBJECT_MAPPER.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		Integer serial = (Integer) properties.get("serial");
		Integer modbusInterface = (Integer) properties.get("modbus_interface");
		Map<String, Object> channels = (Map<String, Object>) thing.getConfig().get("channels");
		for (var mapping : mappings.entrySet()) {
			Optional<ObjectNode> optionalValue = obisCodeToDatapointJSON(channels, thing, thingType, mapping.getKey(),
				mapping.getValue(), serial, slaveId, modbusInterface);
			optionalValue.ifPresent(datapoints::add);
		}
		return datapoints;
	}
}
