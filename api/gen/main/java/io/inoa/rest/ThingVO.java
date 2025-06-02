package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_THING_TYPE_ID = "thing_type_id";
	public static final java.lang.String JSON_PROPERTY_MEASURANDS = "measurands";
	public static final java.lang.String JSON_PROPERTY_CONFIGURATIONS = "configurations";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.util.UUID id;

	/** Human readable name. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 64)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Long description of the thing */
	@jakarta.validation.constraints.Size(max = 4096)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
	@jakarta.validation.constraints.Size(min = 4, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String gatewayId;

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_THING_TYPE_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String thingTypeId;

	/** List of measurands and its configuration */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MEASURANDS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandVO> measurands;

	/** Key / value map of thing configurations */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONFIGURATIONS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Map<String, java.lang.String> configurations;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingVO other = (ThingVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(thingTypeId, other.thingTypeId)
				&& java.util.Objects.equals(measurands, other.measurands)
				&& java.util.Objects.equals(configurations, other.configurations);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name, description, gatewayId, thingTypeId, measurands, configurations);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingVO[")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("gatewayId=").append(gatewayId).append(",")
				.append("thingTypeId=").append(thingTypeId).append(",")
				.append("measurands=").append(measurands).append(",")
				.append("configurations=").append(configurations)
				.append("]")
				.toString();
	}

	// fluent

	public ThingVO id(java.util.UUID newId) {
		this.id = newId;
		return this;
	}

	public ThingVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public ThingVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public ThingVO thingTypeId(java.lang.String newThingTypeId) {
		this.thingTypeId = newThingTypeId;
		return this;
	}

	public ThingVO measurands(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandVO> newMeasurands) {
		this.measurands = newMeasurands;
		return this;
	}
	
	public ThingVO addMeasurandsItem(MeasurandVO measurandsItem) {
		if (this.measurands == null) {
			this.measurands = new java.util.ArrayList<>();
		}
		this.measurands.add(measurandsItem);
		return this;
	}

	public ThingVO removeMeasurandsItem(MeasurandVO measurandsItem) {
		if (this.measurands != null) {
			this.measurands.remove(measurandsItem);
		}
		return this;
	}

	public ThingVO configurations(java.util.Map<String, java.lang.String> newConfigurations) {
		this.configurations = newConfigurations;
		return this;
	}
	
	public ThingVO putConfigurationsItem(java.lang.String key, java.lang.String configurationsItem) {
		if (this.configurations == null) {
			this.configurations = new java.util.HashMap<>();
		}
		this.configurations.put(key, configurationsItem);
		return this;
	}

	public ThingVO removeConfigurationsItem(java.lang.String key) {
		if (this.configurations != null) {
			this.configurations.remove(key);
		}
		return this;
	}

	// getter/setter

	public java.util.UUID getId() {
		return id;
	}

	public void setId(java.util.UUID newId) {
		this.id = newId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String newDescription) {
		this.description = newDescription;
	}

	public java.lang.String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
	}

	public java.lang.String getThingTypeId() {
		return thingTypeId;
	}

	public void setThingTypeId(java.lang.String newThingTypeId) {
		this.thingTypeId = newThingTypeId;
	}

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandVO> getMeasurands() {
		return measurands;
	}

	public void setMeasurands(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandVO> newMeasurands) {
		this.measurands = newMeasurands;
	}

	public java.util.Map<String, java.lang.String> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(java.util.Map<String, java.lang.String> newConfigurations) {
		this.configurations = newConfigurations;
	}
}
