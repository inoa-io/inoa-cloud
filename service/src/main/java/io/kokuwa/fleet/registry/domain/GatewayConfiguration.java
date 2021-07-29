package io.kokuwa.fleet.registry.domain;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;
import lombok.Data;

/**
 * Configuration value for {@link ConfigurationDefinition} in scope of {@link Gateway}.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class GatewayConfiguration implements Configuration {

	@Relation(Kind.MANY_TO_ONE)
	private Gateway gateway;
	@Relation(Kind.MANY_TO_ONE)
	private ConfigurationDefinition definition;
	@MappedProperty
	private String value;
}
