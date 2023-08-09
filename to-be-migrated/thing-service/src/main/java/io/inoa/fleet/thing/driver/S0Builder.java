package io.inoa.fleet.thing.driver;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.driver.modbus.Utils;
import io.inoa.fleet.thing.rest.management.DatapointVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class S0Builder implements ConfigCreator {

	private final ObjectMapper objectMapper;

	@Override
	public ArrayNode build(Thing thing, ThingType thingType) throws JsonProcessingException {
		ArrayNode datapoints = objectMapper.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		String serial = (String) properties.get("serial");
		String sensor = (String) properties.get("sensor");
		Integer iface = (Integer) properties.get("interface");
		datapoints.add(createS0JsonNode(serial, thingType.getThingTypeReference(), thing, sensor, iface));
		return datapoints;
	}

	@Override
	public ArrayNode buildLegacy(Thing thing, ThingType thingType) {
		ArrayNode datapoints = objectMapper.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		String serial = (String) properties.get("serial");
		String sensor = (String) properties.get("sensor");
		Integer iface = (Integer) properties.get("interface");
		datapoints.add(createS0JsonNodeLegacy(serial, thingType.getThingTypeReference(), thing, sensor, iface));
		return datapoints;
	}

	@Override
	public ArrayNode buildRPC(Thing thing, ThingType thingType) {
		return null;
	}

	private JsonNode createS0JsonNode(String serial, String thingTypeReference, Thing thing, String sensor,
		int iface) throws JsonProcessingException {
		return new ObjectMapper()
			.valueToTree(
				new DatapointVO()
					.id(Utils.buildUrn(serial, thingTypeReference, sensor))
					.name(thing.getName())
					.type(DatapointVO.Type.S0)
					.interval(30)
					._interface(iface));
	}

	private ObjectNode createS0JsonNodeLegacy(String serial, String thingTypeReference, Thing thing, String sensor,
		int iface) {
		ObjectNode node = objectMapper.createObjectNode();
		ObjectNode header = objectMapper.createObjectNode();
		header.put("id", Utils.buildUrn(serial, thingTypeReference, sensor));
		header.put("name", thing.getName());
		header.put("type", "S0");
		header.put("interval", 30000);
		node.set("header", header);
		node.put("interface", iface);
		return node;
	}
}
