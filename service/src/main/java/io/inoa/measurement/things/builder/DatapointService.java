package io.inoa.measurement.things.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.builder.http.ShellyBuilder;
import io.inoa.measurement.things.builder.modbus.DvModbusIRBuilder;
import io.inoa.measurement.things.builder.modbus.ModbusDVH4013Builder;
import io.inoa.measurement.things.builder.modbus.ModbusMDVH4006Builder;
import io.inoa.measurement.things.builder.s0.S0Builder;
import io.inoa.measurement.things.domain.Thing;
import lombok.extern.slf4j.Slf4j;

/**
 * Service providing datapoint definitions readable for INOA metering devices.
 *
 * @author Fabian Schlegel
 */
@Slf4j
@Singleton
public class DatapointService {

	private final Map<String, DatapointBuilder> datapointBuilders = new HashMap<>();
	private final ObjectMapper objectMapper;

	public DatapointService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		// ThingType -> Builder mapping is fix by now. May be replaced by plugin mechanism in the
		// future.
		datapointBuilders.put("dvmodbusir", new DvModbusIRBuilder(objectMapper));
		datapointBuilders.put("dvh4013", new ModbusDVH4013Builder(objectMapper));
		datapointBuilders.put("mdvh4006", new ModbusMDVH4006Builder(objectMapper));
		datapointBuilders.put("shplg-s", new ShellyBuilder(objectMapper));
		datapointBuilders.put("shellyht", new ShellyBuilder(objectMapper));
		datapointBuilders.put("s0", new S0Builder(objectMapper));
	}

	public String getDatapointsJson(List<Thing> things) throws DatapointBuilderException {
		ArrayNode datapoints = objectMapper.createArrayNode();
		for (Thing thing : things) {
			DatapointBuilder builder = datapointBuilders.get(thing.getThingType().getIdentifier());
			// TODO: Really stop here? Or go on with known thing types?
			if (builder == null) {
				// TODO: Better exception hierarchy
				throw new IllegalArgumentException(
						"No mapper for thing type: " + thing.getThingType().getIdentifier());
			}
			datapoints.addAll(builder.build(thing));
		}
		return datapoints.toString();
	}
}
