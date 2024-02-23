package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public final class ConfigurationDefinitionIntegerVO implements ConfigurationDefinitionVO {

	public static final java.lang.String JSON_PROPERTY_MINIMUM = "minimum";
	public static final java.lang.String JSON_PROPERTY_MAXIMUM = "maximum";

	/** Key for configuration. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
	@jakarta.validation.constraints.Size(min = 3, max = 48)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String key;

	/** Describes a configuration. */
	@jakarta.validation.constraints.Size(max = 200)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MINIMUM)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer minimum;

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MAXIMUM)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer maximum;

	@Override
	public ConfigurationTypeVO getType() {
		return ConfigurationTypeVO.INTEGER;
	}

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ConfigurationDefinitionIntegerVO other = (ConfigurationDefinitionIntegerVO) object;
		return java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(minimum, other.minimum)
				&& java.util.Objects.equals(maximum, other.maximum);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(key, description, minimum, maximum);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationDefinitionIntegerVO[")
				.append("key=").append(key).append(",")
				.append("description=").append(description).append(",")
				.append("minimum=").append(minimum).append(",")
				.append("maximum=").append(maximum)
				.append("]")
				.toString();
	}

	// fluent

	public ConfigurationDefinitionIntegerVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public ConfigurationDefinitionIntegerVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public ConfigurationDefinitionIntegerVO minimum(java.lang.Integer newMinimum) {
		this.minimum = newMinimum;
		return this;
	}

	public ConfigurationDefinitionIntegerVO maximum(java.lang.Integer newMaximum) {
		this.maximum = newMaximum;
		return this;
	}

	// getter/setter

	public java.lang.String getKey() {
		return key;
	}

	public void setKey(java.lang.String newKey) {
		this.key = newKey;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String newDescription) {
		this.description = newDescription;
	}

	public java.lang.Integer getMinimum() {
		return minimum;
	}

	public void setMinimum(java.lang.Integer newMinimum) {
		this.minimum = newMinimum;
	}

	public java.lang.Integer getMaximum() {
		return maximum;
	}

	public void setMaximum(java.lang.Integer newMaximum) {
		this.maximum = newMaximum;
	}
}
