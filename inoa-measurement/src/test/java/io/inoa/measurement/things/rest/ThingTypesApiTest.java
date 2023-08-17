package io.inoa.measurement.things.rest;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.measurement.AbstractTest;
import io.inoa.measurement.api.ThingTypesApiTestClient;
import io.inoa.measurement.api.ThingTypesApiTestSpec;
import io.inoa.measurement.model.ThingTypeCreateVO;
import jakarta.inject.Inject;

@DisplayName("ThingTypes API Tests")
public class ThingTypesApiTest extends AbstractTest implements ThingTypesApiTestSpec {

	@Inject
	private ThingTypesApiTestClient client;

	@Test
	@Override
	public void createThingType201() throws Exception {
		var response = client.createThingType(auth("inoa"), new ThingTypeCreateVO().name("DvModbusIR")
				.thingTypeId("dvmodbusir").jsonSchema(new HashMap<>()).uiLayout(new ArrayList<>()));
		Assertions.assertEquals(201, response.code());
	}

	@Override
	public void createThingType400() throws Exception {

	}

	@Override
	public void createThingType401() throws Exception {

	}

	@Override
	public void createThingType409() throws Exception {

	}

	@Override
	public void deleteThingType204() throws Exception {

	}

	@Override
	public void deleteThingType401() throws Exception {

	}

	@Override
	public void deleteThingType404() throws Exception {

	}

	@Override
	public void findThingType200() throws Exception {

	}

	@Override
	public void findThingType401() throws Exception {

	}

	@Override
	public void findThingType404() throws Exception {

	}

	@Override
	public void findThingTypes200() throws Exception {

	}

	@Override
	public void findThingTypes401() throws Exception {

	}

	@Override
	public void findThingTypes404() throws Exception {

	}

	@Override
	public void updateThingType200() throws Exception {

	}

	@Override
	public void updateThingType400() throws Exception {

	}

	@Override
	public void updateThingType401() throws Exception {

	}

	@Override
	public void updateThingType404() throws Exception {

	}
}
