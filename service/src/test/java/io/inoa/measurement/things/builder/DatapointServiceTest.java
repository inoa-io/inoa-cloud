package io.inoa.measurement.things.builder;

import static io.inoa.measurement.things.domain.ObisId.OBIS_1_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_1_8_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_7_0;
import static io.inoa.measurement.things.domain.ObisId.OBIS_2_8_0;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.measurement.things.domain.MeasurandType;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingConfigurationType;
import io.inoa.measurement.things.domain.ThingType;

/**
 * Tests for {@see DatapointService}.
 *
 * @author fabian.schlegel@grayc.de
 */
public class DatapointServiceTest extends AbstractBuilderTest {

	DatapointService datapointService = new DatapointService(new ObjectMapper());

	@Test
	public void testBuildDefinition() throws DatapointBuilderException {
		var things = new ArrayList<Thing>();

		Thing thing1 = new Thing();
		thing1.setName("dvh4013");
		thing1.setThingType(new ThingType().setIdentifier("dvh4013"));

		addConfig(thing1, "serial", ThingConfigurationType.NUMBER, "100022");
		addConfig(thing1, "modbus interface", ThingConfigurationType.NUMBER, "1");

		addMeasurand(thing1, new MeasurandType().setObisId(OBIS_1_8_0.getObisId()));
		addMeasurand(thing1, new MeasurandType().setObisId(OBIS_2_8_0.getObisId()));
		addMeasurand(thing1, new MeasurandType().setObisId(OBIS_1_7_0.getObisId()));
		addMeasurand(thing1, new MeasurandType().setObisId(OBIS_2_7_0.getObisId()));

		Thing thing2 = new Thing();
		thing2.setName("shplg-s");
		thing2.setThingType(new ThingType().setIdentifier("shplg-s"));

		addConfig(thing2, "serial", ThingConfigurationType.STRING, "e465b85e2900");
		addConfig(thing2, "uri", ThingConfigurationType.STRING, "1");

		addMeasurand(thing2, new MeasurandType().setObisId(OBIS_1_8_0.getObisId()));

		things.add(thing1);
		things.add(thing2);

		var result = datapointService.getDatapointsJson(things);

		assertEquals(
				"""
						[{"interface":1,"id":"urn:dvh4013:100022:0x4101","name":"dvh4013","enabled":false,"interval":60000,"type":"RS485","timeout":1000,"frame":"2303410100028775"},{"interface":1,"id":"urn:dvh4013:100022:0x0000","name":"dvh4013","enabled":false,"interval":60000,"type":"RS485","timeout":1000,"frame":"230300000002C289"},{"interface":1,"id":"urn:dvh4013:100022:0x4001","name":"dvh4013","enabled":false,"interval":60000,"type":"RS485","timeout":1000,"frame":"2303400100028689"},{"interface":1,"id":"urn:dvh4013:100022:0x0002","name":"dvh4013","enabled":false,"interval":60000,"type":"RS485","timeout":1000,"frame":"2303000200026349"},{"id":"urn:shplg-s:e465b85e2900:status","name":"shplg-s","type":"HTTP_GET","interval":30000,"enabled":true,"uri":"1"}]""",
				result,
				"Expected correct datapoint JSON");
	}
}
