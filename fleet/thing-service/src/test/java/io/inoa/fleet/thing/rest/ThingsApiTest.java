package io.inoa.fleet.thing.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import io.inoa.fleet.thing.AbstractTest;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.rest.management.ThingsApiTestClient;
import io.inoa.fleet.thing.rest.management.ThingsApiTestSpec;
import jakarta.inject.Inject;

@DisplayName("management: things")
public class ThingsApiTest extends AbstractTest implements ThingsApiTestSpec {

	@Inject
	ThingsApiTestClient client;
	WireMockServer wireMockServer;

	@BeforeEach
	public void setup() {
		wireMockServer = new WireMockServer(wireMockConfig().port(34001)); // No-args constructor will
		wireMockServer.start();
	}

	@AfterEach
	public void teardown() {
		if (wireMockServer != null) {
			wireMockServer.stop();
		}
	}

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
	public void findThingsByGatewayId200() throws Exception {

	}

	@Override
	public void findThingsByGatewayId401() throws Exception {

	}

	@DisplayName("management: things - sync to gateway 204")
	@Test
	@Override
	public void syncConfigToGateway204() throws Exception {
		String gatewayId = "test";
		String tenantId = "inoa";
		wireMockServer.stubFor(
				post(String.format("/%s/%s/rpc", tenantId, gatewayId)).withHeader("Content-Type", containing("json"))
						.willReturn(ok()));

		ThingType thingType = data.createThingType("", "");
		data.createThing(gatewayId, tenantId, thingType);
		assert204(() -> client.syncConfigToGateway(auth(), gatewayId));
		wireMockServer.verify(postRequestedFor(urlPathEqualTo(String.format("/%s/%s/rpc", tenantId, gatewayId))));
	}

	@DisplayName("management: things - sync to gateway 401")
	@Test
	@Override
	public void syncConfigToGateway401() throws Exception {
		assert401(() -> client.syncConfigToGateway(""));
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
