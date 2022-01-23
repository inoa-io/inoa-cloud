package io.inoa.fleet.thing;

import java.util.Optional;
import java.util.UUID;

import io.inoa.fleet.thing.domain.Property;
import io.inoa.fleet.thing.domain.PropertyDefinition;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeChannel;
import io.inoa.fleet.thing.domain.ThingTypeChannelRepository;
import io.inoa.fleet.thing.domain.ThingTypeRepository;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
@RequiredArgsConstructor
@Requires(notEnv = Environment.TEST) // Don't load data in tests.
public class ThingTypeCreator implements ApplicationEventListener<ServerStartupEvent> {

	private final ThingTypeRepository thingTypeRepository;
	private final ThingTypeChannelRepository thingTypeChannelRepository;
	public static final UUID THING_TYPE_DVH4013 = UUID.fromString("20065d62-4c6c-11ec-b3cc-db5c5d901ced");
	private static final UUID THING_TYPE_DVH4013_POWER_IN = UUID.fromString("a0e78d7c-4c74-11ec-aa9c-b716c08457d2");
	private static final UUID THING_TYPE_DVH4013_POWER_OUT = UUID.fromString("a9916d0a-4c90-11ec-91f3-cf7e7862e060");
	private static final UUID THING_TYPE_DVH4013_WORK_IN = UUID.fromString("aee1438e-4c90-11ec-9028-cf609b165950");
	private static final UUID THING_TYPE_DVH4013_WORK_OUT = UUID.fromString("b36b33c4-4c90-11ec-833e-77f433b9e964");

	@Override
	public void onApplicationEvent(ServerStartupEvent event) {
		Optional<ThingType> optionalThingType = thingTypeRepository.findByThingTypeId(THING_TYPE_DVH4013);
		ThingType dvh4013;
		if (optionalThingType.isEmpty()) {
			dvh4013 = new ThingType().setThingTypeId(THING_TYPE_DVH4013).setName("dvh4013");
			dvh4013.getProperties()
					.add(new PropertyDefinition().setName("serial").setKey("serial").setInputType("TEXT"));
			dvh4013.getProperties()
					.add(new PropertyDefinition().setName("slaveId").setKey("slaveId").setInputType("NUMBER"));
			dvh4013.getProperties()
					.add(new PropertyDefinition().setName("interface").setKey("interface").setInputType("NUMBER"));
			dvh4013 = thingTypeRepository.save(dvh4013);
		} else {
			dvh4013 = optionalThingType.get();
		}
		log.info("thing type: {}", dvh4013);
		Optional<ThingTypeChannel> optionalThingTypeChannel = thingTypeChannelRepository
				.findByThingTypeChannelId(THING_TYPE_DVH4013_POWER_IN);

		if (optionalThingTypeChannel.isEmpty()) {
			ThingTypeChannel powerChannel = new ThingTypeChannel().setKey("power_in").setName("Power In")
					.setDescription(null).setThingTypeChannelId(THING_TYPE_DVH4013_POWER_IN).setThingType(dvh4013);
			powerChannel.getProperties().add(new Property().setKey("name").setValue("power_in"));
			powerChannel.getProperties().add(new Property().setKey("urnPrefix").setValue("dvh4013"));
			powerChannel.getProperties().add(new Property().setKey("urnPostfix").setValue("0x0000"));
			powerChannel.getProperties().add(new Property().setKey("registerOffset").setValue(0));
			log.info("thing type power channel: {}", powerChannel);
			thingTypeChannelRepository.save(powerChannel);
		}

		optionalThingTypeChannel = thingTypeChannelRepository.findByThingTypeChannelId(THING_TYPE_DVH4013_POWER_OUT);

		if (optionalThingTypeChannel.isEmpty()) {
			ThingTypeChannel powerChannel = new ThingTypeChannel().setKey("power_out").setName("Power Out")
					.setDescription(null).setThingTypeChannelId(THING_TYPE_DVH4013_POWER_OUT).setThingType(dvh4013);
			powerChannel.getProperties().add(new Property().setKey("name").setValue("power_out"));
			powerChannel.getProperties().add(new Property().setKey("urnPrefix").setValue("dvh4013"));
			powerChannel.getProperties().add(new Property().setKey("urnPostfix").setValue("0x0002"));
			powerChannel.getProperties().add(new Property().setKey("registerOffset").setValue(2));
			log.info("thing type power channel: {}", powerChannel);
			thingTypeChannelRepository.save(powerChannel);
		}

		optionalThingTypeChannel = thingTypeChannelRepository.findByThingTypeChannelId(THING_TYPE_DVH4013_WORK_IN);

		if (optionalThingTypeChannel.isEmpty()) {
			ThingTypeChannel powerChannel = new ThingTypeChannel().setKey("work_in").setName("Work Out")
					.setDescription("1-0:1.8.0*255").setThingTypeChannelId(THING_TYPE_DVH4013_WORK_IN)
					.setThingType(dvh4013);
			powerChannel.getProperties().add(new Property().setKey("name").setValue("work_in"));
			powerChannel.getProperties().add(new Property().setKey("urnPrefix").setValue("dvh4013"));
			powerChannel.getProperties().add(new Property().setKey("urnPostfix").setValue("0x4001"));
			powerChannel.getProperties().add(new Property().setKey("registerOffset").setValue(2));
			log.info("thing type power channel: {}", powerChannel);
			thingTypeChannelRepository.save(powerChannel);
		}

		optionalThingTypeChannel = thingTypeChannelRepository.findByThingTypeChannelId(THING_TYPE_DVH4013_WORK_OUT);

		if (optionalThingTypeChannel.isEmpty()) {
			ThingTypeChannel powerChannel = new ThingTypeChannel().setKey("work_out").setName("Work Out")
					.setDescription("1-0:2.8.0*255").setThingTypeChannelId(THING_TYPE_DVH4013_WORK_OUT)
					.setThingType(dvh4013);
			powerChannel.getProperties().add(new Property().setKey("name").setValue("work_out"));
			powerChannel.getProperties().add(new Property().setKey("urnPrefix").setValue("dvh4013"));
			powerChannel.getProperties().add(new Property().setKey("urnPostfix").setValue("0x4101"));
			powerChannel.getProperties().add(new Property().setKey("registerOffset").setValue(16641));
			log.info("thing type power channel: {}", powerChannel);
			thingTypeChannelRepository.save(powerChannel);
		}

	}
}
