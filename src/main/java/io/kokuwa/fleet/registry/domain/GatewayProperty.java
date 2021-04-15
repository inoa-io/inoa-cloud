package io.kokuwa.fleet.registry.domain;

import java.time.Instant;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

/**
 * A property of a gateway.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class GatewayProperty {

	@EmbeddedId
	private GatewayPropertyPK pk;
	@MappedProperty
	private String value;
	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;

	@Embeddable
	@Data
	public static class GatewayPropertyPK {

		@MappedProperty("gateway_id")
		private final Long gatewayId;

		@MappedProperty("key")
		private final String key;
	}
}
