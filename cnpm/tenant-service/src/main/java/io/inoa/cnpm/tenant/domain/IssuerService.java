package io.inoa.cnpm.tenant.domain;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * This a service audience for an issuer.
 */
@MappedEntity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssuerService {

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@Relation(Relation.Kind.MANY_TO_ONE)
	private Issuer issuer;
	@MappedProperty
	private String name;
}
