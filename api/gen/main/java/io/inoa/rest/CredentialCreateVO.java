package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class CredentialCreateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_TYPE = "type";
	public static final java.lang.String JSON_PROPERTY_VALUE = "value";

	/** Name to identify credential. */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-]*$")
	@javax.validation.constraints.Size(min = 3, max = 32)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Flag if enabled or not. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean enabled = true;

	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private CredentialTypeVO type;

	/** Value for credential. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALUE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private byte[] value;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		CredentialCreateVO other = (CredentialCreateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(type, other.type)
				&& java.util.Arrays.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, enabled, type, java.util.Arrays.hashCode(value));
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("CredentialCreateVO[")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("type=").append(type).append(",")
				.append("value=").append(value)
				.append("]")
				.toString();
	}

	// fluent

	public CredentialCreateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public CredentialCreateVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public CredentialCreateVO type(CredentialTypeVO newType) {
		this.type = newType;
		return this;
	}

	public CredentialCreateVO value(byte[] newValue) {
		this.value = newValue;
		return this;
	}

	// getter/setter

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
	}

	public CredentialTypeVO getType() {
		return type;
	}

	public void setType(CredentialTypeVO newType) {
		this.type = newType;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] newValue) {
		this.value = newValue;
	}
}
