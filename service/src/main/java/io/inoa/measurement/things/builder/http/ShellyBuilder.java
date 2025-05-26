package io.inoa.measurement.things.builder.http;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.builder.ConfigCreator;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingType;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class ShellyBuilder extends HttpBuilderBase implements ConfigCreator {

	private final ObjectMapper objectMapper;

	@Override
	public ArrayNode build(Thing thing, ThingType thingType) {
		ArrayNode datapoints = objectMapper.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		String serial = (String) properties.get("serial");
		String uri = (String) properties.get("uri");
		datapoints.add(createHttpGetJsonNode(serial, thingType.getThingTypeId(), thing, uri));
		return datapoints;
	}

	@SuppressWarnings("removal")
	@Override
	public ArrayNode buildLegacy(Thing thing, ThingType thingType) {
		ArrayNode datapoints = objectMapper.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		String serial = (String) properties.get("serial");
		String uri = (String) properties.get("uri");
		datapoints.add(createHttpGetJsonNodeLegacy(serial, thingType.getThingTypeId(), thing, uri));
		return datapoints;
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
