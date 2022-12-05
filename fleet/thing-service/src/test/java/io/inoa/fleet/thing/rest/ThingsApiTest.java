package io.inoa.fleet.thing.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static io.inoa.fleet.thing.rest.HttpResponseAssertions.assert204;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.inoa.fleet.thing.AbstractTest;
import io.inoa.fleet.thing.domain.Property;
import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingChannel;
import io.inoa.fleet.thing.domain.ThingChannelRepository;
import io.inoa.fleet.thing.domain.ThingRepository;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeChannel;
import io.inoa.fleet.thing.domain.ThingTypeChannelRepository;
import io.inoa.fleet.thing.domain.ThingTypeRepository;
import io.inoa.fleet.thing.rest.management.PropertyVO;
import io.inoa.fleet.thing.rest.management.ThingChannelVO;
import io.inoa.fleet.thing.rest.management.ThingCreateVO;
import io.inoa.fleet.thing.rest.management.ThingUpdateVO;
import io.inoa.fleet.thing.rest.management.ThingsApiTestClient;
import io.inoa.fleet.thing.rest.management.ThingsApiTestSpec;
import jakarta.inject.Inject;

public class ThingsApiTest extends AbstractTest implements ThingsApiTestSpec {

	@Inject
	ThingsApiTestClient client;
	@Inject
	ThingRepository thingRepository;
	@Inject
	ThingChannelRepository thingChannelRepository;
	@Inject
	ThingTypeRepository thingTypeRepository;
	@Inject
	ThingTypeChannelRepository thingTypeChannelRepository;

	WireMockServer wireMockServer;

	@BeforeEach
	public void setup() {
		wireMockServer = new WireMockServer(WireMockConfiguration.options().port(35000));

		wireMockServer.start();
	}

	@AfterEach
	public void teardown() {
		wireMockServer.stop();
	}

	protected String newGatewayId() {
		return UUID.randomUUID().toString();
	}

	@Test
	@Override
	public void createThing201() throws Exception {

		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);

		var thingTypeCreateVO = new ThingCreateVO().thingTypeId(thingType.getThingTypeId()).name("test")
				.gatewayId(newGatewayId());
		thingTypeCreateVO.addPropertiesItem(new PropertyVO().key("test").value(1));
		thingTypeCreateVO.addChannelsItem(
				new ThingChannelVO().key("power").addPropertiesItem(new PropertyVO().key("serial").value("test")));

		var created = assert201(() -> client.createThing(auth("test"), thingTypeCreateVO));
		Optional<Thing> thing = thingRepository.findByThingId(created.getId());
		assertTrue(thing.isPresent());
		assertNotNull(thing.get().getGatewayId());
		assertNotNull(thing.get().getThingId());
		assertNotNull(thing.get().getProperties());
		assertEquals(1, thing.get().getProperties().size());
		assertEquals("test", thing.get().getProperties().get(0).getKey());
		assertEquals(1, thing.get().getProperties().get(0).getValue());
		List<ThingChannel> channels = thingChannelRepository.findByThing(thing.get());
		assertEquals(1, channels.size());
	}

	@Test
	@Override
	public void createThing400() throws Exception {
		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);

		var thingTypeCreateVO = new ThingCreateVO().thingTypeId(thingType.getThingTypeId())
				.gatewayId(newGatewayId());
		assert400(() -> client.createThing(auth("test"), thingTypeCreateVO));
	}

	@Test
	@Override
	public void createThing401() throws Exception {
		var thingTypeCreateVO = new ThingCreateVO().gatewayId(newGatewayId());
		assert401(() -> client.createThing(null, thingTypeCreateVO));
	}

	@Test
	@Override
	public void deleteThing204() throws Exception {
		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);
		List<Property> properties = new ArrayList<>();
		properties.add(new Property().setValue(1).setKey("inoa_test"));
		var thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID())
				.setGatewayId(newGatewayId()).setProperties(properties).setTenantId("test");
		Thing finalThing = thingRepository.save(thing);
		assert204(() -> client.deleteThing(auth("test"), finalThing.getThingId()));
	}

	@Test
	@Override
	public void deleteThing401() throws Exception {
		assert401(() -> client.deleteThing(null, UUID.randomUUID()));
	}

	@Test
	@Override
	public void deleteThing404() throws Exception {
		assert404("Not Found", () -> client.deleteThing(auth("test"), UUID.randomUUID()));
	}

	@Override
	public void downloadConfigToGateway200() throws Exception {

	}

	@Override
	public void downloadConfigToGateway401() throws Exception {

	}

	@Test
	@Override
	public void findThing200() throws Exception {
		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);
		List<Property> properties = new ArrayList<>();
		properties.add(new Property().setValue(1).setKey("inoa_test"));
		var thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID())
				.setGatewayId(newGatewayId()).setProperties(properties).setTenantId("test");
		thingRepository.save(thing);

		var thingVo = assert200(() -> client.findThing(auth("test"), thing.getThingId()));
		assertEquals(thingType.getThingTypeId(), thingVo.getThingTypeId());
	}

	@Test
	@Override
	public void findThing401() throws Exception {
		assert401(() -> client.findThing(null, UUID.randomUUID()));
	}

	@Test
	@Override
	public void findThing404() throws Exception {
		assert404("Not Found", () -> client.findThing(auth("test"), UUID.randomUUID()));
	}

	@Test
	@Override
	public void findThings200() throws Exception {
		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);
		List<Property> properties = new ArrayList<>();
		properties.add(new Property().setValue(1).setKey("inoa_test"));
		var thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID())
				.setGatewayId(newGatewayId()).setProperties(properties).setTenantId("test");
		thingRepository.save(thing);
		thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID()).setTenantId("test2");
		thingRepository.save(thing);
		var list = assert200(() -> client.findThings(auth("test"), null, null, null, null));
		assertEquals(1, list.getContent().size());
		assertNotNull(list.getContent().get(0).getProperties());
		assertEquals(1, list.getContent().get(0).getProperties().size());
		assertEquals("inoa_test", list.getContent().get(0).getProperties().get(0).getKey());
		assertEquals(1, list.getContent().get(0).getProperties().get(0).getValue());
		list = assert200(() -> client.findThings(auth("test2"), null, null, null, null));
		assertEquals(1, list.getContent().size());
	}

	@Test
	@Override
	public void findThings401() throws Exception {
		assert401(() -> client.findThings(null, null, null, null, null));
	}

	@Test
	@Override
	public void findThingsByGatewayId200() throws Exception {
		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);
		var gatewayId = newGatewayId();
		var thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID())
				.setGatewayId(gatewayId).setTenantId("test");
		thingRepository.save(thing);
		thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID()).setTenantId("test");
		thingRepository.save(thing);

		var list = assert200(() -> client.findThingsByGatewayId(auth("test"), gatewayId, null, null, null, null));
		assertEquals(1, list.getContent().size());
	}

	@Test
	@Override
	public void findThingsByGatewayId401() throws Exception {
		assert401(() -> client.findThingsByGatewayId(null, UUID.randomUUID().toString(), null, null, null, null));
	}

	@Test
	@Override
	public void syncConfigToGateway204() throws Exception {

		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);
		List<Property> properties = new ArrayList<>();
		String gatewayId = newGatewayId();
		properties.add(new Property().setValue("test").setKey("serial"));
		properties.add(new Property().setValue(1).setKey("slaveId"));
		var thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID())
				.setGatewayId(gatewayId).setProperties(properties).setTenantId("test");
		thingRepository.save(thing);

		ThingChannel thingChannel = new ThingChannel().setThing(thing).setKey("power")
				.setThingChannelId(UUID.randomUUID());
		thingChannelRepository.save(thingChannel);

		ThingTypeChannel powerChannel = new ThingTypeChannel().setKey("power").setName("power")
				.setThingTypeChannelId(UUID.randomUUID()).setThingType(thingType);
		powerChannel.getProperties().add(new Property().setKey("name").setValue("power"));
		powerChannel.getProperties().add(new Property().setKey("urnPrefix").setValue("dvh4013"));
		powerChannel.getProperties().add(new Property().setKey("urnPostfix").setValue("0x4001"));
		powerChannel.getProperties().add(new Property().setKey("registerOffset").setValue(16385));
		thingTypeChannelRepository.save(powerChannel);
		String auth = auth("test");
		wireMockServer.stubFor(post(urlEqualTo(String.format("/api/iot-commands/command/%s/%s", "test", gatewayId)))
				.withHeader("Authorization", equalTo(auth))
				.willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("{}")));

		assert204(() -> client.syncConfigToGateway(auth, gatewayId));
		wireMockServer.verify(
				postRequestedFor(urlEqualTo(String.format("/api/iot-commands/command/%s/%s", "test", gatewayId)))
						.withHeader("Authorization", equalTo(auth)));
	}

	@Test
	@Override
	public void syncConfigToGateway401() throws Exception {
		assert401(() -> client.syncConfigToGateway(null, UUID.randomUUID().toString()));
	}

	@Test
	@Override
	public void updateThing200() throws Exception {
		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);
		List<Property> properties = new ArrayList<>();
		String gatewayId = newGatewayId();
		properties.add(new Property().setValue("test").setKey("serial"));
		properties.add(new Property().setValue(1).setKey("slaveId"));
		var thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID())
				.setGatewayId(gatewayId).setProperties(properties).setTenantId("test");
		Thing finalThing = thingRepository.save(thing);
		assert200(() -> client.updateThing(auth("test"), finalThing.getThingId(), new ThingUpdateVO().name("test2")));

		Optional<Thing> savedThing = thingRepository.findByThingId(thing.getThingId());
		assertTrue(savedThing.isPresent());
		assertEquals("test2", savedThing.get().getName());
	}

	@Test
	@Override
	public void updateThing400() throws Exception {
		ThingType thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingType = thingTypeRepository.save(thingType);
		List<Property> properties = new ArrayList<>();
		String gatewayId = newGatewayId();
		properties.add(new Property().setValue("test").setKey("serial"));
		properties.add(new Property().setValue(1).setKey("slaveId"));
		var thing = new Thing().setThingType(thingType).setName("test").setThingId(UUID.randomUUID())
				.setGatewayId(gatewayId).setProperties(properties).setTenantId("test");
		Thing finalThing = thingRepository.save(thing);
		assert400(() -> client.updateThing(auth("test"), finalThing.getThingId(), new ThingUpdateVO()));
	}

	@Test
	@Override
	public void updateThing401() throws Exception {
		assert401(() -> client.updateThing(null, UUID.randomUUID(), new ThingUpdateVO()));
	}

	@Test
	@Override
	public void updateThing404() throws Exception {
		assert404("Not Found",
				() -> client.updateThing(auth("test"), UUID.randomUUID(), new ThingUpdateVO().name("test")));
	}
}
