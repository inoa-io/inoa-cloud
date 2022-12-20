package io.inoa.fleet.thing.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	@Inject
	ObjectMapper opObjectMapper;
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
		wireMockServer.stubFor(post(String.format("/%s/%s/rpc", tenantId, gatewayId))
				.withHeader("Content-Type", containing("json")).willReturn(ok()));

		ThingType thingType = data.createThingType("", "dvh4013");
		data.createThing(gatewayId, tenantId, thingType);
		assert204(() -> client.syncConfigToGateway(auth(), gatewayId));
		wireMockServer.verify(postRequestedFor(urlPathEqualTo(String.format("/%s/%s/rpc", tenantId, gatewayId))));
	}

	@DisplayName("management: things - sync to gateway with real big data 204 dvh4013")
	@Test
	public void syncConfigToGatewayRealBigData204() throws Exception {
		String gatewayId = "test";
		String tenantId = "inoa";
		wireMockServer.stubFor(post(String.format("/%s/%s/rpc", tenantId, gatewayId))
				.withHeader("Content-Type", containing("json")).willReturn(ok()));

		ThingType thingType = data.createThingType("", "dvh4013");

		for (int i = 0; i < 13; i++) {
			Map<String, Object> config = new HashMap<>();
			HashMap<String, Object> properties = new HashMap<>();
			properties.put("serial", 123456);
			properties.put("modbus_interface", 1);
			config.put("properties", properties);
			HashMap<String, Object> channels = new HashMap<>();
			channels.put("power_in", true);
			channels.put("power_out", true);
			channels.put("obis_1_8_0", true);
			channels.put("obis_2_8_0", true);
			config.put("channels", channels);
			data.createThing(gatewayId, tenantId, thingType, config);
		}
		assert204(() -> client.syncConfigToGateway(auth(), gatewayId));
		wireMockServer.verify(postRequestedFor(urlPathEqualTo(String.format("/%s/%s/rpc", tenantId, gatewayId))));
	}

	@DisplayName("management: things - sync to gateway with real big data 400 dvh4013")
	@Test
	public void syncConfigToGatewayRealBigData400() throws Exception {
		String gatewayId = "test";
		String tenantId = "inoa";
		wireMockServer.stubFor(post(String.format("/%s/%s/rpc", tenantId, gatewayId))
				.withHeader("Content-Type", containing("json")).willReturn(ok()));

		ThingType thingType = data.createThingType("", "dvh4013");

		for (int i = 0; i < 14; i++) {
			Map<String, Object> config = new HashMap<>();
			HashMap<String, Object> properties = new HashMap<>();
			properties.put("serial", 123456);
			properties.put("modbus_interface", 1);
			config.put("properties", properties);
			HashMap<String, Object> channels = new HashMap<>();
			channels.put("power_in", true);
			channels.put("power_out", true);
			channels.put("obis_1_8_0", true);
			channels.put("obis_2_8_0", true);
			config.put("channels", channels);
			data.createThing(gatewayId, tenantId, thingType, config);
		}
		HttpResponseAssertions.assert400(() -> client.syncConfigToGateway(auth(), gatewayId));
		wireMockServer.verify(exactly(0),
				postRequestedFor(urlPathEqualTo(String.format("/%s/%s/rpc", tenantId, gatewayId))));
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
