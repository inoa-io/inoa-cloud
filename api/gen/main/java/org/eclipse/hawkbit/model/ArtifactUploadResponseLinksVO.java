package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ArtifactUploadResponseLinksVO {

	public static final java.lang.String JSON_PROPERTY_SELF = "self";
	public static final java.lang.String JSON_PROPERTY_DOWNLOAD = "download";

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_SELF)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private ArtifactUploadResponseLinksSelfVO self;

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DOWNLOAD)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private ArtifactUploadResponseLinksDownloadVO download;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ArtifactUploadResponseLinksVO other = (ArtifactUploadResponseLinksVO) object;
		return java.util.Objects.equals(self, other.self)
				&& java.util.Objects.equals(download, other.download);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(self, download);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ArtifactUploadResponseLinksVO[")
				.append("self=").append(self).append(",")
				.append("download=").append(download)
				.append("]")
				.toString();
	}

	// fluent

	public ArtifactUploadResponseLinksVO self(ArtifactUploadResponseLinksSelfVO newSelf) {
		this.self = newSelf;
		return this;
	}

	public ArtifactUploadResponseLinksVO download(ArtifactUploadResponseLinksDownloadVO newDownload) {
		this.download = newDownload;
		return this;
	}

	// getter/setter

	public ArtifactUploadResponseLinksSelfVO getSelf() {
		return self;
	}

	public void setSelf(ArtifactUploadResponseLinksSelfVO newSelf) {
		this.self = newSelf;
	}

	public ArtifactUploadResponseLinksDownloadVO getDownload() {
		return download;
	}

	public void setDownload(ArtifactUploadResponseLinksDownloadVO newDownload) {
		this.download = newDownload;
	}
}
