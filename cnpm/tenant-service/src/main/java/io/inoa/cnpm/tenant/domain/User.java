package io.inoa.cnpm.tenant.domain;

import java.time.Instant;
import java.util.UUID;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

/**
 * This class represents a user (without tenant context).
 */
@MappedEntity
@Data
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@MappedProperty
	@AutoPopulated(updateable = false)
	private UUID userId;
	@MappedProperty
	private String email;

	@DateCreated
	private Instant created;
}
