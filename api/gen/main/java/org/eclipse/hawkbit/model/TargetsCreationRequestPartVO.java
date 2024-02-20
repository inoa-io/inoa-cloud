package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class TargetsCreationRequestPartVO {

	public static final java.lang.String JSON_PROPERTY_CONTROLLER_ID = "controllerId";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_ADDRESS = "address";
	public static final java.lang.String JSON_PROPERTY_SECURITY_TOKEN = "securityToken";
	public static final java.lang.String JSON_PROPERTY_TARGET_TYPE = "targetType";

	/** The technical identifier of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONTROLLER_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String controllerId;

	/** The name of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** The description of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** The last known address URI of the target. Includes information of the target is connected either directly (DDI) through HTTP or indirectly (DMF) through amqp. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ADDRESS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String address;

	/** Pre-Shared key that allows targets to authenticate at Direct Device Integration API if enabled in the tenant settings. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_SECURITY_TOKEN)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String securityToken;

	/** ID of the target type */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TARGET_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Double targetType;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		TargetsCreationRequestPartVO other = (TargetsCreationRequestPartVO) object;
		return java.util.Objects.equals(controllerId, other.controllerId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(address, other.address)
				&& java.util.Objects.equals(securityToken, other.securityToken)
				&& java.util.Objects.equals(targetType, other.targetType);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(controllerId, name, description, address, securityToken, targetType);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("TargetsCreationRequestPartVO[")
				.append("controllerId=").append(controllerId).append(",")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("address=").append(address).append(",")
				.append("securityToken=").append(securityToken).append(",")
				.append("targetType=").append(targetType)
				.append("]")
				.toString();
	}

	// fluent

	public TargetsCreationRequestPartVO controllerId(java.lang.String newControllerId) {
		this.controllerId = newControllerId;
		return this;
	}

	public TargetsCreationRequestPartVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public TargetsCreationRequestPartVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public TargetsCreationRequestPartVO address(java.lang.String newAddress) {
		this.address = newAddress;
		return this;
	}

	public TargetsCreationRequestPartVO securityToken(java.lang.String newSecurityToken) {
		this.securityToken = newSecurityToken;
		return this;
	}

	public TargetsCreationRequestPartVO targetType(java.lang.Double newTargetType) {
		this.targetType = newTargetType;
		return this;
	}

	// getter/setter

	public java.lang.String getControllerId() {
		return controllerId;
	}

	public void setControllerId(java.lang.String newControllerId) {
		this.controllerId = newControllerId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String newDescription) {
		this.description = newDescription;
	}

	public java.lang.String getAddress() {
		return address;
	}

	public void setAddress(java.lang.String newAddress) {
		this.address = newAddress;
	}

	public java.lang.String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(java.lang.String newSecurityToken) {
		this.securityToken = newSecurityToken;
	}

	public java.lang.Double getTargetType() {
		return targetType;
	}

	public void setTargetType(java.lang.Double newTargetType) {
		this.targetType = newTargetType;
	}
}
