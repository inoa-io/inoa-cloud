package io.inoa.measurement.things.builder.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.builder.DatapointBuilderException;
import io.inoa.measurement.things.domain.Thing;
import lombok.RequiredArgsConstructor;

/**
 * Datapoint builder for Shelly devices
 *
 * @author fabian.schlegel@grayc.de
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class ShellyBuilder extends HttpBuilderBase {

	private final ObjectMapper objectMapper;

	public static final String CONFIG_KEY_SERIAL = "serial";
	public static final String CONFIG_KEY_URI = "uri";

	@Override
	public ArrayNode build(Thing thing) throws DatapointBuilderException {
		ArrayNode datapoints = objectMapper.createArrayNode();
		var serial = getConfigAsString(thing, CONFIG_KEY_SERIAL);
		var uri = getConfigAsString(thing, CONFIG_KEY_URI);
		datapoints.add(createHttpGetJsonNode(serial, thing.getThingType().getIdentifier(), thing, uri));
		return datapoints;
	}

	@Override
	public ArrayNode buildRPC(Thing thing) {
		return null;
	}

	@Override
	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
