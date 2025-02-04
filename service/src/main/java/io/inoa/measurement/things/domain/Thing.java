package io.inoa.measurement.things.domain;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.core.annotation.Nullable;
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
  @Nullable private String description;
  private Gateway gateway;
  private ThingType thingType;

  @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing")
  private Set<ThingConfigurationValue> thingConfigurationValues;

  @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing")
  private Set<Measurand> measurands;

  /**
   * Returns the thing configuration as key/value map.
   *
   * @return A key/value map.
   */
  public Map<String, String> getConfig() throws NumberFormatException {
    var config = new HashMap<String, String>();
    for (ThingConfigurationValue thingConfigurationValue : thingConfigurationValues) {
      config.put(
          thingConfigurationValue.getThingConfiguration().getName(),
          thingConfigurationValue.getValue());
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
