package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class PropertyVO {

	public static final java.lang.String JSON_PROPERTY_KEY = "key";
	public static final java.lang.String JSON_PROPERTY_VALUE = "value";

	/** Name. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String key;

	/** value for the property */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALUE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
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
		PropertyVO other = (PropertyVO) object;
		return java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(key, value);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("PropertyVO[")
				.append("key=").append(key).append(",")
				.append("value=").append(value)
				.append("]")
				.toString();
	}

	// fluent

	public PropertyVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public PropertyVO value(java.lang.Object newValue) {
		this.value = newValue;
		return this;
	}

	// getter/setter

	public java.lang.String getKey() {
		return key;
	}

	public void setKey(java.lang.String newKey) {
		this.key = newKey;
	}

	public java.lang.Object getValue() {
		return value;
	}

	public void setValue(java.lang.Object newValue) {
		this.value = newValue;
	}
}
