package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import java.time.Instant;
import java.util.Map;
import lombok.Data;

@MappedEntity
@Data
public class ThingType {
  @Id @GeneratedValue private Long id;
  @MappedProperty private String thingTypeId;
  @MappedProperty private String name;
  @MappedProperty private String category;

  @TypeDef(type = DataType.JSON)
  private Map<String, Object> jsonSchema;

  @DateCreated private Instant created;
  @DateUpdated private Instant updated;
}
