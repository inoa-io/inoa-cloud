package io.inoa.measurement.things.rest;

import org.junit.jupiter.api.DisplayName;

import io.inoa.AbstractUnitTest;
import io.inoa.rest.ThingsApiTestClient;
import io.inoa.rest.ThingsApiTestSpec;
import jakarta.inject.Inject;

@DisplayName("Things API Tests")
public class ThingsApiTest extends AbstractUnitTest implements ThingsApiTestSpec {

	private @Inject ThingsApiTestClient client;

	@Override
	public void createThing201() {}

	@Override
	public void createThing400() {}

	@Override
	public void createThing401() {}

	@Override
	public void createThing409() {}

	@Override
	public void deleteThing204() {}

	@Override
	public void deleteThing401() {}

	@Override
	public void deleteThing404() {}

	@Override
	public void downloadConfigToGateway200() {}

	@Override
	public void downloadConfigToGateway401() {}

	@Override
	public void downloadConfigToGateway404() {}

	@Override
	public void downloadConfigToGatewayLegacy200() {}

	@Override
	public void downloadConfigToGatewayLegacy401() {}

	@Override
	public void downloadConfigToGatewayLegacy404() {}

	@Override
	public void findThing200() {}

	@Override
	public void findThing401() {}

	@Override
	public void findThing404() {}

	@Override
	public void findThings200() {}

	@Override
	public void findThings401() {}

	@Override
	public void findThings404() {}

	@Override
	public void findThingsByGatewayId200() {}

	@Override
	public void findThingsByGatewayId401() {}

	@Override
	public void findThingsByGatewayId404() {}

	@Override
	public void syncConfigToGateway204() {}

	@Override
	public void syncConfigToGateway401() {}

	@Override
	public void syncConfigToGateway404() {}

	@Override
	public void syncConfigToGatewaySequential204() {}

	@Override
	public void syncConfigToGatewaySequential401() {}

	@Override
	public void syncConfigToGatewaySequential404() {}

	@Override
	public void updateThing200() {}

	@Override
	public void updateThing400() {}

	@Override
	public void updateThing401() {}

	@Override
	public void updateThing404() {}
}
