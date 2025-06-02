package io.inoa.measurement.things.domain;

import java.util.List;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.sql.JoinColumn;
import io.micronaut.data.annotation.sql.JoinTable;
import lombok.Data;

@MappedEntity
@Data
public class ThingType {
	private @Nullable @Id @GeneratedValue Long id;
	private String identifier;
	private String name;
	private String description;
	@Nullable
	private String version;
	@Nullable
	private ThingTypeCategory category;

	@Relation(value = Relation.Kind.ONE_TO_MANY, cascade = Relation.Cascade.ALL)
	@JoinTable(name = "thing_type_measurand_type", inverseJoinColumns = @JoinColumn(name = "measurand_type_id", referencedColumnName = "id"), joinColumns = @JoinColumn(name = "thing_type_id", referencedColumnName = "id"))
	private List<MeasurandType> measurandTypes;

	@Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "thingType", cascade = Relation.Cascade.ALL)
	private List<ThingConfiguration> thingConfigurations;

	private MeasurandProtocol protocol;
}
