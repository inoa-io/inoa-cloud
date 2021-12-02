package io.inoa.cnpm.tenant.domain;

import java.time.Instant;
import java.util.Set;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@MappedEntity
@Data
public class Tenant {

	@Id
	@GeneratedValue
	private Long id;

	@MappedProperty
	private String tenantId;
	@MappedProperty
	private Boolean enabled;
	@MappedProperty
	private String name;
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "tenant")
	private Set<Issuer> issuers;

	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
	@MappedProperty
	private Instant deleted;
}
