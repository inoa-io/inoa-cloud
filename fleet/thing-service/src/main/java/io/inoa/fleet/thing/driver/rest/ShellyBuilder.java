package io.inoa.fleet.thing.driver.rest;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.driver.ConfigCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ShellyBuilder extends HttpBuilderBase implements ConfigCreator {

	private final ObjectMapper objectMapper;

	@Override
	public ArrayNode build(Thing thing, ThingType thingType) {
		ArrayNode datapoints = objectMapper.createArrayNode();
		Map<String, Object> properties = (Map<String, Object>) thing.getConfig().get("properties");
		String serial = (String) properties.get("serial");
		String uri = (String) properties.get("uri");
		datapoints.add(createHttpGetJsonNode(serial, thingType.getThingTypeReference(), thing, uri));
		return datapoints;
	}

	@Override
	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
