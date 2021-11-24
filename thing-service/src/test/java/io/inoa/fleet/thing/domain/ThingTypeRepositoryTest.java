package io.inoa.fleet.thing.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.inoa.fleet.thing.AbstractTest;
import jakarta.inject.Inject;

public class ThingTypeRepositoryTest extends AbstractTest {

	@Inject
	ThingTypeRepository thingTypeRepository;
	@Inject
	ThingTypeChannelRepository thingTypeChannelRepository;

	@Test
	public void createThingType() {
		var thingType = new ThingType().setThingTypeId(UUID.randomUUID()).setName("test")
				.setProperties(new ArrayList<>());
		thingTypeRepository.save(thingType);

		List<PropertyDefinition> properties = List.of(new PropertyDefinition().setName("test"));
		thingType = new ThingType().setThingTypeId(UUID.randomUUID()).setName("test").setProperties(properties);
		thingType = thingTypeRepository.save(thingType);

		Optional<ThingType> optionalThingType = thingTypeRepository.findById(thingType.getId());
		assertTrue(optionalThingType.isPresent());
		assertEquals(1, optionalThingType.get().getProperties().size());
		assertEquals("test", optionalThingType.get().getProperties().get(0).getName());
	}

	@Test
	public void createThingTypeDvh4013() {
		var thingType = new ThingType().setThingTypeId(UUID.randomUUID()).setName("DVH4013");
		thingType.getProperties().add(new PropertyDefinition().setName("Serial Number").setKey("serial_key"));
		thingType = thingTypeRepository.save(thingType);

		ThingTypeChannel thingTypeChannelPower = new ThingTypeChannel().setThingType(thingType)
				.setThingTypeChannelId(UUID.randomUUID()).setName("Power");
		thingTypeChannelRepository.save(thingTypeChannelPower);

		ThingTypeChannel thingTypeChannelFrequency = new ThingTypeChannel().setThingType(thingType)
				.setThingTypeChannelId(UUID.randomUUID()).setName("Frequency");
		thingTypeChannelRepository.save(thingTypeChannelFrequency);
	}
}
