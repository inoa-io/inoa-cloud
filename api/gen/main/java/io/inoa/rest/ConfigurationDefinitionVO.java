package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include = com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY, property = ConfigurationDefinitionVO.JSON_DISCRIMINATOR)
@com.fasterxml.jackson.annotation.JsonSubTypes({
	@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConfigurationDefinitionBooleanVO.class, name = "BOOLEAN"),
	@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConfigurationDefinitionIntegerVO.class, name = "INTEGER"),
	@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConfigurationDefinitionStringVO.class, name = "STRING"),
	@com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = ConfigurationDefinitionUrlVO.class, name = "URL")
})
public sealed interface ConfigurationDefinitionVO permits ConfigurationDefinitionBooleanVO, ConfigurationDefinitionIntegerVO, ConfigurationDefinitionStringVO, ConfigurationDefinitionUrlVO {

	java.lang.String JSON_DISCRIMINATOR = "type";
	java.lang.String JSON_PROPERTY_KEY = "key";
	java.lang.String JSON_PROPERTY_DESCRIPTION = "description";

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_DISCRIMINATOR)
	ConfigurationTypeVO getType();

	/** Key for configuration. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	java.lang.String getKey();

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	void setKey(java.lang.String newKey);

	/** Describes a configuration. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	java.lang.String getDescription();

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	void setDescription(java.lang.String newDescription);
}
