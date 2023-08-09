package io.inoa.fleet.thing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.driver.modbus.ModbusDVH4013Builder;

public class ModbusDVH44013BuilderTest {

	@Test
	public void testBuildDefinitionLegacy() {
		ModbusDVH4013Builder builder = new ModbusDVH4013Builder(new ObjectMapper());
		Thing thing = new Thing();
		thing.setName("schrank");
		HashMap<String, Object> config = new HashMap<>();
		HashMap<String, Object> properties = new HashMap<>();
		HashMap<String, Object> channels = new HashMap<>();
		properties.put("serial", 100022);
		properties.put("modbus_interface", 1);

		channels.put("obis_1_8_0", true);
		channels.put("obis_2_8_0", true);
		channels.put("power_in", true);
		channels.put("power_out", true);
		config.put("properties", properties);
		config.put("channels", channels);
		thing.setConfig(config);
		ThingType thingType = new ThingType();
		thingType.setThingTypeReference("dvh4013");
		ArrayNode build = builder.buildLegacy(thing, thingType);
		List<JsonNode> items = StreamSupport.stream(build.spliterator(), false).toList();

		Optional<JsonNode> obis180 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:dvh4013:100022:0x0000")).findFirst();
		assertTrue(obis180.isPresent());
		assertEquals("IwMAAAACwok=", obis180.get().get("frame").asText());
		Optional<JsonNode> obis181 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:dvh4013:100022:0x0002")).findFirst();
		assertTrue(obis181.isPresent());
		assertEquals("IwMAAgACY0k=", obis181.get().get("frame").asText());
		Optional<JsonNode> obis182 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:dvh4013:100022:0x4001")).findFirst();
		assertTrue(obis182.isPresent());
		assertEquals("IwNAAQAChok=", obis182.get().get("frame").asText());
		Optional<JsonNode> obis280 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:dvh4013:100022:0x4101")).findFirst();
		assertTrue(obis280.isPresent());
		assertEquals("IwNBAQACh3U=", obis280.get().get("frame").asText());
	}
}
