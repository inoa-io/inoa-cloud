package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class GroupVO {

	public static final java.lang.String JSON_PROPERTY_GROUP_ID = "group_id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_CREATED = "created";
	public static final java.lang.String JSON_PROPERTY_UPDATED = "updated";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GROUP_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.util.UUID groupId;

	/** Name of a group. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-]*$")
	@jakarta.validation.constraints.Size(min = 3, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Common timestamp for created/updated timestamps. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant created;

	/** Common timestamp for created/updated timestamps. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_UPDATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant updated;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GroupVO other = (GroupVO) object;
		return java.util.Objects.equals(groupId, other.groupId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(created, other.created)
				&& java.util.Objects.equals(updated, other.updated);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(groupId, name, created, updated);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GroupVO[")
				.append("groupId=").append(groupId).append(",")
				.append("name=").append(name).append(",")
				.append("created=").append(created).append(",")
				.append("updated=").append(updated)
				.append("]")
				.toString();
	}

	// fluent

	public GroupVO groupId(java.util.UUID newGroupId) {
		this.groupId = newGroupId;
		return this;
	}

	public GroupVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public GroupVO created(java.time.Instant newCreated) {
		this.created = newCreated;
		return this;
	}

	public GroupVO updated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
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

	public java.time.Instant getCreated() {
		return created;
	}

	public void setCreated(java.time.Instant newCreated) {
		this.created = newCreated;
	}

	public java.time.Instant getUpdated() {
		return updated;
	}

	public void setUpdated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
	}
}
