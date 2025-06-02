package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingConfigurationVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_TYPE = "type";
	public static final java.lang.String JSON_PROPERTY_VALIDATION_REGEX = "validation_regex";

	/** Name of the configuration */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 256)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** An optional description of the configuration */
	@jakarta.validation.constraints.Size(max = 4096)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** The type of the configuration */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private Type type;

	/** An optional regular expression validating the configuration value */
	@jakarta.validation.constraints.Size(max = 4096)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALIDATION_REGEX)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String validationRegex;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingConfigurationVO other = (ThingConfigurationVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(type, other.type)
				&& java.util.Objects.equals(validationRegex, other.validationRegex);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, description, type, validationRegex);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingConfigurationVO[")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("type=").append(type).append(",")
				.append("validationRegex=").append(validationRegex)
				.append("]")
				.toString();
	}

	// fluent

	public ThingConfigurationVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingConfigurationVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public ThingConfigurationVO type(Type newType) {
		this.type = newType;
		return this;
	}

	public ThingConfigurationVO validationRegex(java.lang.String newValidationRegex) {
		this.validationRegex = newValidationRegex;
		return this;
	}

	// getter/setter

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

	public Type getType() {
		return type;
	}

	public void setType(Type newType) {
		this.type = newType;
	}

	public java.lang.String getValidationRegex() {
		return validationRegex;
	}

	public void setValidationRegex(java.lang.String newValidationRegex) {
		this.validationRegex = newValidationRegex;
	}

@io.micronaut.serde.annotation.Serdeable
public enum Type {

	STRING("STRING"),
	NUMBER("NUMBER"),
	BOOLEAN("BOOLEAN");

	public static final java.lang.String STRING_VALUE = "STRING";
	public static final java.lang.String NUMBER_VALUE = "NUMBER";
	public static final java.lang.String BOOLEAN_VALUE = "BOOLEAN";

	private final java.lang.String value;

	private Type(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static Type toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<Type> toOptional(java.lang.String value) {
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
