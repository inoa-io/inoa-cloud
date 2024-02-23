package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class RegisterVO {

	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_CREDENTIAL_TYPE = "credential_type";
	public static final java.lang.String JSON_PROPERTY_CREDENTIAL_VALUE = "credential_value";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
	@jakarta.validation.constraints.Size(min = 4, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String gatewayId;

	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREDENTIAL_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private CredentialTypeVO credentialType;

	/** Value for credential. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREDENTIAL_VALUE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private byte[] credentialValue;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		RegisterVO other = (RegisterVO) object;
		return java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(credentialType, other.credentialType)
				&& java.util.Arrays.equals(credentialValue, other.credentialValue);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(gatewayId, credentialType, java.util.Arrays.hashCode(credentialValue));
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("RegisterVO[")
				.append("gatewayId=").append(gatewayId).append(",")
				.append("credentialType=").append(credentialType).append(",")
				.append("credentialValue=").append(credentialValue)
				.append("]")
				.toString();
	}

	// fluent

	public RegisterVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public RegisterVO credentialType(CredentialTypeVO newCredentialType) {
		this.credentialType = newCredentialType;
		return this;
	}

	public RegisterVO credentialValue(byte[] newCredentialValue) {
		this.credentialValue = newCredentialValue;
		return this;
	}

	// getter/setter

	public java.lang.String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
	}

	public CredentialTypeVO getCredentialType() {
		return credentialType;
	}

	public void setCredentialType(CredentialTypeVO newCredentialType) {
		this.credentialType = newCredentialType;
	}

	public byte[] getCredentialValue() {
		return credentialValue;
	}

	public void setCredentialValue(byte[] newCredentialValue) {
		this.credentialValue = newCredentialValue;
	}
}
