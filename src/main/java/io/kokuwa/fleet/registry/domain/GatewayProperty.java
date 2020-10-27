package io.kokuwa.fleet.registry.domain;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

/**
 * A property of a gateway.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class GatewayProperty extends BaseEntity {

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Gateway gateway;
	@MappedProperty
	private String key;
	@MappedProperty
	private String value;
}
