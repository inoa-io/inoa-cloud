package io.inoa.tenant.domain;

import java.time.Instant;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

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

	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
