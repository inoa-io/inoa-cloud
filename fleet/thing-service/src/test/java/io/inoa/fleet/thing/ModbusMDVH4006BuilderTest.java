package io.inoa.fleet.thing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.driver.modbus.ModbusMDVH4006Builder;
import io.inoa.fleet.thing.driver.modbus.Utils;

public class ModbusMDVH4006BuilderTest {

	@Test
	public void testBuildDefinitionLegacy() {
		ModbusMDVH4006Builder builder = new ModbusMDVH4006Builder(new ObjectMapper());
		Thing thing = new Thing();
		thing.setName("schrank");
		HashMap<String, Object> config = new HashMap<>();
		HashMap<String, Object> properties = new HashMap<>();
		HashMap<String, Object> channels = new HashMap<>();
		properties.put("serial", 39000976);
		properties.put("modbus_interface", 1);

		channels.put("obis_1_8_0", true);
		channels.put("obis_1_8_1", true);
		channels.put("obis_1_8_2", true);
		channels.put("obis_2_8_0", true);
		channels.put("obis_2_8_1", true);
		channels.put("obis_2_8_2", true);
		channels.put("obis_1_7_0", true);
		config.put("properties", properties);
		config.put("channels", channels);
		thing.setConfig(config);
		ThingType thingType = new ThingType();
		thingType.setThingTypeReference("mdvh4006");
		ArrayNode build = builder.buildLegacy(thing, thingType);
		List<JsonNode> items = StreamSupport.stream(build.spliterator(), false).toList();

		Optional<JsonNode> obis170 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:mdvh4006:39000976:0x4000")).findFirst();
		assertTrue(obis170.isPresent());
		assertEquals(Utils.toBase64(HexFormat.of().parseHex("770340000002DA9D")), obis170.get().get("frame").asText());

	}
}
