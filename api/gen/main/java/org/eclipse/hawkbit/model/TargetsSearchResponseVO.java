package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class TargetsSearchResponseVO {

	public static final java.lang.String JSON_PROPERTY_TOTAL = "total";
	public static final java.lang.String JSON_PROPERTY_SIZE = "size";
	public static final java.lang.String JSON_PROPERTY_CONTENT = "content";

	/** Total number of elements */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TOTAL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer total;

	/** Current page size */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_SIZE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer size;

	/** List of software modules */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONTENT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<TargetSearchResponseItemVO> content;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		TargetsSearchResponseVO other = (TargetsSearchResponseVO) object;
		return java.util.Objects.equals(total, other.total)
				&& java.util.Objects.equals(size, other.size)
				&& java.util.Objects.equals(content, other.content);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(total, size, content);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("TargetsSearchResponseVO[")
				.append("total=").append(total).append(",")
				.append("size=").append(size).append(",")
				.append("content=").append(content)
				.append("]")
				.toString();
	}

	// fluent

	public TargetsSearchResponseVO total(java.lang.Integer newTotal) {
		this.total = newTotal;
		return this;
	}

	public TargetsSearchResponseVO size(java.lang.Integer newSize) {
		this.size = newSize;
		return this;
	}

	public TargetsSearchResponseVO content(java.util.List<TargetSearchResponseItemVO> newContent) {
		this.content = newContent;
		return this;
	}
	
	public TargetsSearchResponseVO addContentItem(TargetSearchResponseItemVO contentItem) {
		if (this.content == null) {
			this.content = new java.util.ArrayList<>();
		}
		this.content.add(contentItem);
		return this;
	}

	public TargetsSearchResponseVO removeContentItem(TargetSearchResponseItemVO contentItem) {
		if (this.content != null) {
			this.content.remove(contentItem);
		}
		return this;
	}

	// getter/setter

	public java.lang.Integer getTotal() {
		return total;
	}

	public void setTotal(java.lang.Integer newTotal) {
		this.total = newTotal;
	}

	public java.lang.Integer getSize() {
		return size;
	}

	public void setSize(java.lang.Integer newSize) {
		this.size = newSize;
	}

	public java.util.List<TargetSearchResponseItemVO> getContent() {
		return content;
	}

	public void setContent(java.util.List<TargetSearchResponseItemVO> newContent) {
		this.content = newContent;
	}
}
