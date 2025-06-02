package io.inoa.measurement.things.domain;

import java.util.Set;
import java.util.UUID;

import io.inoa.fleet.registry.domain.Gateway;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@MappedEntity
@ToString
@EqualsAndHashCode
public class Thing {
	@Id
	@GeneratedValue
	private Long id;
	private UUID thingId;
	private String name;
	@Nullable
	private String description;
	private Gateway gateway;
	private ThingType thingType;

	@Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing", cascade = Relation.Cascade.ALL)
	private Set<ThingConfigurationValue> thingConfigurationValues;

	@Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thing", cascade = Relation.Cascade.ALL)
	private Set<Measurand> measurands;
}
