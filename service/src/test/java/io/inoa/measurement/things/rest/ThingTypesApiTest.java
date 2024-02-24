package io.inoa.measurement.things.rest;

import static io.inoa.test.HttpAssertions.assert201;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.rest.ThingTypeCreateVO;
import io.inoa.rest.ThingTypesApiTestClient;
import io.inoa.rest.ThingTypesApiTestSpec;
import io.inoa.test.AbstractUnitTest;
import jakarta.inject.Inject;

@DisplayName("ThingTypes API Tests")
public class ThingTypesApiTest extends AbstractUnitTest implements ThingTypesApiTestSpec {

	@Inject ThingTypesApiTestClient client;

	@Test
	@Override
	public void createThingType201() {
		var vo = new ThingTypeCreateVO()
				.name("DvModbusIR")
				.thingTypeId("dvmodbusir")
				.jsonSchema(new HashMap<>())
				.uiLayout(new ArrayList<>());
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
	@Test
	@Override
	public void findThingTypes200() {}

	@Disabled("NYI")
	@Test
	@Override
	public void findThingTypes401() {}

	@Disabled("NYI")
	@Test
	@Override
	public void findThingTypes404() {}

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
