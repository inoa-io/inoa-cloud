package io.inoa.fleet.thing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.modbus.DvModbusIRBuilder;
import io.inoa.fleet.thing.modbus.Utils;

public class DvModbusIRBuilderTest {

	@Test
	public void testFrameCreation() {
		DvModbusIRBuilder builder = new DvModbusIRBuilder(new ObjectMapper());
		var frame = Utils.buildFrame((byte) 9, (byte) 3, (short) 16385, (short) 2);
		String base64 = Utils.toBase64(frame);
		assertEquals("CQNAAQACgUM=", base64);
	}

	@Test
	public void testBuildDefinition() {
		DvModbusIRBuilder builder = new DvModbusIRBuilder(new ObjectMapper());
		Thing thing = new Thing();
		thing.setName("schrank");
		HashMap<String, Object> config = new HashMap<>();
		HashMap<String, Object> properties = new HashMap<>();
		HashMap<String, Object> channels = new HashMap<>();
		properties.put("serial", 100022);
		properties.put("modbus_interface", 1);

		channels.put("work_in", true);
		config.put("properties", properties);
		config.put("channels", channels);
		thing.setConfig(config);
		ThingType thingType = new ThingType();
		thingType.setThingTypeReference("bla");
		ArrayNode build = builder.build(thing, thingType);
	}
}
