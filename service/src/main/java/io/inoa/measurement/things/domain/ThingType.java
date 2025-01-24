package io.inoa.measurement.things.domain;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.sql.JoinColumn;
import io.micronaut.data.annotation.sql.JoinTable;
import java.util.List;

@MappedEntity
public record ThingType(
    @Id @GeneratedValue Long id,
    String identifier,
    String name,
    String description,
    @Nullable String version,
    @Relation(value = Relation.Kind.ONE_TO_MANY, cascade = Relation.Cascade.ALL)
        @JoinTable(
            name = "thing_type_measurand_type",
            inverseJoinColumns =
                @JoinColumn(name = "measurand_type_id", referencedColumnName = "id"),
            joinColumns = @JoinColumn(name = "thing_type_id", referencedColumnName = "id"))
        List<MeasurandType> measurandTypes,
    @Relation(
            value = Relation.Kind.ONE_TO_MANY,
            mappedBy = "thingType",
            cascade = Relation.Cascade.ALL)
        List<ThingConfiguration> thingConfigurations,
    MeasurandProtocol protocol) {}
