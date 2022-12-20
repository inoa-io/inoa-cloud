package io.inoa.fleet.thing;

import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.jetbrains.annotations.NotNull;

import io.inoa.fleet.thing.domain.Thing;
import io.inoa.fleet.thing.domain.ThingRepository;
import io.inoa.fleet.thing.domain.ThingType;
import io.inoa.fleet.thing.domain.ThingTypeRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

@Singleton
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RequiredArgsConstructor
public class Data {
	@Inject
	ThingTypeRepository thingTypeRepository;

	@Inject
	ThingRepository thingRepository;

	@NotNull
	private static Thing getThing(String gatewayId, String tenantId, ThingType thingType) {
		Thing thing = new Thing();
		thing.setThingId(UUID.randomUUID().toString());
		thing.setTenantId(tenantId);
		thing.setName("my_thing");
		thing.setThingType(thingType);
		thing.setGatewayId(gatewayId);
		return thing;
	}

	public void deleteAll() {
		// first delete all things as we have an FK on thing type but no cascade delete
		thingRepository.deleteAll();
		thingTypeRepository.deleteAll();
	}

	public ThingType createThingType(String name, String thingTypeReference) {
		ThingType thingType = new ThingType();
		thingType.setName(name);
		thingType.setThingTypeReference(thingTypeReference);
		thingType.setThingTypeId("");
		return thingTypeRepository.save(thingType);
	}

	public Thing createThing(String gatewayId, String tenantId, ThingType thingType, Map<String, Object> config) {
		Thing thing = getThing(gatewayId, tenantId, thingType);
		thing.setConfig(config);
		return thingRepository.save(thing);
	}

	public Thing createThing(String gatewayId, String tenantId, ThingType thingType) {
		Thing thing = getThing(gatewayId, tenantId, thingType);
		return thingRepository.save(thing);
	}
}
