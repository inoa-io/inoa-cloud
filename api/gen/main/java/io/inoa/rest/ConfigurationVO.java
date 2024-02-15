package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ConfigurationVO {

	public static final java.lang.String JSON_PROPERTY_DEFINITION = "definition";
	public static final java.lang.String JSON_PROPERTY_VALUE = "value";

	@javax.validation.Valid
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DEFINITION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private ConfigurationDefinitionVO definition;

	@javax.validation.constraints.NotNull
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
		ConfigurationVO other = (ConfigurationVO) object;
		return java.util.Objects.equals(definition, other.definition)
				&& java.util.Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(definition, value);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationVO[")
				.append("definition=").append(definition).append(",")
				.append("value=").append(value)
				.append("]")
				.toString();
	}

	// fluent

	public ConfigurationVO definition(ConfigurationDefinitionVO newDefinition) {
		this.definition = newDefinition;
		return this;
	}

	public ConfigurationVO value(java.lang.Object newValue) {
		this.value = newValue;
		return this;
	}

	// getter/setter

	public ConfigurationDefinitionVO getDefinition() {
		return definition;
	}

	public void setDefinition(ConfigurationDefinitionVO newDefinition) {
		this.definition = newDefinition;
	}

	public java.lang.Object getValue() {
		return value;
	}

	public void setValue(java.lang.Object newValue) {
		this.value = newValue;
	}
}
