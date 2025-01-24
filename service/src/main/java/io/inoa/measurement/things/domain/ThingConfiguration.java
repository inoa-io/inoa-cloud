package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;

@MappedEntity
public record ThingConfiguration(
    @Id @GeneratedValue Long id,
    String name,
    String description,
    @Relation(value = Relation.Kind.MANY_TO_ONE) ThingType thingType,
    ThingConfigurationType type,
    String validationRegex) {}
