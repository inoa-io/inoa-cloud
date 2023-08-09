package io.inoa.measurement.things.builder.modbus;

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

import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingType;

public class DvModbusIRBuilderTest {

	@Test
	public void testBuildDefinitionLegacy() {
		DvModbusIRBuilder builder = new DvModbusIRBuilder(new ObjectMapper());
		Thing thing = new Thing();
		thing.setName("schrank");
		HashMap<String, Object> config = new HashMap<>();
		HashMap<String, Object> properties = new HashMap<>();
		HashMap<String, Object> channels = new HashMap<>();
		properties.put("serial", 22);
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
		thingType.setThingTypeReference("bla");
		ArrayNode build = builder.buildLegacy(thing, thingType);
		List<JsonNode> items = StreamSupport.stream(build.spliterator(), false).toList();

		Optional<JsonNode> obis180 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:bla:22:0x000D")).findFirst();
		assertTrue(obis180.isPresent());
		assertEquals(Utils.toBase64(HexFormat.of().parseHex("1603000D0004D6ED")), obis180.get().get("frame").asText());
		Optional<JsonNode> obis181 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:bla:22:0x0012")).findFirst();
		assertTrue(obis181.isPresent());
		assertEquals("FgMAEgAE5ys=", obis181.get().get("frame").asText());
		Optional<JsonNode> obis182 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:bla:22:0x0017")).findFirst();
		assertTrue(obis182.isPresent());
		assertEquals("FgMAFwAE9yo=", obis182.get().get("frame").asText());
		Optional<JsonNode> obis280 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:bla:22:0x001C")).findFirst();
		assertTrue(obis280.isPresent());
		assertEquals("FgMAHAAEhug=", obis280.get().get("frame").asText());
		Optional<JsonNode> obis281 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:bla:22:0x0021")).findFirst();
		assertTrue(obis281.isPresent());
		assertEquals("FgMAIQAEFyQ=", obis281.get().get("frame").asText());
		Optional<JsonNode> obis282 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:bla:22:0x0026")).findFirst();
		assertTrue(obis282.isPresent());
		assertEquals("FgMAJgAEpuU=", obis282.get().get("frame").asText());
		Optional<JsonNode> obis170 = items.stream()
				.filter(i -> i.get("header").get("id").asText().equals("urn:bla:22:0x002B")).findFirst();
		assertTrue(obis170.isPresent());
		assertEquals("FgMAKwACtyQ=", obis170.get().get("frame").asText());

	}
}
