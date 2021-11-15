package io.inoa.tenant.domain;

import java.time.Instant;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedEntity
@Data
@NoArgsConstructor
public class TenantUser {

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;
	@Relation(Relation.Kind.MANY_TO_ONE)
	private User user;

	@DateCreated
	private Instant created;

	public TenantUser(Tenant tenant, User user) {
		this.tenant = tenant;
		this.user = user;
	}
}
