package io.inoa.fleet.auth.domain;

import java.time.Instant;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

@MappedEntity
@Data
public class TenantUser {
	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;
	@Relation(Relation.Kind.MANY_TO_ONE)
	private User user;
	@DateCreated
	private Instant created;
}
