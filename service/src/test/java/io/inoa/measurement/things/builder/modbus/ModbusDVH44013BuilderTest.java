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
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

public class ModbusDVH44013BuilderTest {

  @Test
  public void testBuildDefinitionLegacy() {
    ModbusDVH4013Builder builder = new ModbusDVH4013Builder(new ObjectMapper());
    Thing thing = new Thing();
    thing.setName("schrank");

    thing.addConfig("serial", ThingConfigurationType.STRING, "100022");
    thing.addConfig("modbus_interface", ThingConfigurationType.NUMBER, "1");

    thing.addMeasurand(new MeasurandType().setObisId("obis_1_8_0"));
    thing.addMeasurand(new MeasurandType().setObisId("obis_2_8_0"));
    thing.addMeasurand(new MeasurandType().setObisId("power_in"));
    thing.addMeasurand(new MeasurandType().setObisId("power_out"));

    ThingType thingType = new ThingType();
    thingType.setIdentifier("dvh4013");
    ArrayNode build = builder.buildLegacy(thing, thingType);
    List<JsonNode> items = StreamSupport.stream(build.spliterator(), false).toList();

    Optional<JsonNode> obis180 =
        items.stream()
            .filter(i -> i.get("header").get("id").asText().equals("urn:dvh4013:100022:0x0000"))
            .findFirst();
    assertTrue(obis180.isPresent());
    assertEquals("IwMAAAACwok=", obis180.get().get("frame").asText());
    Optional<JsonNode> obis181 =
        items.stream()
            .filter(i -> i.get("header").get("id").asText().equals("urn:dvh4013:100022:0x0002"))
            .findFirst();
    assertTrue(obis181.isPresent());
    assertEquals("IwMAAgACY0k=", obis181.get().get("frame").asText());
    Optional<JsonNode> obis182 =
        items.stream()
            .filter(i -> i.get("header").get("id").asText().equals("urn:dvh4013:100022:0x4001"))
            .findFirst();
    assertTrue(obis182.isPresent());
    assertEquals("IwNAAQAChok=", obis182.get().get("frame").asText());
    Optional<JsonNode> obis280 =
        items.stream()
            .filter(i -> i.get("header").get("id").asText().equals("urn:dvh4013:100022:0x4101"))
            .findFirst();
    assertTrue(obis280.isPresent());
    assertEquals("IwNBAQACh3U=", obis280.get().get("frame").asText());
  }
}
