package io.kokuwa.fleet.registry.domain;

import java.time.Instant;
import java.util.UUID;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
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
	private UUID tenantId;
	@MappedProperty
	private String name;
	@MappedProperty
	private Boolean enabled;

	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
