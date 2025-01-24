package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class TargetSearchResponseItemVO {

	/** The technical identifier of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("controllerId")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String controllerId;

	/** The name of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("name")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** The description of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("description")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** Pre-Shared key that allows targets to authenticate at Direct Device Integration API if enabled in the tenant settings. */
	@com.fasterxml.jackson.annotation.JsonProperty("securityToken")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String securityToken;

	@com.fasterxml.jackson.annotation.JsonProperty("updateStatus")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private UpdateStatus updateStatus;

	/** The last known address URI of the target. Includes information of the target is connected either directly (DDI) through HTTP or indirectly (DMF) through amqp. */
	@com.fasterxml.jackson.annotation.JsonProperty("address")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String address;

	/** Last known IP address of the target. Only presented if IP address of the target itself is known (connected directly through DDI API). */
	@com.fasterxml.jackson.annotation.JsonProperty("ipAddress")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String ipAddress;

	/** Last time where the target polled the server, same as pollStatus.lastRequestAt. */
	@com.fasterxml.jackson.annotation.JsonProperty("lastControllerRequestAt")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Double lastControllerRequestAt;

	/** Request re-transmission of target attributes. */
	@com.fasterxml.jackson.annotation.JsonProperty("requestAttributes")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean requestAttributes;

	/** Present if user consent flow active. Indicates if auto-confirm is active. */
	@com.fasterxml.jackson.annotation.JsonProperty("autoConfirmActive")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean autoConfirmActive;

	/** Installation time of current installed DistributionSet. */
	@com.fasterxml.jackson.annotation.JsonProperty("installedAt")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Double installedAt;

	/** Entity was originally created by User, AMQP-Controller, anonymous etc.) */
	@com.fasterxml.jackson.annotation.JsonProperty("createdBy")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String createdBy;

	/** Entity was originally created at (timestamp UTC in milliseconds) */
	@com.fasterxml.jackson.annotation.JsonProperty("createdAt")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Double createdAt;

	/** Entity was last modified by User, AMQP-Controller, anonymous etc.) */
	@com.fasterxml.jackson.annotation.JsonProperty("lastModifiedBy")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String lastModifiedBy;

	/** The software module type of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("targetType")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String targetType;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		TargetSearchResponseItemVO other = (TargetSearchResponseItemVO) object;
		return java.util.Objects.equals(controllerId, other.controllerId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(securityToken, other.securityToken)
				&& java.util.Objects.equals(updateStatus, other.updateStatus)
				&& java.util.Objects.equals(address, other.address)
				&& java.util.Objects.equals(ipAddress, other.ipAddress)
				&& java.util.Objects.equals(lastControllerRequestAt, other.lastControllerRequestAt)
				&& java.util.Objects.equals(requestAttributes, other.requestAttributes)
				&& java.util.Objects.equals(autoConfirmActive, other.autoConfirmActive)
				&& java.util.Objects.equals(installedAt, other.installedAt)
				&& java.util.Objects.equals(createdBy, other.createdBy)
				&& java.util.Objects.equals(createdAt, other.createdAt)
				&& java.util.Objects.equals(lastModifiedBy, other.lastModifiedBy)
				&& java.util.Objects.equals(targetType, other.targetType);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(controllerId, name, description, securityToken, updateStatus, address, ipAddress, lastControllerRequestAt, requestAttributes, autoConfirmActive, installedAt, createdBy, createdAt, lastModifiedBy, targetType);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("TargetSearchResponseItemVO[")
				.append("controllerId=").append(controllerId).append(",")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("securityToken=").append(securityToken).append(",")
				.append("updateStatus=").append(updateStatus).append(",")
				.append("address=").append(address).append(",")
				.append("ipAddress=").append(ipAddress).append(",")
				.append("lastControllerRequestAt=").append(lastControllerRequestAt).append(",")
				.append("requestAttributes=").append(requestAttributes).append(",")
				.append("autoConfirmActive=").append(autoConfirmActive).append(",")
				.append("installedAt=").append(installedAt).append(",")
				.append("createdBy=").append(createdBy).append(",")
				.append("createdAt=").append(createdAt).append(",")
				.append("lastModifiedBy=").append(lastModifiedBy).append(",")
				.append("targetType=").append(targetType)
				.append("]")
				.toString();
	}

	// fluent

	public TargetSearchResponseItemVO controllerId(java.lang.String newControllerId) {
		this.controllerId = newControllerId;
		return this;
	}

	public TargetSearchResponseItemVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public TargetSearchResponseItemVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public TargetSearchResponseItemVO securityToken(java.lang.String newSecurityToken) {
		this.securityToken = newSecurityToken;
		return this;
	}

	public TargetSearchResponseItemVO updateStatus(UpdateStatus newUpdateStatus) {
		this.updateStatus = newUpdateStatus;
		return this;
	}

	public TargetSearchResponseItemVO address(java.lang.String newAddress) {
		this.address = newAddress;
		return this;
	}

	public TargetSearchResponseItemVO ipAddress(java.lang.String newIpAddress) {
		this.ipAddress = newIpAddress;
		return this;
	}

	public TargetSearchResponseItemVO lastControllerRequestAt(java.lang.Double newLastControllerRequestAt) {
		this.lastControllerRequestAt = newLastControllerRequestAt;
		return this;
	}

	public TargetSearchResponseItemVO requestAttributes(java.lang.Boolean newRequestAttributes) {
		this.requestAttributes = newRequestAttributes;
		return this;
	}

	public TargetSearchResponseItemVO autoConfirmActive(java.lang.Boolean newAutoConfirmActive) {
		this.autoConfirmActive = newAutoConfirmActive;
		return this;
	}

	public TargetSearchResponseItemVO installedAt(java.lang.Double newInstalledAt) {
		this.installedAt = newInstalledAt;
		return this;
	}

	public TargetSearchResponseItemVO createdBy(java.lang.String newCreatedBy) {
		this.createdBy = newCreatedBy;
		return this;
	}

	public TargetSearchResponseItemVO createdAt(java.lang.Double newCreatedAt) {
		this.createdAt = newCreatedAt;
		return this;
	}

	public TargetSearchResponseItemVO lastModifiedBy(java.lang.String newLastModifiedBy) {
		this.lastModifiedBy = newLastModifiedBy;
		return this;
	}

	public TargetSearchResponseItemVO targetType(java.lang.String newTargetType) {
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

	public java.lang.String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(java.lang.String newSecurityToken) {
		this.securityToken = newSecurityToken;
	}

	public UpdateStatus getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(UpdateStatus newUpdateStatus) {
		this.updateStatus = newUpdateStatus;
	}

	public java.lang.String getAddress() {
		return address;
	}

	public void setAddress(java.lang.String newAddress) {
		this.address = newAddress;
	}

	public java.lang.String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(java.lang.String newIpAddress) {
		this.ipAddress = newIpAddress;
	}

	public java.lang.Double getLastControllerRequestAt() {
		return lastControllerRequestAt;
	}

	public void setLastControllerRequestAt(java.lang.Double newLastControllerRequestAt) {
		this.lastControllerRequestAt = newLastControllerRequestAt;
	}

	public java.lang.Boolean getRequestAttributes() {
		return requestAttributes;
	}

	public void setRequestAttributes(java.lang.Boolean newRequestAttributes) {
		this.requestAttributes = newRequestAttributes;
	}

	public java.lang.Boolean getAutoConfirmActive() {
		return autoConfirmActive;
	}

	public void setAutoConfirmActive(java.lang.Boolean newAutoConfirmActive) {
		this.autoConfirmActive = newAutoConfirmActive;
	}

	public java.lang.Double getInstalledAt() {
		return installedAt;
	}

	public void setInstalledAt(java.lang.Double newInstalledAt) {
		this.installedAt = newInstalledAt;
	}

	public java.lang.String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(java.lang.String newCreatedBy) {
		this.createdBy = newCreatedBy;
	}

	public java.lang.Double getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.lang.Double newCreatedAt) {
		this.createdAt = newCreatedAt;
	}

	public java.lang.String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(java.lang.String newLastModifiedBy) {
		this.lastModifiedBy = newLastModifiedBy;
	}

	public java.lang.String getTargetType() {
		return targetType;
	}

	public void setTargetType(java.lang.String newTargetType) {
		this.targetType = newTargetType;
	}

@io.micronaut.serde.annotation.Serdeable
public enum UpdateStatus {

	ERROR("error"),
	IN_SYNC("in_sync"),
	PENDING("pending"),
	REGISTERED("registered"),
	UNKNOWN("unknown");

	public static final java.lang.String ERROR_VALUE = "error";
	public static final java.lang.String IN_SYNC_VALUE = "in_sync";
	public static final java.lang.String PENDING_VALUE = "pending";
	public static final java.lang.String REGISTERED_VALUE = "registered";
	public static final java.lang.String UNKNOWN_VALUE = "unknown";

	private final java.lang.String value;

	private UpdateStatus(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static UpdateStatus toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<UpdateStatus> toOptional(java.lang.String value) {
		return java.util.Arrays
				.stream(values())
				.filter(e -> e.value.equals(value))
				.findAny();
	}

	@com.fasterxml.jackson.annotation.JsonValue
	public java.lang.String getValue() {
		return value;
	}
}
}
