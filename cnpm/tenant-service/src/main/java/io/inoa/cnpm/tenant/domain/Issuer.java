package io.inoa.cnpm.tenant.domain;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

@MappedEntity
@Data
public class Issuer {

	@Id
	@GeneratedValue
	private Long id;
	@Relation(Relation.Kind.MANY_TO_ONE)
	private Tenant tenant;

	@MappedProperty
	private String name;
	@MappedProperty
	private URL url;
	@MappedProperty
	private Duration cacheDuration;

	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
