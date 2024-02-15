package io.inoa.measurement.things.domain;

import java.time.Instant;
import java.util.Map;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import lombok.Data;

@MappedEntity
@Data
public class Thing {
	@Id
	@GeneratedValue
	private Long id;
	@MappedProperty
	private String thingId;
	@MappedProperty
	private String urn;
	@MappedProperty
	private String tenantId;
	@MappedProperty
	private String gatewayId;
	@MappedProperty
	private String name;
	@MappedProperty
	@TypeDef(type = DataType.JSON)
	private Map<String, Object> config;
	@Relation(Relation.Kind.MANY_TO_ONE)
	private ThingType thingType;
	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
