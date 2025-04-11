package io.inoa.measurement.things.rest;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert204;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert404;
import static io.inoa.test.HttpAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.measurement.things.domain.ObisId;
import io.inoa.rest.MeasurandVO;
import io.inoa.rest.ThingCreateVO;
import io.inoa.rest.ThingUpdateVO;
import io.inoa.rest.ThingsApiTestClient;
import io.inoa.rest.ThingsApiTestSpec;
import io.inoa.test.AbstractUnitTest;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
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

  private static Tenant tenant;
  private static Gateway gateway;
  private static UUID thingId;

  @Inject ThingsApiTestClient client;

  @Override
  protected void cleanupDatabase(JdbcOperations jdbc) {
    // Do nothing
  }

  @Test
  @Order(1)
  @Override
  public void createThing201() {
    tenant = data.tenant();
    gateway = data.gateway(tenant);

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
                    .interval(60000L)
                    .timeout(1000L))
            .putConfigurationsItem("Serial", "33065393");

    var thingVO = assert201(() -> client.createThing(auth(tenant), thingCreateVO));

    thingId = thingVO.getId();
    assertEquals(
        thingCreateVO.getName(), thingVO.getName(), "Expected correct thing name to be created.");
    assertEquals(
        thingCreateVO.getDescription(),
        thingVO.getDescription(),
        "Expected correct thing description to be created.");
    assertEquals(
        thingCreateVO.getGatewayId(),
        gateway.getGatewayId(),
        "Expected correct gatewayId to be created.");
    assertEquals(
        thingCreateVO.getThingTypeId(),
        thingVO.getThingTypeId(),
        "Expected correct thingType to be created.");
    assertEquals(
        thingCreateVO.getMeasurands(),
        thingVO.getMeasurands(),
        "Expected correct measurands to be created.");
    assertEquals(
        thingCreateVO.getConfigurations(),
        thingVO.getConfigurations(),
        "Expected correct configs to be created.");
  }

  @Test
  @Order(2)
  @Override
  public void createThing400() {
    var tenant = data.tenant();
    var gateway = data.gateway(tenant);
    var thingCreateVO = new ThingCreateVO();
    thingCreateVO.setName("New Thing");
    thingCreateVO.setThingTypeId("dvh4013");

    // Gateway does not exist
    thingCreateVO.setGatewayId("ISRL02-1234567890");
    assertEquals(
        "Gateway not found: ISRL02-1234567890",
        assert400(() -> client.createThing(auth(tenant), thingCreateVO)).getMessage());
    thingCreateVO.setGatewayId(gateway.getGatewayId());

    // ThingType does not exist
    thingCreateVO.setThingTypeId("bielefeld");
    assertEquals(
        "No such thing type: bielefeld",
        assert400(() -> client.createThing(auth(tenant), thingCreateVO)).getMessage());
    thingCreateVO.setThingTypeId("dvh4013");

    // MeasurandType does not exist
    var measurand = new MeasurandVO().measurandType("bielefeld");
    thingCreateVO.addMeasurandsItem(measurand);
    assertEquals(
        "Invalid measurand types not supported by given thing type: [bielefeld]",
        assert400(() -> client.createThing(auth(tenant), thingCreateVO)).getMessage());
    thingCreateVO.removeMeasurandsItem(measurand);

    // ConfigKey does not exist
    thingCreateVO.putConfigurationsItem("bielefeld", "bielefeld");
    assertEquals(
        "Configuration keys that do not exist for given thing type: [bielefeld]",
        assert400(() -> client.createThing(auth(tenant), thingCreateVO)).getMessage());
  }

  @Test
  @Order(3)
  @Override
  public void createThing401() {
    assert401(() -> client.createThing(null, new ThingCreateVO()));
  }

  @Test
  @Order(4)
  @Override
  public void createThing409() {
    var thingCreateVO =
        new ThingCreateVO()
            .name("New Thing")
            .description("New created Thing for testing.")
            .gatewayId(gateway.getGatewayId())
            .thingTypeId("dvh4013");
    assert409(() -> client.createThing(auth(tenant), thingCreateVO));
  }

  @Disabled("NYI")
  @Test
  @Order(5)
  @Override
  public void updateThing200() {}

  @Test
  @Order(6)
  @Override
  public void updateThing400() {
    var thingUpdateVO = new ThingUpdateVO();
    thingUpdateVO.setName("Updated");
    thingUpdateVO.setDescription("Updated description");

    // Gateway does not exist
    thingUpdateVO.setGatewayId("ISRL01-0123456789");
    assertEquals(
        "Gateway not found: ISRL01-0123456789",
        assert400(() -> client.updateThing(auth(tenant), thingId, thingUpdateVO)).getMessage());
    thingUpdateVO.setGatewayId(gateway.getGatewayId());

    // MeasurandType does not exist
    thingUpdateVO.setMeasurands(
        List.of(
            new MeasurandVO()
                .measurandType("bielefeld")
                .enabled(true)
                .timeout(1000L)
                .interval(30000L)));
    assertEquals(
        "No such measurand type: bielefeld",
        assert400(() -> client.updateThing(auth(tenant), thingId, thingUpdateVO)).getMessage());
    thingUpdateVO.setMeasurands(null);

    // ConfigKey does not exist
    thingUpdateVO.setConfigurations(Map.of("bielefeld", "bielefeld"));
    assertEquals(
        "No such config name: bielefeld",
        assert400(() -> client.updateThing(auth(tenant), thingId, thingUpdateVO)).getMessage());
  }

  @Test
  @Order(7)
  @Override
  public void updateThing401() {
    assert401(() -> client.updateThing(null, UUID.randomUUID(), new ThingUpdateVO()));
  }

  @Test
  @Order(8)
  @Override
  public void updateThing404() {
    var thingUpdateVO = new ThingUpdateVO();
    thingUpdateVO.setName("Updated");
    thingUpdateVO.setDescription("Updated description");
    thingUpdateVO.setGatewayId(gateway.getGatewayId());

    assert404(() -> client.updateThing(auth(tenant), UUID.randomUUID(), thingUpdateVO));
  }

  @Test
  @Order(9)
  @Override
  public void findThing200() {
    var thing = assert200(() -> client.findThing(auth(tenant), thingId));
    assertEquals("New Thing", thing.getName(), "Expected correct thing name to be created.");
    assertEquals(
        "New created Thing for testing.",
        thing.getDescription(),
        "Expected correct thing description to be created.");
    assertEquals(
        gateway.getGatewayId(), thing.getGatewayId(), "Expected correct gatewayId to be created.");
    assertEquals("dvh4013", thing.getThingTypeId(), "Expected correct thingType to be created.");
    assertEquals(
        List.of(
            new MeasurandVO()
                .measurandType(ObisId.OBIS_1_8_0.getObisId())
                .enabled(true)
                .interval(60000L)
                .timeout(1000L)),
        thing.getMeasurands(),
        "Expected correct measurands to be created.");
    assertEquals(
        Map.of("Serial", "33065393"),
        thing.getConfigurations(),
        "Expected correct configs to be created.");
  }

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
    assert404(() -> client.findThing(auth(tenant), UUID.randomUUID()));
  }

  @Disabled("NYI")
  @Test
  @Order(12)
  @Override
  public void findThings200() {
    var things = assert200(() -> client.findThings(auth(tenant), null, null, null, null));
    assertEquals(1, things.getTotalSize(), "Expected 1 created thing");
  }

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
    assert404(() -> client.findThings(auth(tenant), null, null, null, null));
  }

  @Test
  @Order(15)
  @Override
  public void findThingsByGatewayId200() {
    var result =
        assert200(() -> client.findThingsByGatewayId(auth(tenant), gateway.getGatewayId()));
    assertEquals(1, result.size(), "Expected 1 found thing");
    assertEquals(thingId, result.get(0).getId(), "Expected correct thing id to be found");
  }

  @Test
  @Order(16)
  @Override
  public void findThingsByGatewayId401() {
    assert401(() -> client.findThingsByGatewayId(null, data.gatewayId()));
  }

  @Test
  @Order(17)
  @Override
  public void findThingsByGatewayId404() {
    assert404(() -> client.findThingsByGatewayId(auth(tenant), data.gatewayId()));
  }

  @Test
  @Order(18)
  @Override
  public void deleteThing204() {
    assert204(() -> client.deleteThing(auth(tenant), thingId));
  }

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
    assert404(() -> client.deleteThing(auth(tenant), UUID.randomUUID()));
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
  public void syncThingsToGateway404() {
    assert404(() -> client.syncThingsToGateway(auth(tenant), data.gatewayId()));
  }
}
