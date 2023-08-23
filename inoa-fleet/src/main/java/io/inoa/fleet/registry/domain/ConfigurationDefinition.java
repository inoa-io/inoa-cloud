package io.inoa.fleet.registry.domain;

import io.inoa.fleet.model.ConfigurationTypeVO;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import lombok.Data;

/*
* A definition for a configuration.
*
* @author Stephan Schnabel
*/
@MappedEntity
@Data
public class ConfigurationDefinition {

	@Id
	@GeneratedValue
	private Long id;

	@Relation(Kind.MANY_TO_ONE)
	private Tenant tenant;
	@MappedProperty
	private String key;
	@MappedProperty
	private ConfigurationTypeVO type;
	@MappedProperty
	private String description;

	@MappedProperty
	private Integer maximum;
	@MappedProperty
	private Integer minimum;
	@MappedProperty
	private String pattern;
}
