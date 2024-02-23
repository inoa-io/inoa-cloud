package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ArtifactUploadResponseLinksDownloadVO {

	@com.fasterxml.jackson.annotation.JsonProperty("href")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String href;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ArtifactUploadResponseLinksDownloadVO other = (ArtifactUploadResponseLinksDownloadVO) object;
		return java.util.Objects.equals(href, other.href);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(href);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ArtifactUploadResponseLinksDownloadVO[")
				.append("href=").append(href)
				.append("]")
				.toString();
	}

	// fluent

	public ArtifactUploadResponseLinksDownloadVO href(java.lang.String newHref) {
		this.href = newHref;
		return this;
	}

	// getter/setter

	public java.lang.String getHref() {
		return href;
	}

	public void setHref(java.lang.String newHref) {
		this.href = newHref;
	}
}
