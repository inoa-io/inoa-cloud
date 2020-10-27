package io.kokuwa.fleet.registry.domain;

import java.time.Instant;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import lombok.Data;

/**
 * Base for all database entitites.
 *
 * @author Stephan Schnabel
 */
@Data
public abstract class BaseEntity {

	@Id
	@GeneratedValue
	private Long id;
	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
