package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingTypeUpdateVO {

	public static final java.lang.String JSON_PROPERTY_IDENTIFIER = "identifier";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_CATEGORY = "category";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_VERSION = "version";
	public static final java.lang.String JSON_PROPERTY_PROTOCOL = "protocol";
	public static final java.lang.String JSON_PROPERTY_MEASURANDS = "measurands";
	public static final java.lang.String JSON_PROPERTY_CONFIGURATIONS = "configurations";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_IDENTIFIER)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String identifier;

	/** Human readable name. */
	@jakarta.validation.constraints.Size(max = 64)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** Category for logical grouping. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CATEGORY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private Category category;

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

	/** The protocol the thing type uses. If a thing supports several protocols, one may define several thing types. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PROTOCOL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private Protocol protocol;

	/** List of measurands this type of thing supports */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MEASURANDS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandTypeVO> measurands;

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
		return java.util.Objects.equals(identifier, other.identifier)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(category, other.category)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(version, other.version)
				&& java.util.Objects.equals(protocol, other.protocol)
				&& java.util.Objects.equals(measurands, other.measurands)
				&& java.util.Objects.equals(configurations, other.configurations);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(identifier, name, category, description, version, protocol, measurands, configurations);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingTypeUpdateVO[")
				.append("identifier=").append(identifier).append(",")
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

	public ThingTypeUpdateVO identifier(java.lang.String newIdentifier) {
		this.identifier = newIdentifier;
		return this;
	}

	public ThingTypeUpdateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingTypeUpdateVO category(Category newCategory) {
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

	public ThingTypeUpdateVO protocol(Protocol newProtocol) {
		this.protocol = newProtocol;
		return this;
	}

	public ThingTypeUpdateVO measurands(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandTypeVO> newMeasurands) {
		this.measurands = newMeasurands;
		return this;
	}
	
	public ThingTypeUpdateVO addMeasurandsItem(MeasurandTypeVO measurandsItem) {
		if (this.measurands == null) {
			this.measurands = new java.util.ArrayList<>();
		}
		this.measurands.add(measurandsItem);
		return this;
	}

	public ThingTypeUpdateVO removeMeasurandsItem(MeasurandTypeVO measurandsItem) {
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

	public java.lang.String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(java.lang.String newIdentifier) {
		this.identifier = newIdentifier;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category newCategory) {
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

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol newProtocol) {
		this.protocol = newProtocol;
	}

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandTypeVO> getMeasurands() {
		return measurands;
	}

	public void setMeasurands(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid MeasurandTypeVO> newMeasurands) {
		this.measurands = newMeasurands;
	}

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid ThingConfigurationVO> newConfigurations) {
		this.configurations = newConfigurations;
	}

@io.micronaut.serde.annotation.Serdeable
public enum Category {

	NONE("NONE"),
	ELECTRIC_METER("ELECTRIC_METER"),
	GAS_METER("GAS_METER"),
	CURRENT_TRANSFORMER("CURRENT_TRANSFORMER"),
	SMART_PLUG("SMART_PLUG");

	public static final java.lang.String NONE_VALUE = "NONE";
	public static final java.lang.String ELECTRIC_METER_VALUE = "ELECTRIC_METER";
	public static final java.lang.String GAS_METER_VALUE = "GAS_METER";
	public static final java.lang.String CURRENT_TRANSFORMER_VALUE = "CURRENT_TRANSFORMER";
	public static final java.lang.String SMART_PLUG_VALUE = "SMART_PLUG";

	private final java.lang.String value;

	private Category(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static Category toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<Category> toOptional(java.lang.String value) {
		return java.util.Arrays
				.stream(values())
				.filter(e -> e.value.equals(value))
				.findAny();
	}

	@com.fasterxml.jackson.annotation.JsonValue
	public java.lang.String getValue() {
		return value;
	}
}

@io.micronaut.serde.annotation.Serdeable
public enum Protocol {

	JSON_REST_HTTP("JSON_REST_HTTP"),
	MODBUS_RS458("MODBUS_RS458"),
	MODBUS_TCP("MODBUS_TCP"),
	S0("S0"),
	MBUS("MBUS"),
	WMBUS("WMBUS");

	public static final java.lang.String JSON_REST_HTTP_VALUE = "JSON_REST_HTTP";
	public static final java.lang.String MODBUS_RS458_VALUE = "MODBUS_RS458";
	public static final java.lang.String MODBUS_TCP_VALUE = "MODBUS_TCP";
	public static final java.lang.String S0_VALUE = "S0";
	public static final java.lang.String MBUS_VALUE = "MBUS";
	public static final java.lang.String WMBUS_VALUE = "WMBUS";

	private final java.lang.String value;

	private Protocol(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static Protocol toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<Protocol> toOptional(java.lang.String value) {
		return java.util.Arrays
				.stream(values())
				.filter(e -> e.value.equals(value))
				.findAny();
	}

	@com.fasterxml.jackson.annotation.JsonValue
	public java.lang.String getValue() {
		return value;
	}
}
}
