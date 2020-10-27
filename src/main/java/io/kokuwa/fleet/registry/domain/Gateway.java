package io.kokuwa.fleet.registry.domain;

import java.util.Set;
import java.util.UUID;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.Data;
import lombok.ToString;

/**
 * A gateway.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class Gateway extends BaseEntity {

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;

	@MappedProperty
	private UUID uuid;
	@MappedProperty
	private String serial;
	@Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "gateway")
	@ToString.Exclude
	private Set<GatewayProperty> properties;
}
