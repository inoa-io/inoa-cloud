package io.inoa.measurement.things.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import io.inoa.measurement.things.domain.Measurand;
import io.inoa.measurement.things.domain.MeasurandType;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingConfiguration;
import io.inoa.measurement.things.domain.ThingConfigurationType;
import io.inoa.measurement.things.domain.ThingConfigurationValue;

public abstract class AbstractBuilderTest {

	/**
	 * Returns the thing configuration as key/value map.
	 *
	 * @return A key/value map.
	 */
	protected Map<String, String> getConfig(Thing thing) throws NumberFormatException {
		var config = new HashMap<String, String>();
		for (ThingConfigurationValue thingConfigurationValue : thing.getThingConfigurationValues()) {
			config.put(
					thingConfigurationValue.getThingConfiguration().getName(),
					thingConfigurationValue.getValue());
		}
		return Collections.unmodifiableMap(config);
	}

	protected void addConfig(Thing thing, String name, ThingConfigurationType type, String value) {
		if (thing.getThingConfigurationValues() == null) {
			thing.setThingConfigurationValues(new HashSet<>());
		}

		thing
				.getThingConfigurationValues()
				.add(
						new ThingConfigurationValue()
								.setThingConfiguration(new ThingConfiguration().setName(name).setType(type))
								.setValue(value));
	}

	protected void addMeasurand(Thing thing, MeasurandType type) {
		if (thing.getMeasurands() == null) {
			thing.setMeasurands(new HashSet<>());
		}
		thing.getMeasurands().add(new Measurand().setMeasurandType(type));
	}
}
