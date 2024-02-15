package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class GroupCreateVO {

	public static final java.lang.String JSON_PROPERTY_GROUP_ID = "group_id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";

	/** Id as technical reference (never changes). */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GROUP_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.UUID groupId;

	/** Name of a group. */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-]*$")
	@javax.validation.constraints.Size(min = 3, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
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
		GroupCreateVO other = (GroupCreateVO) object;
		return java.util.Objects.equals(groupId, other.groupId)
				&& java.util.Objects.equals(name, other.name);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(groupId, name);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GroupCreateVO[")
				.append("groupId=").append(groupId).append(",")
				.append("name=").append(name)
				.append("]")
				.toString();
	}

	// fluent

	public GroupCreateVO groupId(java.util.UUID newGroupId) {
		this.groupId = newGroupId;
		return this;
	}

	public GroupCreateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	// getter/setter

	public java.util.UUID getGroupId() {
		return groupId;
	}

	public void setGroupId(java.util.UUID newGroupId) {
		this.groupId = newGroupId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}
}
