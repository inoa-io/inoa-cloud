package io.inoa.measurement.things.rest;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert204;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert404;
import static io.inoa.test.HttpAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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

@DisplayName("Things API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThingsApiTest extends AbstractUnitTest implements ThingsApiTestSpec {

	private static Tenant tenant;
	private static Gateway gateway;
	private static Gateway updatedGateway;
	private static UUID thingId;

	@Inject
	ThingsApiTestClient client;

	@Override
	protected void cleanupDatabase(JdbcOperations jdbc) {
		// Do nothing - tests are depended on and created stuff shall remain
	}

	@Test
	@Order(1)
	@Override
	public void createThing201() {
		tenant = data.tenant();
		gateway = data.gateway(tenant);

		var thingCreateVO = new ThingCreateVO()
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
				.addMeasurandsItem(
						new MeasurandVO()
								.measurandType(ObisId.OBIS_2_8_0.getObisId())
								.enabled(true)
								.interval(60000L)
								.timeout(1000L))
				.putConfigurationsItem("Serial", "33065393")
				.putConfigurationsItem("Modbus Interface", "1");

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
				thingCreateVO.getMeasurands().stream()
						.sorted(Comparator.comparing(MeasurandVO::getMeasurandType))
						.toList(),
				thingVO.getMeasurands().stream()
						.sorted(Comparator.comparing(MeasurandVO::getMeasurandType))
						.toList(),
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
		var thingCreateVO = new ThingCreateVO();
		thingCreateVO.setName("New Thing2");
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
		thingCreateVO.removeConfigurationsItem("bielefeld");

		// ConfigKey does not match regex
		thingCreateVO.putConfigurationsItem("Serial", "bielefeld");
		assertEquals(
				"Some configuration values are invalid: ['bielefeld' does not match regular expression:"
						+ " [0-9]*]",
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
		var thingCreateVO = new ThingCreateVO()
				.name("New Thing")
				.description("New created Thing for testing.")
				.gatewayId(gateway.getGatewayId())
				.thingTypeId("dvh4013");
		assert409(() -> client.createThing(auth(tenant), thingCreateVO));
	}

	@Test
	@Order(5)
	@Override
	public void updateThing200() {
		updatedGateway = data.gateway(tenant);
		ThingUpdateVO thingUpdateVO = new ThingUpdateVO();
		thingUpdateVO.setName("Updated Thing");
		thingUpdateVO.setDescription("Updated description");
		thingUpdateVO.setGatewayId(updatedGateway.getGatewayId());
		thingUpdateVO
				.addMeasurandsItem(
						new MeasurandVO()
								.measurandType(ObisId.OBIS_1_8_0.getObisId())
								.enabled(true)
								.interval(30000L)
								.timeout(2000L))
				.addMeasurandsItem(
						new MeasurandVO()
								.measurandType(ObisId.OBIS_FREQUENCY.getObisId())
								.enabled(true)
								.interval(60000L)
								.timeout(1000L))
				.putConfigurationsItem("Serial", "33065394");

		var updatedThing = assert200(() -> client.updateThing(auth(tenant), thingId, thingUpdateVO));

		assertEquals("Updated Thing", updatedThing.getName(), "Thing name shall be updated");
		assertEquals(
				"Updated description", updatedThing.getDescription(), "Thing description shall be updated");
		assertEquals(
				updatedGateway.getGatewayId(), updatedThing.getGatewayId(), "Gateway id shall be updated");
		assertEquals(2, updatedThing.getMeasurands().size(), "Measurands size shall be updated");
		assertEquals(
				ObisId.OBIS_1_8_0.getObisId(),
				updatedThing.getMeasurands().get(0).getMeasurandType(),
				"Measurands size shall be updated");
		assertEquals(
				30000L,
				updatedThing.getMeasurands().get(0).getInterval(),
				"Measurands size shall be updated");
		assertEquals(
				2000L,
				updatedThing.getMeasurands().get(0).getTimeout(),
				"Measurands size shall be updated");
		assertEquals(
				ObisId.OBIS_FREQUENCY.getObisId(),
				updatedThing.getMeasurands().get(1).getMeasurandType(),
				"Measurands size shall be updated");
		assertEquals(
				60000L,
				updatedThing.getMeasurands().get(1).getInterval(),
				"Measurands size shall be updated");
		assertEquals(
				1000L,
				updatedThing.getMeasurands().get(1).getTimeout(),
				"Measurands size shall be updated");

		assertEquals(
				1, updatedThing.getConfigurations().size(), "Configurations size shall be updated");
		assertEquals(
				"33065394",
				updatedThing.getConfigurations().get("Serial"),
				"Configurations size shall be updated");
	}

	@Test
	@Order(6)
	@Override
	public void updateThing400() {
		var thingUpdateVO = new ThingUpdateVO();
		thingUpdateVO.setName("Invalid");
		thingUpdateVO.setDescription("Invalid description");

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

		// ConfigKey does not match regex
		thingUpdateVO.setConfigurations(Map.of("Serial", "bielefeld"));
		assertEquals(
				"Config value 'bielefeld' does not match regex '[0-9]*'",
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
		thingUpdateVO.setName("Unknown");
		thingUpdateVO.setDescription("Unknown description");
		thingUpdateVO.setGatewayId(gateway.getGatewayId());

		assert404(() -> client.updateThing(auth(tenant), UUID.randomUUID(), thingUpdateVO));
	}

	@Test
	@Order(9)
	@Override
	public void findThing200() {
		var thing = assert200(() -> client.findThing(auth(tenant), thingId));
		assertEquals("Updated Thing", thing.getName(), "Expected correct thing name to be created.");
		assertEquals(
				"Updated description",
				thing.getDescription(),
				"Expected correct thing description to be created.");
		assertEquals(
				updatedGateway.getGatewayId(),
				thing.getGatewayId(),
				"Expected correct gatewayId to be created.");
		assertEquals("dvh4013", thing.getThingTypeId(), "Expected correct thingType to be created.");
		assertEquals(
				List.of(
						new MeasurandVO()
								.measurandType(ObisId.OBIS_1_8_0.getObisId())
								.enabled(true)
								.interval(30000L)
								.timeout(2000L),
						new MeasurandVO()
								.measurandType(ObisId.OBIS_FREQUENCY.getObisId())
								.enabled(true)
								.interval(60000L)
								.timeout(1000L)),
				thing.getMeasurands(),
				"Expected correct measurands to be created.");
		assertEquals(
				Map.of("Serial", "33065394"),
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

	@Test
	@Order(15)
	@Override
	public void findThingsByGatewayId200() {
		var result = assert200(() -> client.findThingsByGatewayId(auth(tenant), updatedGateway.getGatewayId()));
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
