package io.inoa.fleet.registry.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import io.inoa.rest.CredentialTypeVO;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.Data;
import lombok.ToString;

/**
 * A credential of gateway.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class Credential {

	@Id
	@GeneratedValue
	private Long id;

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Gateway gateway;

	@MappedProperty
	private UUID credentialId;
	@MappedProperty
	private String name;
	@MappedProperty
	private Boolean enabled;

	@MappedProperty
	private CredentialTypeVO type;
	@MappedProperty
	@ToString.Exclude
	private byte[] value;

	@DateCreated(truncatedTo = ChronoUnit.MILLIS)
	private Instant created;

	@DateUpdated(truncatedTo = ChronoUnit.MILLIS)
	private Instant updated;
}
