package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingConfigurationsInnerVO {

	public static final java.lang.String JSON_PROPERTY_CONFIGURATION = "configuration";
	public static final java.lang.String JSON_PROPERTY_VALUE = "value";

	@jakarta.validation.Valid
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONFIGURATION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private ThingConfigurationVO _configuration;

	/** Value of the configuration */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 4096)
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
		ThingConfigurationsInnerVO other = (ThingConfigurationsInnerVO) object;
		return java.util.Objects.equals(_configuration, other._configuration)
				&& java.util.Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(_configuration, value);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingConfigurationsInnerVO[")
				.append("_configuration=").append(_configuration).append(",")
				.append("value=").append(value)
				.append("]")
				.toString();
	}

	// fluent

	public ThingConfigurationsInnerVO _configuration(ThingConfigurationVO newConfiguration) {
		this._configuration = newConfiguration;
		return this;
	}

	public ThingConfigurationsInnerVO value(java.lang.String newValue) {
		this.value = newValue;
		return this;
	}

	// getter/setter

	public ThingConfigurationVO getConfiguration() {
		return _configuration;
	}

	public void setConfiguration(ThingConfigurationVO newConfiguration) {
		this._configuration = newConfiguration;
	}

	public java.lang.String getValue() {
		return value;
	}

	public void setValue(java.lang.String newValue) {
		this.value = newValue;
	}
}
