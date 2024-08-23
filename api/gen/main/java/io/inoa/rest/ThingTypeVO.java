package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingTypeVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_CATEGORY = "category";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_VERSION = "version";
	public static final java.lang.String JSON_PROPERTY_MEASURAND_TYPES = "measurand_types";
	public static final java.lang.String JSON_PROPERTY_CONFIGURATIONS = "configurations";
	public static final java.lang.String JSON_PROPERTY_PROTOCOL = "protocol";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String id;

	/** Name. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** Category. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CATEGORY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String category;

	/** Longer description of a thing type. */
	@jakarta.validation.constraints.Size(max = 1024)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** Physical version or firmware version of the thing type */
	@jakarta.validation.constraints.Size(max = 64)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VERSION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String version;

	/** List of measurands this type of thing offers. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MEASURAND_TYPES)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandTypeVO> measurandTypes;

	/** List of configuration settings this type of thing needs. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONFIGURATIONS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> configurations;

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PROTOCOL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private MeasurandProtocolVO protocol;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingTypeVO other = (ThingTypeVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(category, other.category)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(version, other.version)
				&& java.util.Objects.equals(measurandTypes, other.measurandTypes)
				&& java.util.Objects.equals(configurations, other.configurations)
				&& java.util.Objects.equals(protocol, other.protocol);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name, category, description, version, measurandTypes, configurations, protocol);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingTypeVO[")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("category=").append(category).append(",")
				.append("description=").append(description).append(",")
				.append("version=").append(version).append(",")
				.append("measurandTypes=").append(measurandTypes).append(",")
				.append("configurations=").append(configurations).append(",")
				.append("protocol=").append(protocol)
				.append("]")
				.toString();
	}

	// fluent

	public ThingTypeVO id(java.lang.String newId) {
		this.id = newId;
		return this;
	}

	public ThingTypeVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingTypeVO category(java.lang.String newCategory) {
		this.category = newCategory;
		return this;
	}

	public ThingTypeVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public ThingTypeVO version(java.lang.String newVersion) {
		this.version = newVersion;
		return this;
	}

	public ThingTypeVO measurandTypes(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandTypeVO> newMeasurandTypes) {
		this.measurandTypes = newMeasurandTypes;
		return this;
	}
	
	public ThingTypeVO addMeasurandTypesItem(MeasurandTypeVO measurandTypesItem) {
		if (this.measurandTypes == null) {
			this.measurandTypes = new java.util.ArrayList<>();
		}
		this.measurandTypes.add(measurandTypesItem);
		return this;
	}

	public ThingTypeVO removeMeasurandTypesItem(MeasurandTypeVO measurandTypesItem) {
		if (this.measurandTypes != null) {
			this.measurandTypes.remove(measurandTypesItem);
		}
		return this;
	}

	public ThingTypeVO configurations(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> newConfigurations) {
		this.configurations = newConfigurations;
		return this;
	}
	
	public ThingTypeVO addConfigurationsItem(ThingConfigurationVO configurationsItem) {
		if (this.configurations == null) {
			this.configurations = new java.util.ArrayList<>();
		}
		this.configurations.add(configurationsItem);
		return this;
	}

	public ThingTypeVO removeConfigurationsItem(ThingConfigurationVO configurationsItem) {
		if (this.configurations != null) {
			this.configurations.remove(configurationsItem);
		}
		return this;
	}

	public ThingTypeVO protocol(MeasurandProtocolVO newProtocol) {
		this.protocol = newProtocol;
		return this;
	}

	// getter/setter

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String newId) {
		this.id = newId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getCategory() {
		return category;
	}

	public void setCategory(java.lang.String newCategory) {
		this.category = newCategory;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String newDescription) {
		this.description = newDescription;
	}

	public java.lang.String getVersion() {
		return version;
	}

	public void setVersion(java.lang.String newVersion) {
		this.version = newVersion;
	}

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandTypeVO> getMeasurandTypes() {
		return measurandTypes;
	}

	public void setMeasurandTypes(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandTypeVO> newMeasurandTypes) {
		this.measurandTypes = newMeasurandTypes;
	}

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> newConfigurations) {
		this.configurations = newConfigurations;
	}

	public MeasurandProtocolVO getProtocol() {
		return protocol;
	}

	public void setProtocol(MeasurandProtocolVO newProtocol) {
		this.protocol = newProtocol;
	}
}
