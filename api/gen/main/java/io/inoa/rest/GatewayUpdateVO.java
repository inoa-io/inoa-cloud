package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class GatewayUpdateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_GROUP_IDS = "group_ids";

	/** Human friendly description (can change). */
	@javax.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** Flag if enabled or not. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean enabled = true;

	/** Ids of groups where gateway is member. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GROUP_IDS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Set<java.util.UUID> groupIds;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GatewayUpdateVO other = (GatewayUpdateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(groupIds, other.groupIds);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, enabled, groupIds);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GatewayUpdateVO[")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("groupIds=").append(groupIds)
				.append("]")
				.toString();
	}

	// fluent

	public GatewayUpdateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public GatewayUpdateVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public GatewayUpdateVO groupIds(java.util.Set<java.util.UUID> newGroupIds) {
		this.groupIds = newGroupIds;
		return this;
	}
	
	public GatewayUpdateVO addGroupIdsItem(java.util.UUID groupIdsItem) {
		if (this.groupIds == null) {
			this.groupIds = new java.util.LinkedHashSet<>();
		}
		this.groupIds.add(groupIdsItem);
		return this;
	}

	public GatewayUpdateVO removeGroupIdsItem(java.util.UUID groupIdsItem) {
		if (this.groupIds != null) {
			this.groupIds.remove(groupIdsItem);
		}
		return this;
	}

	// getter/setter

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
	}

	public java.util.Set<java.util.UUID> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(java.util.Set<java.util.UUID> newGroupIds) {
		this.groupIds = newGroupIds;
	}
}
