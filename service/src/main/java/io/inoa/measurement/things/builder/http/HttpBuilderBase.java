package io.inoa.measurement.things.builder.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.inoa.measurement.things.builder.DatapointBuilder;
import io.inoa.measurement.things.builder.modbus.Utils;
import io.inoa.measurement.things.domain.Thing;

/**
 * Abstract class for datapoint builders with HTTP measurement (e.g. Shelly devices)
 *
 * @author fabian.schlegel
 */
public abstract class HttpBuilderBase extends DatapointBuilder {

	protected abstract ObjectMapper getObjectMapper();

	/**
	 * Creates JSON structure for the datapoint from the given thing.
	 *
	 * @param serial             serial of the thing
	 * @param thingTypeReference type of the thing
	 * @param thing              thing reference
	 * @param uri                the uri the poll
	 * @return JSON structure of the datapoint
	 */
	protected ObjectNode createHttpGetJsonNode(
			String serial, String thingTypeReference, Thing thing, String uri) {
		ObjectNode node = getObjectMapper().createObjectNode();
		node.put("id", Utils.buildUrn(serial, thingTypeReference, "status"));
		node.put("name", thing.getName());
		node.put("type", "HTTP_GET");
		node.put("interval", 30000);
		node.put("enabled", true);
		node.put("uri", uri);
		return node;
	}
}
