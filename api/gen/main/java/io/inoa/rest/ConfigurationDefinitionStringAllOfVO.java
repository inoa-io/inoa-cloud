package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ConfigurationDefinitionStringAllOfVO {

	public static final java.lang.String JSON_PROPERTY_MIN_LENGTH = "minLength";
	public static final java.lang.String JSON_PROPERTY_MAX_LENGTH = "maxLength";
	public static final java.lang.String JSON_PROPERTY_PATTERN = "pattern";

	@javax.validation.constraints.Min(1)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MIN_LENGTH)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer minLength;

	@javax.validation.constraints.Max(1000)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MAX_LENGTH)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer maxLength;

	@javax.validation.constraints.Size(max = 1000)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PATTERN)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String pattern;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ConfigurationDefinitionStringAllOfVO other = (ConfigurationDefinitionStringAllOfVO) object;
		return java.util.Objects.equals(minLength, other.minLength)
				&& java.util.Objects.equals(maxLength, other.maxLength)
				&& java.util.Objects.equals(pattern, other.pattern);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(minLength, maxLength, pattern);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationDefinitionStringAllOfVO[")
				.append("minLength=").append(minLength).append(",")
				.append("maxLength=").append(maxLength).append(",")
				.append("pattern=").append(pattern)
				.append("]")
				.toString();
	}

	// fluent

	public ConfigurationDefinitionStringAllOfVO minLength(java.lang.Integer newMinLength) {
		this.minLength = newMinLength;
		return this;
	}

	public ConfigurationDefinitionStringAllOfVO maxLength(java.lang.Integer newMaxLength) {
		this.maxLength = newMaxLength;
		return this;
	}

	public ConfigurationDefinitionStringAllOfVO pattern(java.lang.String newPattern) {
		this.pattern = newPattern;
		return this;
	}

	// getter/setter

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
