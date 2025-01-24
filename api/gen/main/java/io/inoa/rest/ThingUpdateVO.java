package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingUpdateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_THING_TYPE_ID = "thing_type_id";
	public static final java.lang.String JSON_PROPERTY_MEASURANDS = "measurands";
	public static final java.lang.String JSON_PROPERTY_CONFIGURATIONS = "configurations";

	/** Human readable name. */
	@jakarta.validation.constraints.Size(max = 64)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** Long description of the thing */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT)
	private org.openapitools.jackson.nullable.JsonNullable<java.lang.Object> description = org.openapitools.jackson.nullable.JsonNullable.of(null);

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
	@jakarta.validation.constraints.Size(min = 4, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String gatewayId;

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_THING_TYPE_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String thingTypeId;

	/** List of measurands and its configuration */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MEASURANDS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandVO> measurands;

	/** List of thing configurations */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONFIGURATIONS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationsInnerVO> configurations;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingUpdateVO other = (ThingUpdateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(thingTypeId, other.thingTypeId)
				&& java.util.Objects.equals(measurands, other.measurands)
				&& java.util.Objects.equals(configurations, other.configurations);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, description, gatewayId, thingTypeId, measurands, configurations);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingUpdateVO[")
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

	public ThingUpdateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingUpdateVO description(org.openapitools.jackson.nullable.JsonNullable<java.lang.Object> newDescription) {
		this.description = newDescription;
		return this;
	}

	public ThingUpdateVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public ThingUpdateVO thingTypeId(java.lang.String newThingTypeId) {
		this.thingTypeId = newThingTypeId;
		return this;
	}

	public ThingUpdateVO measurands(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandVO> newMeasurands) {
		this.measurands = newMeasurands;
		return this;
	}
	
	public ThingUpdateVO addMeasurandsItem(MeasurandVO measurandsItem) {
		if (this.measurands == null) {
			this.measurands = new java.util.ArrayList<>();
		}
		this.measurands.add(measurandsItem);
		return this;
	}

	public ThingUpdateVO removeMeasurandsItem(MeasurandVO measurandsItem) {
		if (this.measurands != null) {
			this.measurands.remove(measurandsItem);
		}
		return this;
	}

	public ThingUpdateVO configurations(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationsInnerVO> newConfigurations) {
		this.configurations = newConfigurations;
		return this;
	}
	
	public ThingUpdateVO addConfigurationsItem(ThingConfigurationsInnerVO configurationsItem) {
		if (this.configurations == null) {
			this.configurations = new java.util.ArrayList<>();
		}
		this.configurations.add(configurationsItem);
		return this;
	}

	public ThingUpdateVO removeConfigurationsItem(ThingConfigurationsInnerVO configurationsItem) {
		if (this.configurations != null) {
			this.configurations.remove(configurationsItem);
		}
		return this;
	}

	// getter/setter

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public org.openapitools.jackson.nullable.JsonNullable<java.lang.Object> getDescription() {
		return description;
	}

	public void setDescription(org.openapitools.jackson.nullable.JsonNullable<java.lang.Object> newDescription) {
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

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationsInnerVO> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationsInnerVO> newConfigurations) {
		this.configurations = newConfigurations;
	}
}
