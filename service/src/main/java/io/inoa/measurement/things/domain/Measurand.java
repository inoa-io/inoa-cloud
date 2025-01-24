package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;

@MappedEntity
public record Measurand(
    @Id @GeneratedValue Long id,
    @Relation(Relation.Kind.MANY_TO_ONE) Thing thing,
    MeasurandType measurandType,
    Boolean enabled,
    Long interval,
    Long timeout) {}
