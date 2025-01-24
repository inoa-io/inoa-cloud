package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

@MappedEntity
@Data
public class Measurand {
  @Id @GeneratedValue private Long id;

  @Relation(Relation.Kind.MANY_TO_ONE)
  private Thing thing;

  private MeasurandType measurandType;
  private Boolean enabled;
  private Long interval;
  private Long timeout;
}
