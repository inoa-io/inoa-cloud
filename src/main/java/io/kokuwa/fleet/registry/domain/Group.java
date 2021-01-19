package io.kokuwa.fleet.registry.domain;

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
public class Group extends BaseEntity {

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;
	@MappedProperty
	private String name;
}
