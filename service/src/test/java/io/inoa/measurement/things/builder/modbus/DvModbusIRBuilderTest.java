package io.inoa.measurement.things.builder.modbus;

import static io.inoa.measurement.things.domain.ObisId.OBIS_1_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_1_8_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_8_0;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
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

public class DvModbusIRBuilderTest extends AbstractBuilderTest {

	@Test
	public void testBuildDefinition() throws JsonProcessingException, DatapointBuilderException {
		DvModbusIRBuilder builder = new DvModbusIRBuilder(new ObjectMapper());
		Thing thing = new Thing();
		thing.setName("schrank");
		thing.setThingConfigurationValues(new HashSet<>());
		thing.setMeasurands(new HashSet<>());

		addConfig(thing, "serial", ThingConfigurationType.NUMBER, "22");
		addConfig(thing, "modbus_interface", ThingConfigurationType.NUMBER, "1");

		addMeasurand(thing, new MeasurandType().setObisId(OBIS_1_8_0.getObisId()));
		addMeasurand(thing, new MeasurandType().setObisId(OBIS_2_8_0.getObisId()));
		addMeasurand(thing, new MeasurandType().setObisId(OBIS_1_7_0.getObisId()));

		thing.setThingType(new ThingType().setIdentifier("bla"));

		ArrayNode build = builder.build(thing);
		List<JsonNode> items = StreamSupport.stream(build.spliterator(), false).toList();

		Optional<JsonNode> obis180 = items.stream().filter(i -> i.get("id").asText().equals("urn:bla:22:0x000D"))
				.findFirst();
		assertTrue(obis180.isPresent());
		assertEquals("1603000D0004D6ED", obis180.get().get("frame").asText());
		Optional<JsonNode> obis280 = items.stream().filter(i -> i.get("id").asText().equals("urn:bla:22:0x001C"))
				.findFirst();
		assertTrue(obis280.isPresent());
		assertEquals("1603001C000486E8", obis280.get().get("frame").asText());
		Optional<JsonNode> obis170 = items.stream().filter(i -> i.get("id").asText().equals("urn:bla:22:0x002B"))
				.findFirst();
		assertTrue(obis170.isPresent());
		assertEquals("1603002B0002B724", obis170.get().get("frame").asText());
	}
}
