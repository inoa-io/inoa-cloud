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
@EqualsAndHashCode(exclude = "thing")
@ToString(exclude = "thing")
public class ThingConfigurationValue {
	@Id
	@GeneratedValue
	private Long id;

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Thing thing;

	private ThingConfiguration thingConfiguration;
	private String value;
}
