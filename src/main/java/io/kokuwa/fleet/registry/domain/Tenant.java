package io.kokuwa.fleet.registry.domain;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

/**
 * A tenant.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class Tenant extends BaseEntity {

	@MappedProperty
	private String name;
	@MappedProperty
	private Boolean enabled;
}
