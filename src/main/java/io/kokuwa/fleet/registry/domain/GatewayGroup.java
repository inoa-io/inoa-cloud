package io.kokuwa.fleet.registry.domain;

import java.time.Instant;
import java.util.UUID;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

/**
 * A gateway assignment to a group.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class GatewayGroup {

	@EmbeddedId
	private GatewayGroupPK pk;
	@DateCreated
	private Instant created;

	@Embeddable
	@Data
	public static class GatewayGroupPK {

		@MappedProperty("gateway_id")
		private final UUID gatewayId;

		@MappedProperty("group_id")
		private final UUID groupId;
	}
}
