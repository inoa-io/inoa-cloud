package io.inoa.measurement;

import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingRepository;
import io.inoa.measurement.things.domain.ThingType;
import io.inoa.measurement.things.domain.ThingTypeRepository;
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
		thingType.setThingTypeId(thingTypeReference);
		thingType.setThingTypeId("");
		return thingTypeRepository.save(thingType);
	}

	public Thing createThing(String gatewayId, String tenantId, ThingType thingType, Map<String, Object> config) {
		Thing thing = getThing(gatewayId, tenantId, thingType);
		thing.setConfig(config);
		return thingRepository.save(thing);
	}
}
