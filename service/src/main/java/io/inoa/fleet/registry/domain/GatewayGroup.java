package io.inoa.fleet.registry.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

/**
 * A gateway assignment to a group.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class GatewayGroup {

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Gateway gateway;

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Group group;

	@DateCreated(truncatedTo = ChronoUnit.MILLIS)
	private Instant created;
}
