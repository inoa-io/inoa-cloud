package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

@MappedEntity
@Data
public class ThingConfiguration {
  @Id @GeneratedValue private Long id;
  private String name;
  private String description;

  @Relation(value = Relation.Kind.MANY_TO_ONE)
  private ThingType thingType;

  private ThingConfigurationType type;
  private String validationRegex;
}
