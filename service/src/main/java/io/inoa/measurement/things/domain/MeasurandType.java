package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import lombok.Data;

@MappedEntity
@Data
public class MeasurandType {
  @Id @GeneratedValue private Long id;
  private String obisId;
  private String name;
  private String description;
}
