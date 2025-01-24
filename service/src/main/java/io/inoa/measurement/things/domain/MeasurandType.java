package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity
public record MeasurandType(
    @Id @GeneratedValue Long id, String obisId, String name, String description) {}
