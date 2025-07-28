package io.inoa.measurement.things.builder.modbus;

import static io.inoa.measurement.things.domain.ObisId.OBIS_1_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_1_8_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_8_0;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.builder.AbstractBuilderTest;
import io.inoa.measurement.things.builder.DatapointBuilderException;
import io.inoa.measurement.things.domain.MeasurandType;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingConfigurationType;
import io.inoa.measurement.things.domain.ThingType;

public class ModbusMDVH4006BuilderTest extends AbstractBuilderTest {

	@Test
	public void testBuildDefinition() throws DatapointBuilderException {
		ModbusMDVH4006Builder builder = new ModbusMDVH4006Builder(new ObjectMapper());
		Thing thing = new Thing();
		thing.setName("schrank");

		addConfig(thing, "serial", ThingConfigurationType.NUMBER, "39000976");
		addConfig(thing, "modbus interface", ThingConfigurationType.NUMBER, "1");

		addMeasurand(thing, new MeasurandType().setObisId(OBIS_1_8_0.getObisId()));
		addMeasurand(thing, new MeasurandType().setObisId(OBIS_2_8_0.getObisId()));
		addMeasurand(thing, new MeasurandType().setObisId(OBIS_1_7_0.getObisId()));

		thing.setThingType(new ThingType().setIdentifier("mdvh4006"));
		ArrayNode build = builder.build(thing);
		List<JsonNode> items = StreamSupport.stream(build.spliterator(), false).toList();

		Optional<JsonNode> obis180 = items.stream()
				.filter(i -> i.get("id").asText().equals("urn:mdvh4006:39000976:0x4000"))
				.findFirst();
		assertTrue(obis180.isPresent());
		assertEquals("770340000002DA9D", obis180.get().get("frame").asText());

		Optional<JsonNode> obis280 = items.stream()
				.filter(i -> i.get("id").asText().equals("urn:mdvh4006:39000976:0x4100"))
				.findFirst();
		assertTrue(obis280.isPresent());
		assertEquals("770341000002DB61", obis280.get().get("frame").asText());

		Optional<JsonNode> obis170 = items.stream()
				.filter(i -> i.get("id").asText().equals("urn:mdvh4006:39000976:0x0000"))
				.findFirst();
		assertTrue(obis170.isPresent());
		assertEquals("770300000002CF5D", obis170.get().get("frame").asText());
	}
}
