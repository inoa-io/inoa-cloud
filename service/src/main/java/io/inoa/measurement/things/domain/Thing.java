package io.inoa.measurement.things.domain;

import io.inoa.fleet.registry.domain.Gateway;
import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

@MappedEntity
@Data
public class Thing {

  @Id @GeneratedValue private Long id;
  private UUID thingId;
  private Tenant tenant;
  private String name;
  @Nullable private String description;
  private Gateway gateway;
  private ThingType thingType;

  @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing")
  private Set<ThingConfigurationValue> thingConfigurationValues;

  @Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing")
  private Set<Measurand> measurands;
}
