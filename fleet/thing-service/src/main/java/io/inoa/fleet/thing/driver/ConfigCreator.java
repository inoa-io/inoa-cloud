package io.inoa.fleet.thing.driver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;

public interface ConfigCreator {

	/**
	 * Build the JSON structure for the datapoint definition for a specific thing
	 *
	 * @param thing the thing to export the JSON from
	 * @param thingType corresponding thing type
	 * @return JSON structure
	 */
	ArrayNode build(Thing thing, ThingType thingType) throws JsonProcessingException;

	/**
	 * Build the JSON structure for the datapoint definition for a specific thing
	 *
	 * @param thing the thing to export the JSON from
	 * @param thingType corresponding thing type
	 * @return JSON structure
	 * @deprecated Use build function for OS version >= 0.4.0
	 */
	@Deprecated(forRemoval = true)
	ArrayNode buildLegacy(Thing thing, ThingType thingType);

	/**
	 * Build the RPC JSON structure for the datapoint definition for a specific thing
	 *
	 * @param thing the thing to export the JSON from
	 * @param thingType corresponding thing type
	 * @return JSON structure
	 */
	ArrayNode buildRPC(Thing thing, ThingType thingType);
}
