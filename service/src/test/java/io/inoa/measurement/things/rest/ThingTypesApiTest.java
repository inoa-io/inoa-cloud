package io.inoa.measurement.things.rest;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert404;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.inoa.measurement.things.domain.ObisId;
import io.inoa.rest.ThingConfigurationVO;
import io.inoa.rest.ThingTypeCategoryVO;
import io.inoa.rest.ThingTypeCreateVO;
import io.inoa.rest.ThingTypeProtocolVO;
import io.inoa.rest.ThingTypeUpdateVO;
import io.inoa.rest.ThingTypesApiTestClient;
import io.inoa.rest.ThingTypesApiTestSpec;
import io.inoa.test.AbstractUnitTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ThingTypes API Tests")
public class ThingTypesApiTest extends AbstractUnitTest implements ThingTypesApiTestSpec {

  @Inject ThingTypesApiTestClient client;

  @Disabled("TODO: Stack Overflow")
  @Test
  @Override
  public void createThingType201() {
    var thingTypeVO =
        new ThingTypeCreateVO()
            .identifier("newThingType")
            .name("New thing type")
            .description("Created new thing type for testing")
            .category(ThingTypeCategoryVO.ELECTRIC_METER)
            .protocol(ThingTypeProtocolVO.MODBUS_RS458)
            .addConfigurationsItem(
                new ThingConfigurationVO()
                    .name("New thing configuration")
                    .description("Created new thing configuration for testing")
                    .type(ThingConfigurationVO.Type.STRING)
                    .validationRegex("$(0-9)[5]^"))
            .addMeasurandsItem(ObisId.OBIS_1_8_0.getObisId());

    assert201(() -> client.createThingType(auth("inoa"), thingTypeVO));
  }

  @Test
  @Override
  public void createThingType400() {
    var thingTypeVO = new ThingTypeCreateVO();
    assert400(() -> client.createThingType(auth("inoa"), thingTypeVO));
  }

  @Test
  @Override
  public void createThingType401() {
    assert401(() -> client.createThingType(null, new ThingTypeCreateVO()));
  }

  @Disabled("NYI")
  @Test
  @Override
  public void createThingType409() {}

  @Disabled("NYI")
  @Test
  @Override
  public void deleteThingType204() {}

  @Test
  @Override
  public void deleteThingType401() {
    assert401(() -> client.deleteThingType(null, "unknown"));
  }

  @Test
  @Override
  public void deleteThingType404() {
    assert404(() -> client.findThingType(auth("inoa"), "unknown"));
  }

  @Test
  @Override
  public void findThingType200() {
    var thingType = assert200(() -> client.findThingType(auth("inoa"), "dvh4013"));
    assertEquals("dvh4013", thingType.getIdentifier(), "Identifier shall be dvh3013");
    assertEquals("DZG DVH4013", thingType.getName(), "Name shall match.");
    assertEquals(
        "DZG DVH4013 bi-directional power meter",
        thingType.getDescription(),
        "Description shall match.");
    assertEquals(null, thingType.getVersion(), "Version shall be null");
    assertEquals(
        ThingTypeCategoryVO.ELECTRIC_METER, thingType.getCategory(), "Category shall match.");
    assertEquals(
        ThingTypeProtocolVO.MODBUS_RS458, thingType.getProtocol(), "Protocol shall match.");
    assertEquals(2, thingType.getConfigurations().size(), "Configurations shall be loaded.");
    assertEquals(8, thingType.getMeasurands().size(), "Measurands shall be loaded.");
  }

  @Test
  @Override
  public void findThingType401() {
    assert401(() -> client.findThingType(null, "unknown"));
  }

  @Test
  @Override
  public void findThingType404() {
    assert404(() -> client.findThingType(auth("inoa"), "unknown"));
  }

  @Override
  public void getThingTypes200() {
    var thingTypes = assert200(() -> client.getThingTypes(auth("inoa")));
    assertNotNull(thingTypes, "ThingTypes shall be loaded.");
    assertFalse(thingTypes.isEmpty(), "ThingTypes shall not be empty.");
  }

  @Override
  public void getThingTypes401() {
    assert401(() -> client.getThingTypes(null));
  }

  @Disabled("It's very unlikely that all thing types are deleted.")
  @Override
  public void getThingTypes404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThingType200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThingType400() {}

  @Test
  @Override
  public void updateThingType401() {
    assert401(() -> client.updateThingType(null, "unknown", new ThingTypeUpdateVO()));
  }

  @Disabled("NYI")
  @Test
  @Override
  public void updateThingType404() {}
}
