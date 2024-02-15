package io.inoa.measurement.things.rest;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.AbstractUnitTest;
import io.inoa.rest.ThingTypeCreateVO;
import io.inoa.rest.ThingTypesApiTestClient;
import io.inoa.rest.ThingTypesApiTestSpec;
import jakarta.inject.Inject;

@DisplayName("ThingTypes API Tests")
public class ThingTypesApiTest extends AbstractUnitTest implements ThingTypesApiTestSpec {

	private @Inject ThingTypesApiTestClient client;

	@Test
	@Override
	public void createThingType201() {
		var response = client.createThingType(auth("inoa"), new ThingTypeCreateVO().name("DvModbusIR")
				.thingTypeId("dvmodbusir").jsonSchema(new HashMap<>()).uiLayout(new ArrayList<>()));
		Assertions.assertEquals(201, response.code());
	}

	@Override
	public void createThingType400() {}

	@Override
	public void createThingType401() {}

	@Override
	public void createThingType409() {}

	@Override
	public void deleteThingType204() {}

	@Override
	public void deleteThingType401() {}

	@Override
	public void deleteThingType404() {}

	@Override
	public void findThingType200() {}

	@Override
	public void findThingType401() {}

	@Override
	public void findThingType404() {}

	@Override
	public void findThingTypes200() {}

	@Override
	public void findThingTypes401() {}

	@Override
	public void findThingTypes404() {}

	@Override
	public void updateThingType200() {}

	@Override
	public void updateThingType400() {}

	@Override
	public void updateThingType401() {}

	@Override
	public void updateThingType404() {}
}
