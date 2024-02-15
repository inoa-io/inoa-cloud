package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class GroupUpdateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";

	/** Name of a group. */
	@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-]*$")
	@javax.validation.constraints.Size(min = 3, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GroupUpdateVO other = (GroupUpdateVO) object;
		return java.util.Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GroupUpdateVO[")
				.append("name=").append(name)
				.append("]")
				.toString();
	}

	// fluent

	public GroupUpdateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	// getter/setter

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}
}
