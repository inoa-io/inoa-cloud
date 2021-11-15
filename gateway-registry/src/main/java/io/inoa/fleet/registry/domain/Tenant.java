package io.inoa.fleet.registry.domain;

import java.time.Instant;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
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
public class Tenant {

	@Id
	@GeneratedValue
	private Long id;

	@MappedProperty
	private String tenantId;
	@MappedProperty
	private String name;
	@MappedProperty
	private Boolean enabled;

	@MappedProperty
	private Instant created;
	@MappedProperty
	private Instant updated;
	@MappedProperty
	private Instant deleted;
}
