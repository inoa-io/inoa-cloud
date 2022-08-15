package io.inoa.fleet.registry.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import io.inoa.fleet.registry.rest.management.CredentialTypeVO;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Cascade;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
	private String authId;
	@MappedProperty
	private Boolean enabled;

	@MappedProperty
	private CredentialTypeVO type;
	@Relation(value = Relation.Kind.ONE_TO_MANY, mappedBy = "credential", cascade = Cascade.PERSIST)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Secret> secrets;

	@DateCreated(truncatedTo = ChronoUnit.MILLIS)
	private Instant created;
	@DateUpdated(truncatedTo = ChronoUnit.MILLIS)
	private Instant updated;
}
