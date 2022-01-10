package io.inoa.fleet.thing.rest;

import static io.inoa.fleet.thing.rest.HttpResponseAssertions.assert204;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.inoa.fleet.thing.domain.PropertyDefinition;
import org.junit.jupiter.api.Test;

import io.inoa.fleet.thing.AbstractTest;
import io.inoa.fleet.thing.domain.Property;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeChannel;
import io.inoa.fleet.thing.domain.ThingTypeChannelRepository;
import io.inoa.fleet.thing.domain.ThingTypeRepository;
import io.inoa.fleet.thing.rest.management.ThingTypeChannelCreateVO;
import io.inoa.fleet.thing.rest.management.ThingTypeCreateVO;
import io.inoa.fleet.thing.rest.management.ThingTypeUpdateVO;
import io.inoa.fleet.thing.rest.management.ThingTypesApiTestClient;
import io.inoa.fleet.thing.rest.management.ThingTypesApiTestSpec;
import jakarta.inject.Inject;

public class ThingTypesApiTest extends AbstractTest implements ThingTypesApiTestSpec {

	@Inject
	ThingTypesApiTestClient client;
	@Inject
	ThingTypeRepository thingTypeRepository;
	@Inject
	ThingTypeChannelRepository thingTypeChannelRepository;

	@Test
	@Override
	public void createThingType201() throws Exception {
		var thingTypeCreateVO = new ThingTypeCreateVO().setName("test").setChannels(new ArrayList<>());
		thingTypeCreateVO.getChannels().add(new ThingTypeChannelCreateVO().key("test").name("test"));
		var created = assert201(() -> client.createThingType(auth(), thingTypeCreateVO));
		Optional<ThingType> thingType = thingTypeRepository.findByThingTypeId(created.id());
		assertTrue(thingType.isPresent());
		List<ThingTypeChannel> channels = thingTypeChannelRepository.findByThingType(thingType.get());
		assertNotNull(channels);
		assertEquals(1, channels.size());
	}

	@Test
	@Override
	public void createThingType400() throws Exception {
		var thingTypeCreateVO = new ThingTypeCreateVO().setChannels(new ArrayList<>());
		assert400(() -> client.createThingType(auth(), thingTypeCreateVO));
	}

	@Test
	@Override
	public void createThingType401() throws Exception {
		var thingTypeCreateVO = new ThingTypeCreateVO().setChannels(new ArrayList<>());
		assert401(() -> client.createThingType(null, thingTypeCreateVO));
	}

	@Test
	@Override
	public void deleteThingType204() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		ThingType finalThingType = thingTypeRepository.save(dvh4013);
		assert204(() -> client.deleteThingType(auth(), finalThingType.getThingTypeId()));
	}

	@Test
	@Override
	public void deleteThingType401() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		ThingType finalThingType = thingTypeRepository.save(dvh4013);
		assert401(() -> client.deleteThingType(null, finalThingType.getThingTypeId()));
	}

	@Test
	@Override
	public void deleteThingType404() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		thingTypeRepository.save(dvh4013);
		assert404("Not Found", () -> client.deleteThingType(auth(), UUID.randomUUID()));
	}

	@Test
	@Override
	public void findThingType200() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		ThingType finalThingType = thingTypeRepository.save(dvh4013);
		assert200(() -> client.findThingType(auth(), finalThingType.getThingTypeId()));
	}

	@Test
	@Override
	public void findThingType401() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		ThingType finalThingType = thingTypeRepository.save(dvh4013);
		assert401(() -> client.findThingType(null, finalThingType.getThingTypeId()));
	}

	@Test
	@Override
	public void findThingType404() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		thingTypeRepository.save(dvh4013);
		assert404("Not Found", () -> client.findThingType(auth(), UUID.randomUUID()));
	}

	@Test
	@Override
	public void findThingTypeWithDetails200() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		dvh4013.getProperties().add(new PropertyDefinition().setInputType("TEXT").setName("serialId").setKey("serialId"));
		dvh4013.getProperties().add(new PropertyDefinition().setInputType("NUMBER").setName("slaveId").setKey("serialId"));
		dvh4013 = thingTypeRepository.save(dvh4013);
		ThingTypeChannel powerChannel = new ThingTypeChannel().setThingTypeChannelId(UUID.randomUUID()).setKey("power")
				.setName("power").setThingType(dvh4013);
		powerChannel.getProperties().add(new Property().setKey("name").setValue("power"));
		powerChannel.getProperties().add(new Property().setKey("urnPrefix").setValue("dvh4013"));
		powerChannel.getProperties().add(new Property().setKey("urnPostfix").setValue("0x4001"));
		powerChannel.getProperties().add(new Property().setKey("registerOffset").setValue(16385));
		thingTypeChannelRepository.save(powerChannel);
		ThingType finalDvh401 = dvh4013;
		var result = assert200(() -> client.findThingTypeWithDetails(auth(), finalDvh401.getThingTypeId()));
		assertNotNull(result.getChannels());
		assertEquals(1, result.getChannels().size());
	}

	@Test
	@Override
	public void findThingTypeWithDetails401() throws Exception {
		assert401(() -> client.findThingTypeWithDetails(null, UUID.randomUUID()));
	}

	@Test
	@Override
	public void findThingTypeWithDetails404() throws Exception {
		assert404("Not Found", () -> client.findThingTypeWithDetails(auth(), UUID.randomUUID()));
	}

	@Test
	@Override
	public void findThingTypes200() throws Exception {
		var thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingTypeRepository.save(thingType);
		var list = assert200(() -> client.findThingTypes(auth(), null, null, null, null));
		assertEquals(1, list.getContent().size());
	}

	@Test
	@Override
	public void findThingTypes401() throws Exception {
		var thingType = new ThingType().setName("test").setThingTypeId(UUID.randomUUID());
		thingTypeRepository.save(thingType);
		assert401(() -> client.findThingTypes(null, null, null, null));
	}

	@Test
	@Override
	public void updateThingType200() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		ThingType finalThingType = thingTypeRepository.save(dvh4013);
		var thingTypeUpdateVO = new ThingTypeUpdateVO().setName("test").setChannels(new ArrayList<>());
		var updated = assert200(
				() -> client.updateThingType(auth(), finalThingType.getThingTypeId(), thingTypeUpdateVO));
		Optional<ThingType> byThingTypeId = thingTypeRepository.findByThingTypeId(finalThingType.getThingTypeId());
		assertEquals("test", byThingTypeId.get().getName());
	}

	@Test
	@Override
	public void updateThingType400() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		ThingType finalThingType = thingTypeRepository.save(dvh4013);
		var thingTypeUpdateVO = new ThingTypeUpdateVO().setChannels(new ArrayList<>());
		assert400(() -> client.updateThingType(auth(), finalThingType.getThingTypeId(), thingTypeUpdateVO));
	}

	@Test
	@Override
	public void updateThingType401() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		ThingType finalThingType = thingTypeRepository.save(dvh4013);
		var thingTypeUpdateVO = new ThingTypeUpdateVO().setChannels(new ArrayList<>());
		assert401(() -> client.updateThingType(null, finalThingType.getThingTypeId(), thingTypeUpdateVO));
	}

	@Test
	@Override
	public void updateThingType404() throws Exception {
		var dvh4013 = new ThingType().setThingTypeId(UUID.randomUUID()).setName("dvh4013");
		ThingType finalThingType = thingTypeRepository.save(dvh4013);
		var thingTypeUpdateVO = new ThingTypeUpdateVO().setName("test").setChannels(new ArrayList<>());
		assert404("Not Found", () -> client.updateThingType(auth(), UUID.randomUUID(), thingTypeUpdateVO));
	}
}
