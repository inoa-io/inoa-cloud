package io.inoa.fleet.thing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.inoa.fleet.thing.domain.Property;
import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingChannel;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeChannel;
import io.inoa.fleet.thing.modbus.InoaSatelliteModbusRTUBuilder;

public class InoaSatelliteModbusRTUBuilderTest {

	@Test
	public void testFrameCreation() {
		InoaSatelliteModbusRTUBuilder builder = new InoaSatelliteModbusRTUBuilder(new ObjectMapper());
		var frame = builder.buildFrame((byte) 9, (byte) 3, (short) 16385, (short) 2);
		String base64 = builder.toBase64(frame);
		assertEquals("CQNAAQACgUM=", base64);
	}

	@Test
	public void testBuildDefinition() {
		var thingType = new ThingType();
		ThingTypeChannel powerChannel = new ThingTypeChannel().setKey("power");
		powerChannel.getProperties().add(new Property().setKey("name").setValue("power"));
		powerChannel.getProperties().add(new Property().setKey("urnPrefix").setValue("dvh4013"));
		powerChannel.getProperties().add(new Property().setKey("urnPostfix").setValue("0x4001"));
		powerChannel.getProperties().add(new Property().setKey("registerOffset").setValue(16385));

		List<ThingTypeChannel> thingTypeChannels = new ArrayList<>();
		thingTypeChannels.add(powerChannel);
		InoaSatelliteModbusRTUBuilder builder = new InoaSatelliteModbusRTUBuilder(new ObjectMapper());
		var thing = new Thing().setThingType(thingType);
		thing.getProperties().add(new Property().setKey("serial").setValue("12345-01"));
		thing.getProperties().add(new Property().setKey("slaveId").setValue(9));

		List<ThingChannel> channels = new ArrayList<>();
		ThingChannel thingChannel = new ThingChannel().setThing(thing).setKey("power")
				.setThingChannelId(UUID.randomUUID());
		channels.add(thingChannel);

		builder.build(thing, thingTypeChannels, channels);
	}
}
