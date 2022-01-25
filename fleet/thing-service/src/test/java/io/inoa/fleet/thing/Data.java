package io.inoa.fleet.thing;

import javax.transaction.Transactional;

import io.inoa.fleet.thing.domain.ThingRepository;
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
}
