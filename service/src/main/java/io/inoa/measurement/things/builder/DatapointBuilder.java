package io.inoa.measurement.things.builder;

import static io.inoa.measurement.things.domain.ThingConfigurationType.BOOLEAN;
import static io.inoa.measurement.things.domain.ThingConfigurationType.NUMBER;
import static io.inoa.measurement.things.domain.ThingConfigurationType.STRING;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingConfigurationValue;

/**
 * Base class for all datapoint builders. Their duty is to build the datapoint JSON structure from
 * the thing definition. This JSON is readable by the OS on the IoT devices. The difference between
 * the thing and the datapoint is that a datapoint is completely agnostic with respect to the
 * specific thing itself - it only holds information what to measure and how.
 *
 * @author fabian.schlegel@grayc.de
 */
public abstract class DatapointBuilder {

	/**
	 * Build the JSON structure for the datapoint definition for a specific thing
	 *
	 * @param thing the thing to export the JSON from
	 * @return JSON structure
	 */
	public abstract ArrayNode build(Thing thing) throws DatapointBuilderException;

	/**
	 * Build the RPC JSON structure for the datapoint definition for a specific thing
	 *
	 * @param thing the thing to export the JSON from
	 * @return JSON structure
	 */
	public abstract ArrayNode buildRPC(Thing thing);

	/**
	 * Returns a specific thing configuration as string
	 *
	 * @param thing     the specific thing
	 * @param configKey the specific configuration key
	 * @return the configuration value as string
	 * @throws DatapointBuilderException thrown if config does not exist, is invalid or has wrong type
	 */
	protected String getConfigAsString(Thing thing, String configKey)
			throws DatapointBuilderException {
		var config = getConfig(thing, configKey);
		if (STRING != config.getThingConfiguration().getType()) {
			throw new DatapointBuilderException(thing.getThingId(), configKey, "Config type mismatch");
		}
		var value = config.getValue();
		if (config.getThingConfiguration().getValidationRegex() != null) {
			try {
				var pattern = Pattern.compile(config.getThingConfiguration().getValidationRegex());
				if (!pattern.matcher(value).matches()) {
					throw new DatapointBuilderException(
							thing.getThingId(), configKey, "Config validation regex mismatch");
				}
			} catch (PatternSyntaxException e) {
				throw new DatapointBuilderException(thing.getThingId(), configKey, "Regex invalid");
			}
		}
		return value;
	}

	/**
	 * Returns a specific thing configuration as number
	 *
	 * @param thing     the specific thing
	 * @param configKey the specific configuration key
	 * @return the configuration value as number
	 * @throws DatapointBuilderException thrown if config does not exist, is invalid or has wrong type
	 */
	protected Number getConfigAsNumber(Thing thing, String configKey)
			throws DatapointBuilderException {
		var config = getConfig(thing, configKey);
		if (NUMBER != config.getThingConfiguration().getType()) {
			throw new DatapointBuilderException(thing.getThingId(), configKey, "Config type mismatch");
		}
		try {
			return Double.parseDouble(config.getValue());
		} catch (NumberFormatException e) {
			throw new DatapointBuilderException(thing.getThingId(), configKey, "Not a number");
		}
	}

	/**
	 * Returns a specific thing configuration as boolean
	 *
	 * @param thing     the specific thing
	 * @param configKey the specific configuration key
	 * @return the configuration value as boolean
	 * @throws DatapointBuilderException thrown if config does not exist, is invalid or has wrong type
	 */
	protected Boolean getConfigAsBoolean(Thing thing, String configKey)
			throws DatapointBuilderException {
		var config = getConfig(thing, configKey);
		if (BOOLEAN != config.getThingConfiguration().getType()) {
			throw new DatapointBuilderException(thing.getThingId(), configKey, "Config type mismatch");
		}
		return Boolean.parseBoolean(config.getValue());
	}

	ThingConfigurationValue getConfig(Thing thing, String configKey)
			throws DatapointBuilderException {
		var config = getThingConfigurationValue(thing, configKey);
		if (config == null || config.getValue() == null) {
			throw new DatapointBuilderException(thing.getThingId(), configKey, "Is not set");
		}
		return config;
	}

	ThingConfigurationValue getThingConfigurationValue(Thing thing, String configKey) {
		for (var thingConfigurationValue : thing.getThingConfigurationValues()) {
			if (configKey.equalsIgnoreCase(thingConfigurationValue.getThingConfiguration().getName())) {
				return thingConfigurationValue;
			}
		}
		return null;
	}
}
