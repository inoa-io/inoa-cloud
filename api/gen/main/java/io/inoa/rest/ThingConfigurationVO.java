package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingConfigurationVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_TYPE = "type";
	public static final java.lang.String JSON_PROPERTY_VALIDATION_REGEX = "validation_regex";
	public static final java.lang.String JSON_PROPERTY_VALUE = "value";

	/** Property name of the configuration setting. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Description of the configuration setting. */
	@jakarta.validation.constraints.Size(max = 1024)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private ThingConfigurationTypeVO type;

	/** Regex to validation the configuration value. */
	@jakarta.validation.constraints.Size(max = 1024)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALIDATION_REGEX)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String validationRegex;

	/** The value of the configuration setting. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 1024)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALUE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String value;

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
				&& java.util.Objects.equals(validationRegex, other.validationRegex)
				&& java.util.Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, description, type, validationRegex, value);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingConfigurationVO[")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("type=").append(type).append(",")
				.append("validationRegex=").append(validationRegex).append(",")
				.append("value=").append(value)
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

	public ThingConfigurationVO type(ThingConfigurationTypeVO newType) {
		this.type = newType;
		return this;
	}

	public ThingConfigurationVO validationRegex(java.lang.String newValidationRegex) {
		this.validationRegex = newValidationRegex;
		return this;
	}

	public ThingConfigurationVO value(java.lang.String newValue) {
		this.value = newValue;
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

	public ThingConfigurationTypeVO getType() {
		return type;
	}

	public void setType(ThingConfigurationTypeVO newType) {
		this.type = newType;
	}

	public java.lang.String getValidationRegex() {
		return validationRegex;
	}

	public void setValidationRegex(java.lang.String newValidationRegex) {
		this.validationRegex = newValidationRegex;
	}

	public java.lang.String getValue() {
		return value;
	}

	public void setValue(java.lang.String newValue) {
		this.value = newValue;
	}
}
