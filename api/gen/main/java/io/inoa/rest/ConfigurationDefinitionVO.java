package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = ConfigurationDefinitionVO.JSON_DISCRIMINATOR)
@com.fasterxml.jackson.annotation.JsonSubTypes({
	@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConfigurationDefinitionBooleanVO.class, name = "BOOLEAN"),
	@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConfigurationDefinitionIntegerVO.class, name = "INTEGER"),
	@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConfigurationDefinitionStringVO.class, name = "STRING"),
	@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConfigurationDefinitionUrlVO.class, name = "URL")
})
@io.micronaut.core.annotation.Introspected
public abstract class ConfigurationDefinitionVO {

	public static final java.lang.String JSON_PROPERTY_KEY = "key";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_DISCRIMINATOR = "type";

	/** Key for configuration. */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\.\\_]*$")
	@javax.validation.constraints.Size(min = 3, max = 48)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String key;

	/** Describes a configuration. */
	@javax.validation.constraints.Size(max = 200)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_DISCRIMINATOR)
	public abstract ConfigurationTypeVO getType();

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ConfigurationDefinitionVO other = (ConfigurationDefinitionVO) object;
		return java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(getType(), other.getType());
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(key, description, getType());
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationDefinitionVO[")
				.append("key=").append(key).append(",")
				.append("description=").append(description)
				.append("]")
				.toString();
	}

	// fluent

	public ConfigurationDefinitionVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public ConfigurationDefinitionVO description(java.lang.String newDescription) {
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
