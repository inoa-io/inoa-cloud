package io.inoa.fleet.thing;

import javax.transaction.Transactional;

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
	public Thing createThing(String gatewayId, String tenantId, ThingType thingType) {
		Thing thing = new Thing();
		thing.setThingId("");
		thing.setTenantId(tenantId);
		thing.setName("");
		thing.setThingType(thingType);
		thing.setGatewayId(gatewayId);
		return thingRepository.save(thing);
	}
}
