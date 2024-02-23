package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ArtifactUploadResponseHashesVO {

	/** MD5 hash of the artifact */
	@com.fasterxml.jackson.annotation.JsonProperty("md5")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String md5;

	/** SHA1 hash of the artifact */
	@com.fasterxml.jackson.annotation.JsonProperty("sha1")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String sha1;

	/** SHA256 hash of the artifact */
	@com.fasterxml.jackson.annotation.JsonProperty("sha256")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String sha256;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ArtifactUploadResponseHashesVO other = (ArtifactUploadResponseHashesVO) object;
		return java.util.Objects.equals(md5, other.md5)
				&& java.util.Objects.equals(sha1, other.sha1)
				&& java.util.Objects.equals(sha256, other.sha256);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(md5, sha1, sha256);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ArtifactUploadResponseHashesVO[")
				.append("md5=").append(md5).append(",")
				.append("sha1=").append(sha1).append(",")
				.append("sha256=").append(sha256)
				.append("]")
				.toString();
	}

	// fluent

	public ArtifactUploadResponseHashesVO md5(java.lang.String newMd5) {
		this.md5 = newMd5;
		return this;
	}

	public ArtifactUploadResponseHashesVO sha1(java.lang.String newSha1) {
		this.sha1 = newSha1;
		return this;
	}

	public ArtifactUploadResponseHashesVO sha256(java.lang.String newSha256) {
		this.sha256 = newSha256;
		return this;
	}

	// getter/setter

	public java.lang.String getMd5() {
		return md5;
	}

	public void setMd5(java.lang.String newMd5) {
		this.md5 = newMd5;
	}

	public java.lang.String getSha1() {
		return sha1;
	}

	public void setSha1(java.lang.String newSha1) {
		this.sha1 = newSha1;
	}

	public java.lang.String getSha256() {
		return sha256;
	}

	public void setSha256(java.lang.String newSha256) {
		this.sha256 = newSha256;
	}
}
