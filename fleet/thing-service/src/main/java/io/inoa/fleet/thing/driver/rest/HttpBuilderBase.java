package io.inoa.fleet.thing.driver.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.driver.modbus.Utils;

public abstract class HttpBuilderBase {

	protected abstract ObjectMapper getObjectMapper();

	protected ObjectNode createHttpGetJsonNode(String serial, String thingTypeReference, Thing thing, String uri) {
		ObjectNode node = getObjectMapper().createObjectNode();
		ObjectNode header = getObjectMapper().createObjectNode();
		header.put("id", Utils.buildUrn(serial, thingTypeReference, "status"));
		header.put("name", thing.getName());
		header.put("type", "HTTP_GET");
		header.put("interval", 30000);
		node.set("header", header);
		node.put("uri", uri);
		return node;
	}
}
