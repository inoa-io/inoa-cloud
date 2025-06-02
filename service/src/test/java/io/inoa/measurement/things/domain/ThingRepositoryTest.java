package io.inoa.measurement.things.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.GatewayRepository;
import io.inoa.fleet.registry.domain.GatewayStatus;
import io.inoa.fleet.registry.domain.GatewayStatusMqtt;
import io.inoa.fleet.registry.domain.TenantRepository;
import io.inoa.test.AbstractUnitTest;

/**
 * Specific tests for (quite complex) {@link ThingRepository}.
 *
 * @author fabian.schlegel@grayc.de
 */
public class ThingRepositoryTest extends AbstractUnitTest {

	@Inject
	ThingRepository thingRepository;
	@Inject
	TenantRepository tenantRepository;
	@Inject
	ThingTypeRepository thingTypeRepository;
	@Inject
	GatewayRepository gatewayRepository;
	@Inject
	MeasurandTypeRepository measurandTypeRepository;

	@Test
	public void findByThingId() {
		var thing = new Thing();
		var tenant = data.tenant();
		var thingType = thingTypeRepository.findByIdentifier("dvh4013").get(0);
		var measurandType = measurandTypeRepository.findByObisId(ObisId.OBIS_1_8_0.getObisId()).orElseThrow();
		var thingConfiguration = thingType.getThingConfigurations().get(0);

		// Create Gateway
		var gateway = gatewayRepository.save(
				new Gateway()
						.setTenant(tenant)
						.setGatewayId("ISRL02-1234567890")
						.setEnabled(true)
						.setStatus(
								new GatewayStatus().setMqtt(new GatewayStatusMqtt().setConnected(false))));

		// Create thing with all possible fields
		thing.setThingId(UUID.randomUUID());
		thing.setName("test");
		thing.setDescription("description");
		thing.setThingType(thingType);
		thing.setGateway(gateway);
		thing.setMeasurands(
				Set.of(
						new Measurand()
								.setThing(thing)
								.setMeasurandType(measurandType)
								.setEnabled(true)
								.setInterval(3000L)
								.setTimeout(1000L)));
		thing.setThingConfigurationValues(
				Set.of(
						new ThingConfigurationValue()
								.setThing(thing)
								.setValue("cool")
								.setThingConfiguration(thingType.getThingConfigurations().get(0))));

		// Save thing
		var thingCreated = thingRepository.save(thing);

		// Find by UUID
		var thingFound = thingRepository.findByThingId(thingCreated.getThingId());

		// Check Equality
		assertTrue(thingFound.isPresent(), "Expected query result");

		// Base
		assertEquals("test", thingFound.get().getName(), "Expected equal name");
		assertEquals("description", thingFound.get().getDescription(), "Expected equal description");

		// ThingType loaded?
		assertEquals(
				"dvh4013",
				thingFound.get().getThingType().getIdentifier(),
				"Expected equal thingType identifier");

		// Gateway loaded?
		assertEquals(
				"ISRL02-1234567890",
				thingFound.get().getGateway().getGatewayId(),
				"Expected equal gateway id");

		// Tenant loaded?
		assertEquals(
				tenant.getTenantId(), thingFound.get().getGateway().getTenant().getTenantId(),
				"Expected equal tenantId");

		// Measurands loaded?
		assertEquals(1, thingFound.get().getMeasurands().size(), "Expected measurands loaded");
		assertEquals(
				3000L,
				thingFound.get().getMeasurands().iterator().next().getInterval(),
				"Expected equal measurand interval");
		assertEquals(
				ObisId.OBIS_1_8_0.getObisId(),
				thingFound.get().getMeasurands().iterator().next().getMeasurandType().getObisId(),
				"Expected equal Obis");

		// Configs loaded?
		assertEquals(
				1, thingFound.get().getThingConfigurationValues().size(), "Expected configs loaded");
		assertEquals(
				"cool",
				thingFound.get().getThingConfigurationValues().iterator().next().getValue(),
				"Expected equal config value");
		assertEquals(
				"Serial",
				thingFound
						.get()
						.getThingConfigurationValues()
						.iterator()
						.next()
						.getThingConfiguration()
						.getName(),
				"Expected equal config name");
	}
}
