package io.inoa.fleet.thing.modbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InoaSatelliteModbusDVHBuilder implements ConfigCreator {

	private final ObjectMapper objectMapper;

	@Override
	public ArrayNode build(Thing thing, ThingType thingType) {

		ArrayNode datapoints = objectMapper.createArrayNode();
		/*
		 * for (var channel : thingChannels) { Optional<ThingTypeChannel>
		 * optionalThingTypeChannel = thingTypeChannels.stream() .filter(t ->
		 * t.getKey().equals(channel.getKey())).findFirst(); if
		 * (optionalThingTypeChannel.isEmpty()) { continue; } var thingTypeChannel =
		 * optionalThingTypeChannel.get(); Optional<Property> name =
		 * thingTypeChannel.getProperties().stream().filter(p ->
		 * p.getKey().equals("name")) .findFirst(); Optional<Property> urnPrefix =
		 * thingTypeChannel.getProperties().stream() .filter(p ->
		 * p.getKey().equals("urnPrefix")).findFirst(); Optional<Property> urnPostfix =
		 * thingTypeChannel.getProperties().stream() .filter(p ->
		 * p.getKey().equals("urnPostfix")).findFirst(); Optional<Property>
		 * registerOffsetProperty = thingTypeChannel.getProperties().stream() .filter(p
		 * -> p.getKey().equals("registerOffset")).findFirst(); Optional<Property>
		 * serial = thing.getProperties().stream().filter(p ->
		 * p.getKey().equals("serial")) .findFirst(); Optional<Property> slaveIdProperty
		 * = thing.getProperties().stream() .filter(p ->
		 * p.getKey().equals("slaveId")).findFirst(); int slaveId =
		 * Integer.parseInt(slaveIdProperty.get().getValue().toString()); int
		 * registerOffset =
		 * Integer.parseInt(registerOffsetProperty.get().getValue().toString());
		 *
		 * ObjectNode node = objectMapper.createObjectNode(); ObjectNode header =
		 * objectMapper.createObjectNode(); header.put("id",
		 * buildUrn(serial.get().getValue().toString(),
		 * urnPrefix.get().getValue().toString(),
		 * urnPostfix.get().getValue().toString())); header.put("name",
		 * name.get().getValue().toString()); header.put("type", "RS485");
		 * header.put("interval", 30000); node.set("header", header); Optional<Property>
		 * interfaceProperty = thing.getProperties().stream() .filter(p ->
		 * p.getKey().equals("interface")).findFirst(); node.put("interface",
		 * Integer.parseInt(interfaceProperty.get().getValue().toString()));
		 * node.put("frame", toBase64(buildFrame((byte) slaveId, (byte) 3, (short)
		 * registerOffset, (short) 2))); node.put("timeout", 500); datapoints.add(node);
		 * }
		 */
		return datapoints;
	}
}
