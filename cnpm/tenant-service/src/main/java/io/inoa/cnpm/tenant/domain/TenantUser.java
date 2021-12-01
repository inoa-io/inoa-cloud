package io.inoa.cnpm.tenant.domain;

import java.time.Instant;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

@MappedEntity
@Data
public class TenantUser {

	@Id
	@GeneratedValue
	private Long id;

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;
	@Relation(Relation.Kind.MANY_TO_ONE)
	private User user;

	@DateCreated
	private Instant created;
}
