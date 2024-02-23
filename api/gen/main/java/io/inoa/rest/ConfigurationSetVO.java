package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ConfigurationSetVO {

	public static final java.lang.String JSON_PROPERTY_VALUE = "value";

	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALUE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Object value;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ConfigurationSetVO other = (ConfigurationSetVO) object;
		return java.util.Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(value);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationSetVO[")
				.append("value=").append(value)
				.append("]")
				.toString();
	}

	// fluent

	public ConfigurationSetVO value(java.lang.Object newValue) {
		this.value = newValue;
		return this;
	}

	// getter/setter

	public java.lang.Object getValue() {
		return value;
	}

	public void setValue(java.lang.Object newValue) {
		this.value = newValue;
	}
}
