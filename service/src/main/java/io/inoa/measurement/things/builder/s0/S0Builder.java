package io.inoa.measurement.things.builder.s0;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.builder.DatapointBuilder;
import io.inoa.measurement.things.builder.DatapointBuilderException;
import io.inoa.measurement.things.builder.modbus.Utils;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.rest.DatapointVO;
import lombok.RequiredArgsConstructor;

/**
 * Datapoint builder for <a href="https://de.wikipedia.org/wiki/S0-Schnittstelle">S0 pulse
 * devices</a>.
 *
 * @author fabian.schlegel@grayc.de
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class S0Builder extends DatapointBuilder {

	private final ObjectMapper objectMapper;

	public static final String CONFIG_KEY_SERIAL = "serial";
	public static final String CONFIG_KEY_SENSOR = "sensor";
	public static final String CONFIG_KEY_INTERFACE = "interface";

	@Override
	public ArrayNode build(Thing thing) throws DatapointBuilderException {
		ArrayNode datapoints = objectMapper.createArrayNode();
		var serial = getConfigAsString(thing, CONFIG_KEY_SERIAL);
		var sensor = getConfigAsString(thing, CONFIG_KEY_SENSOR);
		var iface = getConfigAsNumber(thing, CONFIG_KEY_INTERFACE).intValue();
		datapoints.add(
				createS0JsonNode(serial, thing.getThingType().getIdentifier(), thing, sensor, iface));
		return datapoints;
	}

	@Override
	public ArrayNode buildRPC(Thing thing) {
		return null;
	}

	private JsonNode createS0JsonNode(
			String serial, String thingTypeReference, Thing thing, String sensor, int iface) {
		return new ObjectMapper()
				.valueToTree(
						new DatapointVO()
								.id(Utils.buildUrn(serial, thingTypeReference, sensor))
								.name(thing.getName())
								.type(DatapointVO.Type.S0)
								.interval(30)
								._interface(iface));
	}
}
