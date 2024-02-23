package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class CredentialVO {

	public static final java.lang.String JSON_PROPERTY_CREDENTIAL_ID = "credential_id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_TYPE = "type";
	public static final java.lang.String JSON_PROPERTY_VALUE = "value";
	public static final java.lang.String JSON_PROPERTY_CREATED = "created";
	public static final java.lang.String JSON_PROPERTY_UPDATED = "updated";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREDENTIAL_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.util.UUID credentialId;

	/** Name to identify credential. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-]*$")
	@jakarta.validation.constraints.Size(min = 3, max = 32)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Flag if enabled or not. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Boolean enabled = true;

	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private CredentialTypeVO type;

	/** Value for credential. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALUE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private byte[] value;

	/** Common timestamp for created/updated timestamps. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant created;

	/** Common timestamp for created/updated timestamps. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_UPDATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant updated;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		CredentialVO other = (CredentialVO) object;
		return java.util.Objects.equals(credentialId, other.credentialId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(type, other.type)
				&& java.util.Arrays.equals(value, other.value)
				&& java.util.Objects.equals(created, other.created)
				&& java.util.Objects.equals(updated, other.updated);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(credentialId, name, enabled, type, java.util.Arrays.hashCode(value), created, updated);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("CredentialVO[")
				.append("credentialId=").append(credentialId).append(",")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("type=").append(type).append(",")
				.append("value=").append(value).append(",")
				.append("created=").append(created).append(",")
				.append("updated=").append(updated)
				.append("]")
				.toString();
	}

	// fluent

	public CredentialVO credentialId(java.util.UUID newCredentialId) {
		this.credentialId = newCredentialId;
		return this;
	}

	public CredentialVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public CredentialVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public CredentialVO type(CredentialTypeVO newType) {
		this.type = newType;
		return this;
	}

	public CredentialVO value(byte[] newValue) {
		this.value = newValue;
		return this;
	}

	public CredentialVO created(java.time.Instant newCreated) {
		this.created = newCreated;
		return this;
	}

	public CredentialVO updated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
		return this;
	}

	// getter/setter

	public java.util.UUID getCredentialId() {
		return credentialId;
	}

	public void setCredentialId(java.util.UUID newCredentialId) {
		this.credentialId = newCredentialId;
	}

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

	public java.time.Instant getCreated() {
		return created;
	}

	public void setCreated(java.time.Instant newCreated) {
		this.created = newCreated;
	}

	public java.time.Instant getUpdated() {
		return updated;
	}

	public void setUpdated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
	}
}
