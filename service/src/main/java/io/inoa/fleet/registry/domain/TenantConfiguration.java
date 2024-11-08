package io.inoa.fleet.registry.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import lombok.Data;

/**
 * Configuration value for {@link ConfigurationDefinition} in scope of {@link Tenant}.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class TenantConfiguration implements Configuration {

  @Id @GeneratedValue private Long id;

  @Relation(Kind.MANY_TO_ONE)
  private ConfigurationDefinition definition;

  @Relation(Kind.MANY_TO_ONE)
  private Tenant tenant;

  @MappedProperty private String value;
}
