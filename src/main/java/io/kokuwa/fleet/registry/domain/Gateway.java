package io.kokuwa.fleet.registry.domain;

import java.util.List;

import io.micronaut.core.annotation.Nullable;
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
public class Gateway extends BaseEntity {

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;
	@MappedProperty
	private String name;
	@MappedProperty
	private Boolean enabled;
	@Transient
	@Nullable
	private List<Group> groups;
	@Transient
	@Nullable
	private List<GatewayProperty> properties;
}
