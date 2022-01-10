package io.inoa.cnpm.tenant.domain;

import java.time.Instant;
import java.util.List;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This class represents a tenant.
 */
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
	@Transient
	private List<Issuer> issuers;

	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
	@MappedProperty
	private Instant deleted;
}
