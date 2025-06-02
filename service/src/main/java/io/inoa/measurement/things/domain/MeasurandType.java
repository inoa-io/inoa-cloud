package io.inoa.measurement.things.domain;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@MappedEntity
@EqualsAndHashCode
public class MeasurandType {
	@Id
	@GeneratedValue
	private Long id;
	private String obisId;
	private String name;
	private String description;
}
