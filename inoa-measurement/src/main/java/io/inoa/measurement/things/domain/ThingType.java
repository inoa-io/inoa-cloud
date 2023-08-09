package io.inoa.measurement.things.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import lombok.Data;

@MappedEntity
@Data
public class ThingType {
	@Id
	@GeneratedValue
	private Long id;
	@MappedProperty
	private String thingTypeId;
	@MappedProperty
	private String thingTypeReference;
	@MappedProperty
	private String name;
	@TypeDef(type = DataType.JSON)
	private Map<String, Object> jsonSchema;
	@TypeDef(type = DataType.JSON)
	private List<Map<String, Object>> uiLayout;
	@DateCreated
	private Instant created;
	@DateUpdated
	private Instant updated;
}
