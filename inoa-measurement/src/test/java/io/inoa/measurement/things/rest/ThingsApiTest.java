package io.inoa.measurement.things.rest;

import org.junit.jupiter.api.DisplayName;

import io.inoa.measurement.AbstractTest;
import io.inoa.measurement.api.ThingsApiTestClient;
import io.inoa.measurement.api.ThingsApiTestSpec;
import jakarta.inject.Inject;

@DisplayName("Things API Tests")
public class ThingsApiTest extends AbstractTest implements ThingsApiTestSpec {

	@Inject
	private ThingsApiTestClient thingsApiTestClient;

	@Override
	public void createThing201() throws Exception {

	}

	@Override
	public void createThing400() throws Exception {

	}

	@Override
	public void createThing401() throws Exception {

	}

	@Override
	public void createThing409() throws Exception {

	}

	@Override
	public void deleteThing204() throws Exception {

	}

	@Override
	public void deleteThing401() throws Exception {

	}

	@Override
	public void deleteThing404() throws Exception {

	}

	@Override
	public void downloadConfigToGateway200() throws Exception {

	}

	@Override
	public void downloadConfigToGateway401() throws Exception {

	}

	@Override
	public void downloadConfigToGateway404() throws Exception {

	}

	@Override
	public void downloadConfigToGatewayLegacy200() throws Exception {

	}

	@Override
	public void downloadConfigToGatewayLegacy401() throws Exception {

	}

	@Override
	public void downloadConfigToGatewayLegacy404() throws Exception {

	}

	@Override
	public void findThing200() throws Exception {

	}

	@Override
	public void findThing401() throws Exception {

	}

	@Override
	public void findThing404() throws Exception {

	}

	@Override
	public void findThings200() throws Exception {

	}

	@Override
	public void findThings401() throws Exception {

	}

	@Override
	public void findThings404() throws Exception {

	}

	@Override
	public void findThingsByGatewayId200() throws Exception {

	}

	@Override
	public void findThingsByGatewayId401() throws Exception {

	}

	@Override
	public void findThingsByGatewayId404() throws Exception {

	}

	@Override
	public void syncConfigToGateway204() throws Exception {

	}

	@Override
	public void syncConfigToGateway401() throws Exception {

	}

	@Override
	public void syncConfigToGateway404() throws Exception {

	}

	@Override
	public void syncConfigToGatewaySequential204() throws Exception {

	}

	@Override
	public void syncConfigToGatewaySequential401() throws Exception {

	}

	@Override
	public void syncConfigToGatewaySequential404() throws Exception {

	}

	@Override
	public void updateThing200() throws Exception {

	}

	@Override
	public void updateThing400() throws Exception {

	}

	@Override
	public void updateThing401() throws Exception {

	}

	@Override
	public void updateThing404() throws Exception {

	}
}
