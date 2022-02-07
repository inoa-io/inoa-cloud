package io.inoa.fleet.thing.modbus;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.inoa.fleet.thing.domain.Property;
import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingChannel;
import io.inoa.fleet.thing.domain.ThingTypeChannel;
import io.inoa.lib.modbus.CRC16;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InoaSatelliteModbusRTUBuilder {

	private final ObjectMapper objectMapper;

	public ArrayNode build(Thing thing, List<ThingTypeChannel> thingTypeChannels, List<ThingChannel> thingChannels) {

		ArrayNode datapoints = objectMapper.createArrayNode();
		for (var channel : thingChannels) {
			Optional<ThingTypeChannel> optionalThingTypeChannel = thingTypeChannels.stream()
					.filter(t -> t.getKey().equals(channel.getKey())).findFirst();
			if (optionalThingTypeChannel.isEmpty()) {
				continue;
			}
			var thingTypeChannel = optionalThingTypeChannel.get();
			Optional<Property> name = thingTypeChannel.getProperties().stream().filter(p -> p.getKey().equals("name"))
					.findFirst();
			Optional<Property> urnPrefix = thingTypeChannel.getProperties().stream()
					.filter(p -> p.getKey().equals("urnPrefix")).findFirst();
			Optional<Property> urnPostfix = thingTypeChannel.getProperties().stream()
					.filter(p -> p.getKey().equals("urnPostfix")).findFirst();
			Optional<Property> registerOffsetProperty = thingTypeChannel.getProperties().stream()
					.filter(p -> p.getKey().equals("registerOffset")).findFirst();
			Optional<Property> serial = thing.getProperties().stream().filter(p -> p.getKey().equals("serial"))
					.findFirst();
			Optional<Property> slaveIdProperty = thing.getProperties().stream()
					.filter(p -> p.getKey().equals("slaveId")).findFirst();
			int slaveId = Integer.parseInt(slaveIdProperty.get().getValue().toString());
			int registerOffset = Integer.parseInt(registerOffsetProperty.get().getValue().toString());

			ObjectNode node = objectMapper.createObjectNode();
			ObjectNode header = objectMapper.createObjectNode();
			header.put("id", buildUrn(serial.get().getValue().toString(), urnPrefix.get().getValue().toString(),
					urnPostfix.get().getValue().toString()));
			header.put("name", name.get().getValue().toString());
			header.put("type", "RS485");
			header.put("interval", 30000);
			node.set("header", header);
			Optional<Property> interfaceProperty = thing.getProperties().stream()
					.filter(p -> p.getKey().equals("interface")).findFirst();
			node.put("interface", Integer.parseInt(interfaceProperty.get().getValue().toString()));
			node.put("frame", toBase64(buildFrame((byte) slaveId, (byte) 3, (short) registerOffset, (short) 2)));
			node.put("timeout", 500);
			datapoints.add(node);
		}
		return datapoints;
	}

	// urn:dvh4013:12345-01:0x4001
	String buildUrn(String serial, String urnPrefix, String urnPostfix) {
		return String.format("urn:%s:%s:%s", urnPrefix, serial, urnPostfix);
	}

	public byte[] buildFrame(byte slaveId, byte functionCode, short registerOffset, short numberOfRegisters) {
		byte[] bytes = new byte[6];
		bytes[0] = slaveId;
		bytes[1] = functionCode;
		bytes[2] = (byte) ((registerOffset >> 8) & 0xff);
		bytes[3] = (byte) (registerOffset & 0xff);
		bytes[4] = (byte) ((numberOfRegisters >> 8) & 0xff);
		bytes[5] = (byte) (numberOfRegisters & 0xff);
		byte[] target = new byte[8];
		target[0] = bytes[0];
		target[1] = bytes[1];
		target[2] = bytes[2];
		target[3] = bytes[3];
		target[4] = bytes[4];
		target[5] = bytes[5];
		byte[] crc = CRC16.getCRC(bytes);
		target[6] = crc[0];
		target[7] = crc[1];
		return target;
	}

	public String toBase64(byte[] frame) {
		return Base64.getEncoder().encodeToString(frame);
	}
}
