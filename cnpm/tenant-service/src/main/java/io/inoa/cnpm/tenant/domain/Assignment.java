package io.inoa.cnpm.tenant.domain;

import java.time.Instant;

import io.inoa.cnpm.tenant.rest.UserRoleVO;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import io.micronaut.data.model.DataType;
import lombok.Data;

/**
 * This is an assignment of an user to a tenant.
 */
@MappedEntity
@Data
public class Assignment {

	@Id
	@GeneratedValue
	private Long id;

	@Relation(Kind.MANY_TO_ONE)
	private Tenant tenant;
	@Relation(Kind.MANY_TO_ONE)
	private User user;
	@MappedProperty(type = DataType.STRING)
	private UserRoleVO role;

	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
