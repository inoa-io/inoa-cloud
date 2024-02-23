package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public final class ConfigurationDefinitionStringVO implements ConfigurationDefinitionVO {

	public static final java.lang.String JSON_PROPERTY_MIN_LENGTH = "minLength";
	public static final java.lang.String JSON_PROPERTY_MAX_LENGTH = "maxLength";
	public static final java.lang.String JSON_PROPERTY_PATTERN = "pattern";

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

	@jakarta.validation.constraints.Min(1)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MIN_LENGTH)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer minLength;

	@jakarta.validation.constraints.Max(1000)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MAX_LENGTH)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer maxLength;

	@jakarta.validation.constraints.Size(max = 1000)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PATTERN)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String pattern;

	@Override
	public ConfigurationTypeVO getType() {
		return ConfigurationTypeVO.STRING;
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
		ConfigurationDefinitionStringVO other = (ConfigurationDefinitionStringVO) object;
		return java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(minLength, other.minLength)
				&& java.util.Objects.equals(maxLength, other.maxLength)
				&& java.util.Objects.equals(pattern, other.pattern);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(key, description, minLength, maxLength, pattern);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationDefinitionStringVO[")
				.append("key=").append(key).append(",")
				.append("description=").append(description).append(",")
				.append("minLength=").append(minLength).append(",")
				.append("maxLength=").append(maxLength).append(",")
				.append("pattern=").append(pattern)
				.append("]")
				.toString();
	}

	// fluent

	public ConfigurationDefinitionStringVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public ConfigurationDefinitionStringVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public ConfigurationDefinitionStringVO minLength(java.lang.Integer newMinLength) {
		this.minLength = newMinLength;
		return this;
	}

	public ConfigurationDefinitionStringVO maxLength(java.lang.Integer newMaxLength) {
		this.maxLength = newMaxLength;
		return this;
	}

	public ConfigurationDefinitionStringVO pattern(java.lang.String newPattern) {
		this.pattern = newPattern;
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

	public java.lang.Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(java.lang.Integer newMinLength) {
		this.minLength = newMinLength;
	}

	public java.lang.Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(java.lang.Integer newMaxLength) {
		this.maxLength = newMaxLength;
	}

	public java.lang.String getPattern() {
		return pattern;
	}

	public void setPattern(java.lang.String newPattern) {
		this.pattern = newPattern;
	}
}
