package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingTypeUpdateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_CATEGORY = "category";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_VERSION = "version";
	public static final java.lang.String JSON_PROPERTY_PROTOCOL = "protocol";
	public static final java.lang.String JSON_PROPERTY_MEASURANDS = "measurands";
	public static final java.lang.String JSON_PROPERTY_CONFIGURATIONS = "configurations";

	/** Human readable name. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 64)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CATEGORY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private ThingTypeCategoryVO category;

	/** Additional description of the thing type */
	@jakarta.validation.constraints.Size(max = 4096)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** An optional version of the thing type (e.g. firmware, revision, ...) */
	@jakarta.validation.constraints.Size(max = 256)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VERSION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String version;

	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PROTOCOL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private ThingTypeProtocolVO protocol;

	/** List of measurands this type of thing supports */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MEASURANDS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Pattern(regexp = "^((?<a>[0-9]{1,3})-)?((?<b>[0-9]{1,3}):)?(S\\.)?(?<cde>(?<c>[0-9A-F]{1,3}).(?<d>[0-9A-F]{1,3})(.(?<e>[0-9A-F]{1,3}))?)([\\*\\&](?<f>[0-9A-F]{1,3}))?$") String> measurands;

	/** List of settings that can be configured for things of this type */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONFIGURATIONS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> configurations;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingTypeUpdateVO other = (ThingTypeUpdateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(category, other.category)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(version, other.version)
				&& java.util.Objects.equals(protocol, other.protocol)
				&& java.util.Objects.equals(measurands, other.measurands)
				&& java.util.Objects.equals(configurations, other.configurations);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, category, description, version, protocol, measurands, configurations);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingTypeUpdateVO[")
				.append("name=").append(name).append(",")
				.append("category=").append(category).append(",")
				.append("description=").append(description).append(",")
				.append("version=").append(version).append(",")
				.append("protocol=").append(protocol).append(",")
				.append("measurands=").append(measurands).append(",")
				.append("configurations=").append(configurations)
				.append("]")
				.toString();
	}

	// fluent

	public ThingTypeUpdateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingTypeUpdateVO category(ThingTypeCategoryVO newCategory) {
		this.category = newCategory;
		return this;
	}

	public ThingTypeUpdateVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public ThingTypeUpdateVO version(java.lang.String newVersion) {
		this.version = newVersion;
		return this;
	}

	public ThingTypeUpdateVO protocol(ThingTypeProtocolVO newProtocol) {
		this.protocol = newProtocol;
		return this;
	}

	public ThingTypeUpdateVO measurands(java.util.List<java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Pattern(regexp = "^((?<a>[0-9]{1,3})-)?((?<b>[0-9]{1,3}):)?(S\\.)?(?<cde>(?<c>[0-9A-F]{1,3}).(?<d>[0-9A-F]{1,3})(.(?<e>[0-9A-F]{1,3}))?)([\\*\\&](?<f>[0-9A-F]{1,3}))?$") String> newMeasurands) {
		this.measurands = newMeasurands;
		return this;
	}
	
	public ThingTypeUpdateVO addMeasurandsItem(java.lang.String measurandsItem) {
		if (this.measurands == null) {
			this.measurands = new java.util.ArrayList<>();
		}
		this.measurands.add(measurandsItem);
		return this;
	}

	public ThingTypeUpdateVO removeMeasurandsItem(java.lang.String measurandsItem) {
		if (this.measurands != null) {
			this.measurands.remove(measurandsItem);
		}
		return this;
	}

	public ThingTypeUpdateVO configurations(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> newConfigurations) {
		this.configurations = newConfigurations;
		return this;
	}
	
	public ThingTypeUpdateVO addConfigurationsItem(ThingConfigurationVO configurationsItem) {
		if (this.configurations == null) {
			this.configurations = new java.util.ArrayList<>();
		}
		this.configurations.add(configurationsItem);
		return this;
	}

	public ThingTypeUpdateVO removeConfigurationsItem(ThingConfigurationVO configurationsItem) {
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

	public ThingTypeCategoryVO getCategory() {
		return category;
	}

	public void setCategory(ThingTypeCategoryVO newCategory) {
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

	public ThingTypeProtocolVO getProtocol() {
		return protocol;
	}

	public void setProtocol(ThingTypeProtocolVO newProtocol) {
		this.protocol = newProtocol;
	}

	public java.util.List<java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Pattern(regexp = "^((?<a>[0-9]{1,3})-)?((?<b>[0-9]{1,3}):)?(S\\.)?(?<cde>(?<c>[0-9A-F]{1,3}).(?<d>[0-9A-F]{1,3})(.(?<e>[0-9A-F]{1,3}))?)([\\*\\&](?<f>[0-9A-F]{1,3}))?$") String> getMeasurands() {
		return measurands;
	}

	public void setMeasurands(java.util.List<java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Pattern(regexp = "^((?<a>[0-9]{1,3})-)?((?<b>[0-9]{1,3}):)?(S\\.)?(?<cde>(?<c>[0-9A-F]{1,3}).(?<d>[0-9A-F]{1,3})(.(?<e>[0-9A-F]{1,3}))?)([\\*\\&](?<f>[0-9A-F]{1,3}))?$") String> newMeasurands) {
		this.measurands = newMeasurands;
	}

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> newConfigurations) {
		this.configurations = newConfigurations;
	}
}
