package io.kokuwa.fleet.registry.domain;

import java.time.Instant;
import java.util.UUID;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

/**
 * Base for all database entities referenced by external systems.
 *
 * @author Stephan Schnabel
 */
@Data
public abstract class BaseEntity {

	@Id
	@GeneratedValue
	private Long id;
	@MappedProperty
	@AutoPopulated
	private UUID externalId;
	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
