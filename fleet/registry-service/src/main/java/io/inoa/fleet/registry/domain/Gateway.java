package io.inoa.fleet.registry.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Transient;
import lombok.Data;

/**
 * A gateway.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class Gateway {

	@Id
	@GeneratedValue
	private Long id;

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;
	@MappedProperty
	private UUID gatewayId;
	@MappedProperty
	private String name;
	@MappedProperty
	private Boolean enabled;
	@Relation(Relation.Kind.EMBEDDED)
	private GatewayStatus status;

	@DateCreated(truncatedTo = ChronoUnit.MILLIS)
	private Instant created;
	@DateUpdated(truncatedTo = ChronoUnit.MILLIS)
	private Instant updated;

	@Transient
	@Nullable
	private List<Group> groups;
	@Transient
	@Nullable
	private List<GatewayProperty> properties;
}
