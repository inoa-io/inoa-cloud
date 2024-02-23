package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class GatewayPageVO {

	public static final java.lang.String JSON_PROPERTY_CONTENT = "content";
	public static final java.lang.String JSON_PROPERTY_TOTAL_SIZE = "total_size";

	/** List of entries on page. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONTENT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid GatewayVO> content = new java.util.ArrayList<>();

	/** Total available entries. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TOTAL_SIZE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Integer totalSize;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GatewayPageVO other = (GatewayPageVO) object;
		return java.util.Objects.equals(content, other.content)
				&& java.util.Objects.equals(totalSize, other.totalSize);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(content, totalSize);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GatewayPageVO[")
				.append("content=").append(content).append(",")
				.append("totalSize=").append(totalSize)
				.append("]")
				.toString();
	}

	// fluent

	public GatewayPageVO content(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid GatewayVO> newContent) {
		this.content = newContent;
		return this;
	}
	
	public GatewayPageVO addContentItem(GatewayVO contentItem) {
		if (this.content == null) {
			this.content = new java.util.ArrayList<>();
		}
		this.content.add(contentItem);
		return this;
	}

	public GatewayPageVO removeContentItem(GatewayVO contentItem) {
		if (this.content != null) {
			this.content.remove(contentItem);
		}
		return this;
	}

	public GatewayPageVO totalSize(java.lang.Integer newTotalSize) {
		this.totalSize = newTotalSize;
		return this;
	}

	// getter/setter

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid GatewayVO> getContent() {
		return content;
	}

	public void setContent(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid GatewayVO> newContent) {
		this.content = newContent;
	}

	public java.lang.Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(java.lang.Integer newTotalSize) {
		this.totalSize = newTotalSize;
	}
}
