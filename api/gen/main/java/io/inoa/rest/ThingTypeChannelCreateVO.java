package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ThingTypeChannelCreateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_KEY = "key";
	public static final java.lang.String JSON_PROPERTY_PROPERTIES = "properties";

	/** Name. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** key for the channel type */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String key;

	@javax.validation.Valid
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PROPERTIES)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<PropertyDefinitionVO> properties;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingTypeChannelCreateVO other = (ThingTypeChannelCreateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(properties, other.properties);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, key, properties);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingTypeChannelCreateVO[")
				.append("name=").append(name).append(",")
				.append("key=").append(key).append(",")
				.append("properties=").append(properties)
				.append("]")
				.toString();
	}

	// fluent

	public ThingTypeChannelCreateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingTypeChannelCreateVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public ThingTypeChannelCreateVO properties(java.util.List<PropertyDefinitionVO> newProperties) {
		this.properties = newProperties;
		return this;
	}
	
	public ThingTypeChannelCreateVO addPropertiesItem(PropertyDefinitionVO propertiesItem) {
		if (this.properties == null) {
			this.properties = new java.util.ArrayList<>();
		}
		this.properties.add(propertiesItem);
		return this;
	}

	public ThingTypeChannelCreateVO removePropertiesItem(PropertyDefinitionVO propertiesItem) {
		if (this.properties != null) {
			this.properties.remove(propertiesItem);
		}
		return this;
	}

	// getter/setter

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getKey() {
		return key;
	}

	public void setKey(java.lang.String newKey) {
		this.key = newKey;
	}

	public java.util.List<PropertyDefinitionVO> getProperties() {
		return properties;
	}

	public void setProperties(java.util.List<PropertyDefinitionVO> newProperties) {
		this.properties = newProperties;
	}
}
