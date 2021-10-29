package io.inoa.fleet.auth.domain;

import java.time.Instant;
import java.util.UUID;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import lombok.Data;

@MappedEntity
@Data
public class User {
	@Id
	@GeneratedValue
	private Long id;
	@MappedProperty
	private UUID userId;
	@MappedProperty
	private String email;
	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
