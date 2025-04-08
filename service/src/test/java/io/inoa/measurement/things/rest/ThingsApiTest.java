package io.inoa.measurement.things.rest;

import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert404;

import io.inoa.measurement.things.domain.ObisId;
import io.inoa.rest.MeasurandVO;
import io.inoa.rest.ThingCreateVO;
import io.inoa.rest.ThingUpdateVO;
import io.inoa.rest.ThingsApiTestClient;
import io.inoa.rest.ThingsApiTestSpec;
import io.inoa.test.AbstractUnitTest;
import jakarta.inject.Inject;
import java.util.UUID;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@DisplayName("Things API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThingsApiTest extends AbstractUnitTest implements ThingsApiTestSpec {

  @Inject ThingsApiTestClient client;

  @Test
  @Order(1)
  @Override
  public void createThing201() {
    var tenant = data.tenant();
    var gateway = data.gateway();

    var thingCreateVO =
        new ThingCreateVO()
            .name("New Thing")
            .description("New created Thing for testing.")
            .gatewayId(gateway.getGatewayId())
            .thingTypeId("dvh4013")
            .addMeasurandsItem(
                new MeasurandVO()
                    .measurandType(ObisId.OBIS_1_8_0.getObisId())
                    .enabled(true)
                    .interval(60000)
                    .timeout(1000))
            .putConfigurationsItem("Serial", "33065393");

    assert201(() -> client.createThing(auth(tenant), thingCreateVO));
  }

  @Disabled("NYI")
  @Test
  @Order(2)
  @Override
  public void createThing400() {
    var tenant = data.tenant();
    var thingCreateVO = new ThingCreateVO();

    // TODO: Duplicate thing, config key does not exist, obis code cannot be used for this thing
    assert400(() -> client.createThing(auth(tenant), thingCreateVO));
  }

  @Test
  @Order(3)
  @Override
  public void createThing401() {
    assert401(() -> client.createThing(null, new ThingCreateVO()));
  }

  @Disabled("NYI")
  @Test
  @Order(4)
  @Override
  public void createThing409() {}

  @Disabled("NYI")
  @Test
  @Order(5)
  @Override
  public void updateThing200() {}

  @Disabled("NYI")
  @Test
  @Order(6)
  @Override
  public void updateThing400() {}

  @Test
  @Order(7)
  @Override
  public void updateThing401() {
    assert401(() -> client.updateThing(null, UUID.randomUUID(), new ThingUpdateVO()));
  }

  @Disabled("NYI")
  @Test
  @Order(8)
  @Override
  public void updateThing404() {}

  @Disabled("NYI")
  @Test
  @Order(9)
  @Override
  public void findThing200() {}

  @Test
  @Order(10)
  @Override
  public void findThing401() {
    assert401(() -> client.findThing(null, UUID.randomUUID()));
  }

  @Test
  @Order(11)
  @Override
  public void findThing404() {
    assert404(() -> client.findThing(auth("inoa"), UUID.randomUUID()));
  }

  @Disabled("NYI")
  @Test
  @Order(12)
  @Override
  public void findThings200() {}

  @Test
  @Order(13)
  @Override
  public void findThings401() {
    assert401(() -> client.findThings(null, null, null, null, null));
  }

  @Test
  @Order(14)
  @Override
  @Disabled("NYI")
  public void findThings404() {
    assert404(() -> client.findThings(auth("inoa"), null, null, null, null));
  }

  @Disabled("NYI")
  @Test
  @Order(15)
  @Override
  public void findThingsByGatewayId200() {}

  @Test
  @Order(16)
  @Override
  public void findThingsByGatewayId401() {
    assert401(
        () -> client.findThingsByGatewayId(null, data.gatewayId(), null, null, null, null, null));
  }

  @Test
  @Order(17)
  @Override
  @Disabled("NYI")
  public void findThingsByGatewayId404() {
    assert404(
        () ->
            client.findThingsByGatewayId(
                auth("inoa"), data.gatewayId(), null, null, null, null, null));
  }

  @Disabled("NYI")
  @Test
  @Order(18)
  @Override
  public void deleteThing204() {}

  @Test
  @Order(19)
  @Override
  public void deleteThing401() {
    assert401(() -> client.deleteThing(null, UUID.randomUUID()));
  }

  @Test
  @Order(20)
  @Override
  public void deleteThing404() {
    assert404(() -> client.deleteThing(auth("inoa"), UUID.randomUUID()));
  }

  @Disabled("NYI")
  @Order(21)
  @Test
  @Override
  public void syncThingsToGateway204() {}

  @Test
  @Order(22)
  @Override
  public void syncThingsToGateway401() {
    assert401(() -> client.syncThingsToGateway(null, "unknown"));
  }

  @Test
  @Order(23)
  @Override
  @Disabled("NYI")
  public void syncThingsToGateway404() {
    assert404(() -> client.syncThingsToGateway(auth("inoa"), data.gatewayId()));
  }
}
