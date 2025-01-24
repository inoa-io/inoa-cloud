package io.inoa.measurement.things.rest;

import static io.inoa.test.HttpAssertions.assert201;

import io.inoa.rest.ThingTypeVO;
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

  @Disabled("NYI")
  @Test
  @Override
  public void createThingType201() {
    var vo = new ThingTypeVO().name("DvModbusIR").identifier("dvmodbusir").category("foo");
    assert201(() -> client.createThingType(auth("inoa"), vo));
  }

  @Disabled("NYI")
  @Test
  @Override
  public void createThingType400() {}

  @Disabled("NYI")
  @Test
  @Override
  public void createThingType401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void createThingType409() {}

  @Disabled("NYI")
  @Test
  @Override
  public void deleteThingType204() {}

  @Disabled("NYI")
  @Test
  @Override
  public void deleteThingType401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void deleteThingType404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThingType200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThingType401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThingType404() {}

  @Disabled("NYI")
  @Override
  public void getThingTypes200() throws Exception {}

  @Disabled("NYI")
  @Override
  public void getThingTypes401() throws Exception {}

  @Disabled("NYI")
  @Override
  public void getThingTypes404() throws Exception {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThingType200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThingType400() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThingType401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThingType404() {}
}
