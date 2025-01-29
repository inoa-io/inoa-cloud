package io.inoa.measurement.things.builder;

import static io.inoa.measurement.things.domain.ThingConfigurationType.BOOLEAN;
import static io.inoa.measurement.things.domain.ThingConfigurationType.NUMBER;
import static io.inoa.measurement.things.domain.ThingConfigurationType.STRING;

import com.fasterxml.jackson.databind.node.ArrayNode;
import io.inoa.measurement.things.domain.Thing;
import io.inoa.measurement.things.domain.ThingConfigurationValue;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class ConfigCreator {

  /**
   * Build the JSON structure for the datapoint definition for a specific thing
   *
   * @param thing the thing to export the JSON from
   * @return JSON structure
   */
  public abstract ArrayNode build(Thing thing) throws ConfigException;

  /**
   * Build the RPC JSON structure for the datapoint definition for a specific thing
   *
   * @param thing the thing to export the JSON from
   * @return JSON structure
   */
  public abstract ArrayNode buildRPC(Thing thing);

  protected String getConfigAsString(Thing thing, String configKey) throws ConfigException {
    var config = getConfig(thing, configKey);
    if (STRING != config.getThingConfiguration().getType()) {
      throw new ConfigException(thing.getThingId(), configKey, "Config type mismatch");
    }
    var value = config.getValue();
    if (config.getThingConfiguration().getValidationRegex() != null) {
      try {
        var pattern = Pattern.compile(config.getThingConfiguration().getValidationRegex());
        if (!pattern.matcher(value).matches()) {
          throw new ConfigException(
              thing.getThingId(), configKey, "Config validation regex mismatch");
        }
      } catch (PatternSyntaxException e) {
        throw new ConfigException(thing.getThingId(), configKey, "Regex invalid");
      }
    }
    return value;
  }

  protected Number getConfigAsNumber(Thing thing, String configKey) throws ConfigException {
    var config = getConfig(thing, configKey);
    if (NUMBER != config.getThingConfiguration().getType()) {
      throw new ConfigException(thing.getThingId(), configKey, "Config type mismatch");
    }
    try {
      return Double.parseDouble(config.getValue());
    } catch (NumberFormatException e) {
      throw new ConfigException(thing.getThingId(), configKey, "Not a number");
    }
  }

  protected Boolean getConfigAsBoolean(Thing thing, String configKey) throws ConfigException {
    var config = getConfig(thing, configKey);
    if (BOOLEAN != config.getThingConfiguration().getType()) {
      throw new ConfigException(thing.getThingId(), configKey, "Config type mismatch");
    }
    return Boolean.parseBoolean(config.getValue());
  }

  ThingConfigurationValue getConfig(Thing thing, String configKey) throws ConfigException {
    var config = getThingConfigurationValue(thing, configKey);
    if (config == null || config.getValue() == null) {
      throw new ConfigException(thing.getThingId(), configKey, "Is not set");
    }
    return config;
  }

  ThingConfigurationValue getThingConfigurationValue(Thing thing, String configKey) {
    for (var thingConfigurationValue : thing.getThingConfigurationValues()) {
      if (configKey.equals(thingConfigurationValue.getThingConfiguration().getName())) {
        return thingConfigurationValue;
      }
    }
    return null;
  }
}
