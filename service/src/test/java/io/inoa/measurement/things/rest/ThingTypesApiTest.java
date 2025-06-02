package io.inoa.measurement.things.rest;

import static io.inoa.test.HttpAssertions.assert200;
import static io.inoa.test.HttpAssertions.assert201;
import static io.inoa.test.HttpAssertions.assert204;
import static io.inoa.test.HttpAssertions.assert400;
import static io.inoa.test.HttpAssertions.assert401;
import static io.inoa.test.HttpAssertions.assert404;
import static io.inoa.test.HttpAssertions.assert409;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.measurement.things.domain.ObisId;
import io.inoa.rest.ThingConfigurationVO;
import io.inoa.rest.ThingTypeCategoryVO;
import io.inoa.rest.ThingTypeCreateVO;
import io.inoa.rest.ThingTypeProtocolVO;
import io.inoa.rest.ThingTypeUpdateVO;
import io.inoa.rest.ThingTypesApiTestClient;
import io.inoa.rest.ThingTypesApiTestSpec;
import io.inoa.test.AbstractUnitTest;

@DisplayName("ThingTypes API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThingTypesApiTest extends AbstractUnitTest implements ThingTypesApiTestSpec {

	@Inject
	ThingTypesApiTestClient client;

	@Test
	@Order(1)
	@Override
	public void createThingType201() {
		var thingTypeVO = new ThingTypeCreateVO()
				.identifier("sct-013-030")
				.name("New thing type")
				.description("Created new thing type for testing")
				.category(ThingTypeCategoryVO.ELECTRIC_METER)
				.protocol(ThingTypeProtocolVO.MODBUS_RS458)
				.addConfigurationsItem(
						new ThingConfigurationVO()
								.name("Serial")
								.description("Serial number")
								.type(ThingConfigurationVO.Type.STRING)
								.validationRegex("$(0-9)[5]^"))
				.addMeasurandsItem(ObisId.OBIS_CURRENT.getObisId());

		assert201(() -> client.createThingType(auth("inoa"), thingTypeVO));
	}

	@Test
	@Order(2)
	@Override
	public void createThingType400() {
		var thingTypeVO = new ThingTypeCreateVO();
		assert400(() -> client.createThingType(auth("inoa"), thingTypeVO));
	}

	@Test
	@Order(3)
	@Override
	public void createThingType401() {
		assert401(() -> client.createThingType(null, new ThingTypeCreateVO()));
	}

	@Test
	@Order(4)
	@Override
	public void createThingType409() {
		var thingTypeVO = new ThingTypeCreateVO()
				.identifier("sct-013-030")
				.name("New thing type")
				.description("Created new thing type for testing")
				.category(ThingTypeCategoryVO.ELECTRIC_METER)
				.protocol(ThingTypeProtocolVO.MODBUS_RS458);
		assert409(() -> client.createThingType(auth("inoa"), thingTypeVO));
	}

	@Test
	@Order(5)
	@Override
	public void updateThingType200() {
		var thingTypeUpdateVO = new ThingTypeUpdateVO()
				.name("SCT-013-030")
				.description("Current transformer 1V = 30A")
				.category(ThingTypeCategoryVO.CURRENT_TRANSFORMER)
				.protocol(ThingTypeProtocolVO.RMS)
				.addConfigurationsItem(
						new ThingConfigurationVO()
								.name("Linear Correction")
								.description("Correction factor for linear adjustment")
								.type(ThingConfigurationVO.Type.NUMBER));
		assert200(() -> client.updateThingType(auth("inoa"), "sct-013-030", thingTypeUpdateVO));
	}

	@Test
	@Order(6)
	@Override
	public void updateThingType400() {
		var thingTypeUpdateVO = new ThingTypeUpdateVO();
		assert400(() -> client.updateThingType(auth("inoa"), "sct-013-030", thingTypeUpdateVO));
	}

	@Test
	@Order(7)
	@Override
	public void updateThingType401() {
		assert401(() -> client.updateThingType(null, "unknown", new ThingTypeUpdateVO()));
	}

	@Test
	@Order(8)
	@Override
	public void updateThingType404() {
		var thingTypeUpdateVO = new ThingTypeUpdateVO()
				.name("SCT-013-030")
				.description("Current transformer 1V = 30A")
				.category(ThingTypeCategoryVO.CURRENT_TRANSFORMER)
				.protocol(ThingTypeProtocolVO.RMS);
		// TODO: Also update measurands and configs
		assert404(() -> client.updateThingType(auth("inoa"), "unknown", thingTypeUpdateVO));
	}

	@Test
	@Order(9)
	@Override
	public void findThingType200() {
		var thingType = assert200(() -> client.findThingType(auth("inoa"), "sct-013-030"));
		assertEquals("sct-013-030", thingType.getIdentifier(), "Identifier shall match.");
		assertEquals("SCT-013-030", thingType.getName(), "Name shall match.");
		assertEquals(
				"Current transformer 1V = 30A", thingType.getDescription(), "Description shall match.");
		assertNull(thingType.getVersion(), "Version shall be null");
		assertEquals(
				ThingTypeCategoryVO.CURRENT_TRANSFORMER, thingType.getCategory(), "Category shall match.");
		assertEquals(ThingTypeProtocolVO.RMS, thingType.getProtocol(), "Protocol shall match.");
		assertEquals(2, thingType.getConfigurations().size(), "Configurations shall be loaded.");
		assertEquals(1, thingType.getMeasurands().size(), "Measurands shall be loaded.");
	}

	@Test
	@Order(10)
	@Override
	public void findThingType401() {
		assert401(() -> client.findThingType(null, "unknown"));
	}

	@Test
	@Order(11)
	@Override
	public void findThingType404() {
		assert404(() -> client.findThingType(auth("inoa"), "unknown"));
	}

	@Test
	@Order(12)
	@Override
	public void getThingTypes200() {
		var thingTypes = assert200(() -> client.getThingTypes(auth("inoa")));
		assertNotNull(thingTypes, "ThingTypes shall be loaded.");
		assertFalse(thingTypes.isEmpty(), "ThingTypes shall not be empty.");
	}

	@Test
	@Order(13)
	@Override
	public void getThingTypes401() {
		assert401(() -> client.getThingTypes(null));
	}

	@Test
	@Order(14)
	@Override
	public void deleteThingType204() {
		assert204(() -> client.deleteThingType(auth("inoa"), "sct-013-030"));
	}

	@Test
	@Order(15)
	@Override
	public void deleteThingType401() {
		assert401(() -> client.deleteThingType(null, "unknown"));
	}

	@Test
	@Order(16)
	@Override
	public void deleteThingType404() {
		assert404(() -> client.findThingType(auth("inoa"), "unknown"));
	}

	@Test
	@Order(17)
	@Override
	public void findMeasurandTypes200() {
		var result = assert200(() -> client.findMeasurandTypes(auth("inoa")));
		assertNotNull(result, "Measurand types shall be loaded.");
		assertFalse(result.isEmpty(), "Measurand types shall not be empty.");
		assertEquals("0.2.0", result.iterator().next().getObisId());
		assertEquals("Firmware version", result.iterator().next().getName());
		assertEquals("Firmware of the meter", result.iterator().next().getDescription());
	}

	@Test
	@Order(18)
	@Override
	public void findMeasurandTypes401() {
		assert401(() -> client.findMeasurandTypes(null));
	}
}
