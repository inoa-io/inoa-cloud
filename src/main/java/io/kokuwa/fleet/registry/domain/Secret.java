package io.kokuwa.fleet.registry.domain;

import java.time.Instant;
import java.util.UUID;

import io.kokuwa.fleet.registry.rest.management.SecretTypeVO;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.Data;

/**
 * A secret of gateway.
 *
 * @author Stephan Schnabel
 */
@MappedEntity("gateway_secret")
@Data
public class Secret {

	@Id
	@GeneratedValue
	private Long id;

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Gateway gateway;
	@MappedProperty
	private UUID secretId;
	@MappedProperty
	private String name;
	@MappedProperty
	private Boolean enabled;
	@MappedProperty
	private SecretTypeVO type;
	@MappedProperty
	private byte[] hmac;
	@MappedProperty
	private byte[] publicKey;
	@MappedProperty
	private byte[] privateKey;

	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
