package io.inoa.fleet.registry.domain;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.Data;

/**
 * A property of a gateway.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class GatewayProperty {

  @Relation(Relation.Kind.MANY_TO_ONE)
  private Gateway gateway;

  @MappedProperty("key")
  private String key;

  @MappedProperty private String value;

  @DateCreated(truncatedTo = ChronoUnit.MILLIS)
  private Instant created;

  @DateUpdated(truncatedTo = ChronoUnit.MILLIS)
  private Instant updated;
}
