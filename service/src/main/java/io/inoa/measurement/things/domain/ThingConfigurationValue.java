package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;

@MappedEntity
public record ThingConfigurationValue(
    @Relation(Relation.Kind.MANY_TO_ONE) Thing thing,
    ThingConfiguration thingConfiguration,
    String value) {}
