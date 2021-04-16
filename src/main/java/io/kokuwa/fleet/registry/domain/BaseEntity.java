package io.kokuwa.fleet.registry.domain;

import java.time.Instant;
import java.util.UUID;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.event.PrePersist;
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
	private UUID externalId;
	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;

	@PrePersist
	void initExternalId() {
		if (externalId == null) {
			externalId = UUID.randomUUID();
		}
	}
}
