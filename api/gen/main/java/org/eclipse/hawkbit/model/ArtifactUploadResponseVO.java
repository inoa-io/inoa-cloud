package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ArtifactUploadResponseVO {

	/** Size of the artifact */
	@com.fasterxml.jackson.annotation.JsonProperty("size")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer size;

	/** The technical identifier of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty("id")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer id;

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

	@com.fasterxml.jackson.annotation.JsonProperty("_links")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private ArtifactUploadResponseLinksVO links;

	@com.fasterxml.jackson.annotation.JsonProperty("hashes")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private ArtifactUploadResponseHashesVO hashes;

	/** Filename of the artifact */
	@com.fasterxml.jackson.annotation.JsonProperty("providedFilename")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String providedFilename;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ArtifactUploadResponseVO other = (ArtifactUploadResponseVO) object;
		return java.util.Objects.equals(size, other.size)
				&& java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(createdBy, other.createdBy)
				&& java.util.Objects.equals(createdAt, other.createdAt)
				&& java.util.Objects.equals(lastModifiedBy, other.lastModifiedBy)
				&& java.util.Objects.equals(lastModifiedAt, other.lastModifiedAt)
				&& java.util.Objects.equals(links, other.links)
				&& java.util.Objects.equals(hashes, other.hashes)
				&& java.util.Objects.equals(providedFilename, other.providedFilename);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(size, id, createdBy, createdAt, lastModifiedBy, lastModifiedAt, links, hashes, providedFilename);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ArtifactUploadResponseVO[")
				.append("size=").append(size).append(",")
				.append("id=").append(id).append(",")
				.append("createdBy=").append(createdBy).append(",")
				.append("createdAt=").append(createdAt).append(",")
				.append("lastModifiedBy=").append(lastModifiedBy).append(",")
				.append("lastModifiedAt=").append(lastModifiedAt).append(",")
				.append("links=").append(links).append(",")
				.append("hashes=").append(hashes).append(",")
				.append("providedFilename=").append(providedFilename)
				.append("]")
				.toString();
	}

	// fluent

	public ArtifactUploadResponseVO size(java.lang.Integer newSize) {
		this.size = newSize;
		return this;
	}

	public ArtifactUploadResponseVO id(java.lang.Integer newId) {
		this.id = newId;
		return this;
	}

	public ArtifactUploadResponseVO createdBy(java.lang.String newCreatedBy) {
		this.createdBy = newCreatedBy;
		return this;
	}

	public ArtifactUploadResponseVO createdAt(java.lang.Integer newCreatedAt) {
		this.createdAt = newCreatedAt;
		return this;
	}

	public ArtifactUploadResponseVO lastModifiedBy(java.lang.String newLastModifiedBy) {
		this.lastModifiedBy = newLastModifiedBy;
		return this;
	}

	public ArtifactUploadResponseVO lastModifiedAt(java.lang.Integer newLastModifiedAt) {
		this.lastModifiedAt = newLastModifiedAt;
		return this;
	}

	public ArtifactUploadResponseVO links(ArtifactUploadResponseLinksVO newLinks) {
		this.links = newLinks;
		return this;
	}

	public ArtifactUploadResponseVO hashes(ArtifactUploadResponseHashesVO newHashes) {
		this.hashes = newHashes;
		return this;
	}

	public ArtifactUploadResponseVO providedFilename(java.lang.String newProvidedFilename) {
		this.providedFilename = newProvidedFilename;
		return this;
	}

	// getter/setter

	public java.lang.Integer getSize() {
		return size;
	}

	public void setSize(java.lang.Integer newSize) {
		this.size = newSize;
	}

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer newId) {
		this.id = newId;
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

	public ArtifactUploadResponseLinksVO getLinks() {
		return links;
	}

	public void setLinks(ArtifactUploadResponseLinksVO newLinks) {
		this.links = newLinks;
	}

	public ArtifactUploadResponseHashesVO getHashes() {
		return hashes;
	}

	public void setHashes(ArtifactUploadResponseHashesVO newHashes) {
		this.hashes = newHashes;
	}

	public java.lang.String getProvidedFilename() {
		return providedFilename;
	}

	public void setProvidedFilename(java.lang.String newProvidedFilename) {
		this.providedFilename = newProvidedFilename;
	}
}
