package io.inoa.measurement.things.builder.modbus;

import static io.inoa.measurement.things.domain.ObisId.OBIS_1_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_1_8_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_8_0;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.builder.AbstractBuilderTest;
import io.inoa.measurement.things.builder.DatapointBuilderException;
import io.inoa.measurement.things.domain.MeasurandType;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingConfigurationType;
import io.inoa.measurement.things.domain.ThingType;

public class ModbusDVH44013BuilderTest extends AbstractBuilderTest {

	@Test
	public void testBuildDefinition() throws JsonProcessingException, DatapointBuilderException {
		ModbusDVH4013Builder builder = new ModbusDVH4013Builder(new ObjectMapper());
		Thing thing = new Thing();
		thing.setName("schrank");

		addConfig(thing, "serial", ThingConfigurationType.NUMBER, "100022");
		addConfig(thing, "modbus_interface", ThingConfigurationType.NUMBER, "1");

		addMeasurand(thing, new MeasurandType().setObisId(OBIS_1_8_0.getObisId()));
		addMeasurand(thing, new MeasurandType().setObisId(OBIS_2_8_0.getObisId()));
		addMeasurand(thing, new MeasurandType().setObisId(OBIS_1_7_0.getObisId()));
		addMeasurand(thing, new MeasurandType().setObisId(OBIS_2_7_0.getObisId()));

		thing.setThingType(new ThingType().setIdentifier("dvh4013"));
		ArrayNode build = builder.build(thing);
		List<JsonNode> items = StreamSupport.stream(build.spliterator(), false).toList();

		Optional<JsonNode> obis180 = items.stream()
				.filter(i -> i.get("id").asText().equals("urn:dvh4013:100022:0x0000"))
				.findFirst();
		assertTrue(obis180.isPresent());
		assertEquals("230300000002C289", obis180.get().get("frame").asText());
		Optional<JsonNode> obis181 = items.stream()
				.filter(i -> i.get("id").asText().equals("urn:dvh4013:100022:0x0002"))
				.findFirst();
		assertTrue(obis181.isPresent());
		assertEquals("2303000200026349", obis181.get().get("frame").asText());
		Optional<JsonNode> obis182 = items.stream()
				.filter(i -> i.get("id").asText().equals("urn:dvh4013:100022:0x4001"))
				.findFirst();
		assertTrue(obis182.isPresent());
		assertEquals("2303400100028689", obis182.get().get("frame").asText());
		Optional<JsonNode> obis280 = items.stream()
				.filter(i -> i.get("id").asText().equals("urn:dvh4013:100022:0x4101"))
				.findFirst();
		assertTrue(obis280.isPresent());
		assertEquals("2303410100028775", obis280.get().get("frame").asText());
	}
}
