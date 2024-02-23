package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class SoftwaremoduleCreatetionResponsePartVO {

	/** The technical identifier of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("id")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer id;

	/** The name of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("name")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** The description of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("description")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** Package version */
	@com.fasterxml.jackson.annotation.JsonProperty("version")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String version;

	/** The software vendor */
	@com.fasterxml.jackson.annotation.JsonProperty("vendor")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String vendor;

	/** The type of the software module identified by its key */
	@com.fasterxml.jackson.annotation.JsonProperty("type")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String type;

	/** Deleted flag, used for soft deleted entities */
	@com.fasterxml.jackson.annotation.JsonProperty("deleted")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean deleted;

	/** Entity was originally created by User, AMQP-Controller, anonymous etc.) */
	@com.fasterxml.jackson.annotation.JsonProperty("createdBy")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String createdBy;

	/** Entity was originally created at (timestamp UTC in milliseconds) */
	@com.fasterxml.jackson.annotation.JsonProperty("createdAt")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer createdAt;

	/** Entity was last modified by User, AMQP-Controller, anonymous etc.) */
	@com.fasterxml.jackson.annotation.JsonProperty("lastModifiedBy")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String lastModifiedBy;

	/** Entity was last modified at (timestamp UTC in milliseconds) */
	@com.fasterxml.jackson.annotation.JsonProperty("lastModifiedAt")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer lastModifiedAt;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		SoftwaremoduleCreatetionResponsePartVO other = (SoftwaremoduleCreatetionResponsePartVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(version, other.version)
				&& java.util.Objects.equals(vendor, other.vendor)
				&& java.util.Objects.equals(type, other.type)
				&& java.util.Objects.equals(deleted, other.deleted)
				&& java.util.Objects.equals(createdBy, other.createdBy)
				&& java.util.Objects.equals(createdAt, other.createdAt)
				&& java.util.Objects.equals(lastModifiedBy, other.lastModifiedBy)
				&& java.util.Objects.equals(lastModifiedAt, other.lastModifiedAt);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name, description, version, vendor, type, deleted, createdBy, createdAt, lastModifiedBy, lastModifiedAt);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("SoftwaremoduleCreatetionResponsePartVO[")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("version=").append(version).append(",")
				.append("vendor=").append(vendor).append(",")
				.append("type=").append(type).append(",")
				.append("deleted=").append(deleted).append(",")
				.append("createdBy=").append(createdBy).append(",")
				.append("createdAt=").append(createdAt).append(",")
				.append("lastModifiedBy=").append(lastModifiedBy).append(",")
				.append("lastModifiedAt=").append(lastModifiedAt)
				.append("]")
				.toString();
	}

	// fluent

	public SoftwaremoduleCreatetionResponsePartVO id(java.lang.Integer newId) {
		this.id = newId;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO version(java.lang.String newVersion) {
		this.version = newVersion;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO vendor(java.lang.String newVendor) {
		this.vendor = newVendor;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO type(java.lang.String newType) {
		this.type = newType;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO deleted(java.lang.Boolean newDeleted) {
		this.deleted = newDeleted;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO createdBy(java.lang.String newCreatedBy) {
		this.createdBy = newCreatedBy;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO createdAt(java.lang.Integer newCreatedAt) {
		this.createdAt = newCreatedAt;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO lastModifiedBy(java.lang.String newLastModifiedBy) {
		this.lastModifiedBy = newLastModifiedBy;
		return this;
	}

	public SoftwaremoduleCreatetionResponsePartVO lastModifiedAt(java.lang.Integer newLastModifiedAt) {
		this.lastModifiedAt = newLastModifiedAt;
		return this;
	}

	// getter/setter

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer newId) {
		this.id = newId;
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

	public java.lang.String getVersion() {
		return version;
	}

	public void setVersion(java.lang.String newVersion) {
		this.version = newVersion;
	}

	public java.lang.String getVendor() {
		return vendor;
	}

	public void setVendor(java.lang.String newVendor) {
		this.vendor = newVendor;
	}

	public java.lang.String getType() {
		return type;
	}

	public void setType(java.lang.String newType) {
		this.type = newType;
	}

	public java.lang.Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(java.lang.Boolean newDeleted) {
		this.deleted = newDeleted;
	}

	public java.lang.String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(java.lang.String newCreatedBy) {
		this.createdBy = newCreatedBy;
	}

	public java.lang.Integer getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.lang.Integer newCreatedAt) {
		this.createdAt = newCreatedAt;
	}

	public java.lang.String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(java.lang.String newLastModifiedBy) {
		this.lastModifiedBy = newLastModifiedBy;
	}

	public java.lang.Integer getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(java.lang.Integer newLastModifiedAt) {
		this.lastModifiedAt = newLastModifiedAt;
	}
}
