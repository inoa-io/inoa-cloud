package io.inoa.fleet.registry.domain;

import io.inoa.rest.ConfigurationTypeVO;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

/*
 * A definition for a configuration.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class ConfigurationDefinition {

  @Id @GeneratedValue private Long id;

  @MappedProperty private String key;
  @MappedProperty private ConfigurationTypeVO type;
  @MappedProperty private String description;

  @MappedProperty private Integer maximum;
  @MappedProperty private Integer minimum;
  @MappedProperty private String pattern;
}
