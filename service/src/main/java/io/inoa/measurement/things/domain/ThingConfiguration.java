package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@MappedEntity
@Data
@EqualsAndHashCode(exclude = "thingType")
@ToString(exclude = "thingType")
public class ThingConfiguration {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String description;

	@Relation(value = Relation.Kind.MANY_TO_ONE, cascade = Relation.Cascade.ALL)
	private ThingType thingType;

	private ThingConfigurationType type;
	private String validationRegex;
}
