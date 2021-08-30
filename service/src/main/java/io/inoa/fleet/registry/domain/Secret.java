package io.inoa.fleet.registry.domain;

import java.time.Instant;
import java.util.UUID;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.Data;
import lombok.ToString;

/**
 * A secret related to {@link Credential}.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class Secret {

	@Id
	@GeneratedValue
	private Long id;

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Credential credential;
	@MappedProperty
	private UUID secretId;

	@MappedProperty
	@ToString.Exclude
	private byte[] password;
	@MappedProperty
	@ToString.Exclude
	private byte[] secret;
	@MappedProperty
	@ToString.Exclude
	private byte[] publicKey;
	@MappedProperty
	@ToString.Exclude
	private byte[] privateKey;

	@DateCreated
	private Instant created;
}
