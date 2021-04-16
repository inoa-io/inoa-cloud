package io.kokuwa.fleet.registry.domain;

import io.kokuwa.fleet.registry.rest.management.SecretTypeVO;
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
public class Secret extends BaseEntity {

	@Relation(Relation.Kind.MANY_TO_ONE)
	private Gateway gateway;
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
}
