package io.inoa.fleet.registry.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

/**
 * A group related to a tenant.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class Group {

	@Id
	@GeneratedValue
	private Long id;

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;

	@MappedProperty
	private UUID groupId;
	@MappedProperty
	private String name;

	@DateCreated(truncatedTo = ChronoUnit.MILLIS)
	private Instant created;

	@DateUpdated(truncatedTo = ChronoUnit.MILLIS)
	private Instant updated;
}
