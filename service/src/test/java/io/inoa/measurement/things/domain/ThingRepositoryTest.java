package io.inoa.measurement.things.domain;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.GatewayStatus;
import io.inoa.fleet.registry.domain.GatewayStatusMqtt;
import io.inoa.fleet.registry.domain.Tenant;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.test.AbstractUnitTest;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThingRepositoryTest extends AbstractUnitTest {

	@Inject ThingRepository thingRepository;
	@Inject TenantRepository tenantRepository;
	@Inject	ThingTypeRepository thingTypeRepository;
	@Inject GatewayRepository gatewayRepository;
	@Inject MeasurandTypeRepository measurandTypeRepository;

	@Test
	public void testN() {
		var thing = new Thing();
		var tenant = tenantRepository.save(new Tenant().setTenantId("inoa").setEnabled(true).setName("INOA").setGatewayIdPattern("egal"));
		var thingType = thingTypeRepository.findByIdentifier("dvh4013").get(0);
		var gateway = gatewayRepository.save(new Gateway().setTenant(tenant).setGatewayId("ISRL02-1234567890").setEnabled(true).setStatus(new GatewayStatus().setMqtt(new GatewayStatusMqtt().setConnected(false))));
		var measurandType = measurandTypeRepository.findByObisId(ObisId.OBIS_1_8_0.getObisId()).get();

		thing.setThingId(UUID.randomUUID());
		thing.setName("test");
		thing.setDescription("description");
		thing.setThingType(thingType);
		thing.setTenant(tenant);
		thing.setGateway(gateway);
		thing.setMeasurands(Set.of(new Measurand().setThing(thing).setMeasurandType(measurandType).setEnabled(true).setInterval(3000L).setTimeout(1000L)));
		var thingCreated = thingRepository.save(thing);

		var thingFound = thingRepository.findByThingId(thingCreated.getThingId());
		assertTrue(thingFound.isPresent());
		assertEquals("inoa", thingFound.get().getTenant().getTenantId());
		assertNotNull(thingFound.get().getMeasurands());
		assertEquals(1, thingFound.get().getMeasurands().size());

		assertNotNull(thingFound.get().getGateway());
		assertEquals("ISRL02-1234567890", thingFound.get().getGateway().getGatewayId());

		assertNotNull(thingFound.get().getThingType());

	}
}
