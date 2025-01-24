package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

@MappedEntity
@Data
public class ThingConfigurationValue {
  @Relation(Relation.Kind.MANY_TO_ONE)
  private Thing thing;

  private ThingConfiguration thingConfiguration;
  private String value;
}
