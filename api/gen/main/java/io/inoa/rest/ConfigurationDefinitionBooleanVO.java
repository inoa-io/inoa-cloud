package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public final class ConfigurationDefinitionBooleanVO implements ConfigurationDefinitionVO {

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

	@Override
	public ConfigurationTypeVO getType() {
		return ConfigurationTypeVO.BOOLEAN;
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
		ConfigurationDefinitionBooleanVO other = (ConfigurationDefinitionBooleanVO) object;
		return java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(description, other.description);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(key, description);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationDefinitionBooleanVO[")
				.append("key=").append(key).append(",")
				.append("description=").append(description)
				.append("]")
				.toString();
	}

	// fluent

	public ConfigurationDefinitionBooleanVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public ConfigurationDefinitionBooleanVO description(java.lang.String newDescription) {
		this.description = newDescription;
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
}
