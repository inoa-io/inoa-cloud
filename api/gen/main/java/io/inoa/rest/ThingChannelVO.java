package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ThingChannelVO {

	public static final java.lang.String JSON_PROPERTY_KEY = "key";
	public static final java.lang.String JSON_PROPERTY_PROPERTIES = "properties";

	/** key for the channel */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String key;

	@javax.validation.Valid
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PROPERTIES)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<PropertyVO> properties;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingChannelVO other = (ThingChannelVO) object;
		return java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(properties, other.properties);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(key, properties);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingChannelVO[")
				.append("key=").append(key).append(",")
				.append("properties=").append(properties)
				.append("]")
				.toString();
	}

	// fluent

	public ThingChannelVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public ThingChannelVO properties(java.util.List<PropertyVO> newProperties) {
		this.properties = newProperties;
		return this;
	}
	
	public ThingChannelVO addPropertiesItem(PropertyVO propertiesItem) {
		if (this.properties == null) {
			this.properties = new java.util.ArrayList<>();
		}
		this.properties.add(propertiesItem);
		return this;
	}

	public ThingChannelVO removePropertiesItem(PropertyVO propertiesItem) {
		if (this.properties != null) {
			this.properties.remove(propertiesItem);
		}
		return this;
	}

	// getter/setter

	public java.lang.String getKey() {
		return key;
	}

	public void setKey(java.lang.String newKey) {
		this.key = newKey;
	}

	public java.util.List<PropertyVO> getProperties() {
		return properties;
	}

	public void setProperties(java.util.List<PropertyVO> newProperties) {
		this.properties = newProperties;
	}
}
