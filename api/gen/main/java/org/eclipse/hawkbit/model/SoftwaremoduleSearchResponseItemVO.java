package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class SoftwaremoduleSearchResponseItemVO {

	/** The technical identifier of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("id")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Double id;

	/** The name of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("name")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** The software vendor */
	@com.fasterxml.jackson.annotation.JsonProperty("vendor")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String vendor;

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
	@com.fasterxml.jackson.annotation.JsonProperty("type")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String type;

	/** Package version */
	@com.fasterxml.jackson.annotation.JsonProperty("version")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String version;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		SoftwaremoduleSearchResponseItemVO other = (SoftwaremoduleSearchResponseItemVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(vendor, other.vendor)
				&& java.util.Objects.equals(createdBy, other.createdBy)
				&& java.util.Objects.equals(createdAt, other.createdAt)
				&& java.util.Objects.equals(lastModifiedBy, other.lastModifiedBy)
				&& java.util.Objects.equals(type, other.type)
				&& java.util.Objects.equals(version, other.version);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name, vendor, createdBy, createdAt, lastModifiedBy, type, version);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("SoftwaremoduleSearchResponseItemVO[")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("vendor=").append(vendor).append(",")
				.append("createdBy=").append(createdBy).append(",")
				.append("createdAt=").append(createdAt).append(",")
				.append("lastModifiedBy=").append(lastModifiedBy).append(",")
				.append("type=").append(type).append(",")
				.append("version=").append(version)
				.append("]")
				.toString();
	}

	// fluent

	public SoftwaremoduleSearchResponseItemVO id(java.lang.Double newId) {
		this.id = newId;
		return this;
	}

	public SoftwaremoduleSearchResponseItemVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public SoftwaremoduleSearchResponseItemVO vendor(java.lang.String newVendor) {
		this.vendor = newVendor;
		return this;
	}

	public SoftwaremoduleSearchResponseItemVO createdBy(java.lang.String newCreatedBy) {
		this.createdBy = newCreatedBy;
		return this;
	}

	public SoftwaremoduleSearchResponseItemVO createdAt(java.lang.Double newCreatedAt) {
		this.createdAt = newCreatedAt;
		return this;
	}

	public SoftwaremoduleSearchResponseItemVO lastModifiedBy(java.lang.String newLastModifiedBy) {
		this.lastModifiedBy = newLastModifiedBy;
		return this;
	}

	public SoftwaremoduleSearchResponseItemVO type(java.lang.String newType) {
		this.type = newType;
		return this;
	}

	public SoftwaremoduleSearchResponseItemVO version(java.lang.String newVersion) {
		this.version = newVersion;
		return this;
	}

	// getter/setter

	public java.lang.Double getId() {
		return id;
	}

	public void setId(java.lang.Double newId) {
		this.id = newId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getVendor() {
		return vendor;
	}

	public void setVendor(java.lang.String newVendor) {
		this.vendor = newVendor;
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

	public java.lang.String getType() {
		return type;
	}

	public void setType(java.lang.String newType) {
		this.type = newType;
	}

	public java.lang.String getVersion() {
		return version;
	}

	public void setVersion(java.lang.String newVersion) {
		this.version = newVersion;
	}
}
