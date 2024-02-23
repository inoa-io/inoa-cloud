package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class CredentialUpdateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";

	/** Name to identify credential. */
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-]*$")
	@jakarta.validation.constraints.Size(min = 3, max = 32)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** Flag if enabled or not. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean enabled = true;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		CredentialUpdateVO other = (CredentialUpdateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, enabled);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("CredentialUpdateVO[")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled)
				.append("]")
				.toString();
	}

	// fluent

	public CredentialUpdateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public CredentialUpdateVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
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
}
