package io.inoa.measurement.things.domain;

import static io.inoa.measurement.things.domain.ThingConfigurationType.BOOLEAN;
import static io.inoa.measurement.things.domain.ThingConfigurationType.NUMBER;
import static io.inoa.measurement.things.domain.ThingConfigurationType.STRING;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

@MappedEntity
@Data
public class Thing {

  @Id @GeneratedValue private Long id;
  private UUID thingId;
  private Tenant tenant;
  private String urn;
  private String name;
  private String description;
  private Gateway gateway;
  private ThingType thingType;

  @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing")
  private Set<ThingConfigurationValue> thingConfigurationValues;

  @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing")
  private Set<Measurand> measurands;

  /**
   * Returns the thing configuration as key/object map.
   *
   * @return A key/object map.
   * @throws NumberFormatException thrown if key declared as number cannot be parsed.
   */
  public Map<String, Object> getConfig() throws NumberFormatException {
    var config = new HashMap<String, Object>();
    for (ThingConfigurationValue thingConfigurationValue : thingConfigurationValues) {
      var key = thingConfigurationValue.getThingConfiguration().getName();
      var value = thingConfigurationValue.getValue();
      switch (thingConfigurationValue.getThingConfiguration().getType()) {
        case NUMBER:
          config.put(key, Double.parseDouble(value));
          break;
        case BOOLEAN:
          config.put(key, Boolean.parseBoolean(value));
          break;
        case STRING:
        default:
          config.put(key, value);
      }
    }
    return Collections.unmodifiableMap(config);
  }

  public void addConfig(String name, ThingConfigurationType type, String value) {
    if (thingConfigurationValues == null) {
      thingConfigurationValues = new HashSet<>();
    }
    thingConfigurationValues.add(
        new ThingConfigurationValue()
            .setThingConfiguration(new ThingConfiguration().setName(name).setType(type))
            .setValue(value));
  }

  public void addMeasurand(MeasurandType type) {
    if (measurands == null) {
      measurands = new HashSet<>();
    }
    measurands.add(new Measurand().setMeasurandType(type));
  }
}
