package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ConfigurationDefinitionIntegerVO extends ConfigurationDefinitionVO {

	public static final java.lang.String JSON_PROPERTY_MINIMUM = "minimum";
	public static final java.lang.String JSON_PROPERTY_MAXIMUM = "maximum";

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
		return java.util.Objects.equals(minimum, other.minimum)
				&& java.util.Objects.equals(maximum, other.maximum)
				&& super.equals(object);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(minimum, maximum, super.hashCode());
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationDefinitionIntegerVO[")
				.append("super").append(super.toString())
				.append("minimum=").append(minimum).append(",")
				.append("maximum=").append(maximum)
				.append("]")
				.toString();
	}

	// fluent

	public ConfigurationDefinitionIntegerVO minimum(java.lang.Integer newMinimum) {
		this.minimum = newMinimum;
		return this;
	}

	public ConfigurationDefinitionIntegerVO maximum(java.lang.Integer newMaximum) {
		this.maximum = newMaximum;
		return this;
	}

	// getter/setter

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
