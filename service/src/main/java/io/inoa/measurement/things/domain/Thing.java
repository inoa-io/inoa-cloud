package io.inoa.measurement.things.domain;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@MappedEntity
public record Thing(
    @Id @GeneratedValue Long id,
    UUID thingId,
    Tenant tenant,
    String urn,
    String name,
    String description,
    Gateway gateway,
    ThingType thingType,
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing")
        Set<ThingConfigurationValue> thingConfigurationValues,
    @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing") Set<Measurand> measurands) {

  /**
   * Returns the thing configuration as key/object map.
   *
   * @return A key/object map.
   * @throws NumberFormatException thrown if key declared as number cannot be parsed.
   */
  public Map<String, Object> getConfig() throws NumberFormatException {
    var config = new HashMap<String, Object>();
    for (ThingConfigurationValue thingConfigurationValue : thingConfigurationValues) {
      var key = thingConfigurationValue.thingConfiguration().name();
      var value = thingConfigurationValue.value();
      switch (thingConfigurationValue.thingConfiguration().type()) {
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
    return config;
  }
}
