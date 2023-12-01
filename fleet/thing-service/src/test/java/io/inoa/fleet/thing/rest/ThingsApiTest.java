package io.inoa.fleet.thing.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
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
	public void createThing201() {}

	@Override
	public void createThing400() {}

	@Override
	public void createThing401() {}

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
	public void downloadConfigToGatewayLegacy200() throws Exception {

	}

	@Override
	public void downloadConfigToGatewayLegacy401() throws Exception {

	}

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
	public void findThingsByGatewayId200() {}

	@Override
	public void findThingsByGatewayId401() {}

	@DisplayName("management: things - sync to gateway 204")
	@Test
	@Override
	public void syncConfigToGateway204() {
		String gatewayId = "test";
		String tenantId = "inoa";
		wireMockServer.stubFor(post(String.format("/%s/%s/rpc", tenantId, gatewayId))
			.withHeader("Content-Type", containing("json")).willReturn(ok()));
		ThingType thingType = data.createThingType("", "dvh4013");
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
		assert204(() -> client.syncConfigToGateway(auth(), gatewayId));
		wireMockServer.verify(postRequestedFor(urlPathEqualTo(String.format("/%s/%s/rpc", tenantId, gatewayId))));
	}

	@DisplayName("management: things - sync to gateway with real big data 204 dvh4013")
	@Test
	public void syncConfigToGatewayRealBigData204() {
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
	public void syncConfigToGatewayRealBigData400() {
		String gatewayId = "test";
		String tenantId = "inoa";
		wireMockServer.stubFor(post(String.format("/%s/%s/rpc", tenantId, gatewayId))
			.withHeader("Content-Type", containing("json")).willReturn(ok()));
		ThingType thingType = data.createThingType("", "dvh4013");
		for (int i = 0; i < 121; i++) {
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
	public void syncConfigToGateway401() {
		assert401(() -> client.syncConfigToGateway(""));
	}

	@DisplayName("management: things - sync to gateway sequential 204")
	@Test
	@Override
	public void syncConfigToGatewaySequential204() throws Exception {
		String gatewayId = "test";
		String tenantId = "inoa";
		wireMockServer.stubFor(post(String.format("/%s/%s/rpc", tenantId, gatewayId))
			.withHeader("Content-Type", containing("json")).willReturn(ok()));
		ThingType thingType = data.createThingType("", "dvh4013");
		for (int i = 0; i < 3; i++) {
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
		HttpResponseAssertions.assert204(() -> client.syncConfigToGatewaySequential(auth(), gatewayId));
		wireMockServer.verify(exactly(1),
			postRequestedFor(urlPathEqualTo(String.format("/%s/%s/rpc", tenantId, gatewayId)))
				.withRequestBody(matchingJsonPath("$[?(@.method == 'dp.clear')]")));

		wireMockServer.verify(exactly(3),
			postRequestedFor(urlPathEqualTo(String.format("/%s/%s/rpc", tenantId, gatewayId)))
			.withRequestBody(matchingJsonPath("$[?("
				+ "@.method == 'dp.add' && "
				+ "@.params.id == 'urn:dvh4013:123456:0x4000' && "
				+ "@.params.type == 'RS485' && "
				+ "@.params.name == 'my_thing' && "
				+ "@.params.interval == 30000 && "
				+ "@.params.enabled == true && "
				+ "@.params.timeout == 1000 && "
				+ "@.params.interface == 1 && "
				+ "@.params.frame == '570340000002ddfd')]")));
	}

	@Override
	public void syncConfigToGatewaySequential401() throws Exception {

	}

	@Override
	public void updateThing200() {}

	@Override
	public void updateThing400() {}

	@Override
	public void updateThing401() {}

	@Override
	public void updateThing404() {}
}
