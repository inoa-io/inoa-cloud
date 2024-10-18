package io.inoa.measurement.things.rest;

import io.inoa.rest.ThingsApiTestClient;
import io.inoa.rest.ThingsApiTestSpec;
import io.inoa.test.AbstractUnitTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Things API Tests")
public class ThingsApiTest extends AbstractUnitTest implements ThingsApiTestSpec {

  @Inject ThingsApiTestClient client;

  @Disabled("NYI")
  @Test
  @Override
  public void createThing201() {}

  @Disabled("NYI")
  @Test
  @Override
  public void createThing400() {}

  @Disabled("NYI")
  @Test
  @Override
  public void createThing401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void createThing409() {}

  @Disabled("NYI")
  @Test
  @Override
  public void deleteThing204() {}

  @Disabled("NYI")
  @Test
  @Override
  public void deleteThing401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void deleteThing404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void downloadConfigToGateway200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void downloadConfigToGateway401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void downloadConfigToGateway404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void downloadConfigToGatewayLegacy200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void downloadConfigToGatewayLegacy401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void downloadConfigToGatewayLegacy404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThing200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThing401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThing404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThings200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThings401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThings404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThingsByGatewayId200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThingsByGatewayId401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void findThingsByGatewayId404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void syncConfigToGateway204() {}

  @Disabled("NYI")
  @Test
  @Override
  public void syncConfigToGateway401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void syncConfigToGateway404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void syncConfigToGatewaySequential204() {}

  @Disabled("NYI")
  @Test
  @Override
  public void syncConfigToGatewaySequential401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void syncConfigToGatewaySequential404() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThing200() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThing400() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThing401() {}

  @Disabled("NYI")
  @Test
  @Override
  public void updateThing404() {}
}
