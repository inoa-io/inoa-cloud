package io.kokuwa.fleet.registry.domain;

import java.time.Instant;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A gateway assignment to a group.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
@NoArgsConstructor
public class GatewayGroup {

	@EmbeddedId
	private GatewayGroupPK pk;
	@DateCreated
	private Instant created;

	@Embeddable
	@Data
	@AllArgsConstructor
	public static class GatewayGroupPK {

		@MappedProperty("gateway_id")
		private final Long gatewayId;
		@MappedProperty("group_id")
		private final Long groupId;

		public GatewayGroupPK(Gateway gateway, Group group) {
			this.gatewayId = gateway.getId();
			this.groupId = group.getId();
		}
	}
}
