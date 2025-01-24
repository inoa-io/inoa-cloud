package io.inoa.measurement.things.builder.modbus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.domain.MeasurandType;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingConfigurationType;
import io.inoa.measurement.things.domain.ThingType;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

public class ModbusMDVH4006BuilderTest {

  @Test
  public void testBuildDefinitionLegacy() {
    ModbusMDVH4006Builder builder = new ModbusMDVH4006Builder(new ObjectMapper());
    Thing thing = new Thing();
    thing.setName("schrank");

    thing.addConfig("serial", ThingConfigurationType.STRING, "39000976");
    thing.addConfig("modbus_interface", ThingConfigurationType.NUMBER, "1");

    thing.addMeasurand(new MeasurandType().setObisId("obis_1_8_0"));
    thing.addMeasurand(new MeasurandType().setObisId("obis_1_8_1"));
    thing.addMeasurand(new MeasurandType().setObisId("obis_1_8_2"));
    thing.addMeasurand(new MeasurandType().setObisId("obis_2_8_0"));
    thing.addMeasurand(new MeasurandType().setObisId("obis_2_8_1"));
    thing.addMeasurand(new MeasurandType().setObisId("obis_2_8_2"));
    thing.addMeasurand(new MeasurandType().setObisId("obis_1_7_0"));

    ThingType thingType = new ThingType();
    thingType.setIdentifier("mdvh4006");
    ArrayNode build = builder.buildLegacy(thing, thingType);
    List<JsonNode> items = StreamSupport.stream(build.spliterator(), false).toList();

    Optional<JsonNode> obis170 =
        items.stream()
            .filter(i -> i.get("header").get("id").asText().equals("urn:mdvh4006:39000976:0x4000"))
            .findFirst();
    assertTrue(obis170.isPresent());
    assertEquals(
        Utils.toBase64(HexFormat.of().parseHex("770340000002DA9D")),
        obis170.get().get("frame").asText());
  }
}
