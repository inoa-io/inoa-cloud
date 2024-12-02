package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class GatewayCreateVO {

	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_LOCATION = "location";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_GROUP_IDS = "group_ids";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
	@jakarta.validation.constraints.Size(min = 4, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String gatewayId;

	/** Human friendly description (can change). */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	@jakarta.validation.Valid
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_LOCATION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private GatewayLocationDataVO location;

	/** Flag if enabled or not. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean enabled = true;

	/** Ids of groups where gateway is member. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GROUP_IDS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Set<java.util.@jakarta.validation.constraints.NotNull UUID> groupIds;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GatewayCreateVO other = (GatewayCreateVO) object;
		return java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(location, other.location)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(groupIds, other.groupIds);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(gatewayId, name, location, enabled, groupIds);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GatewayCreateVO[")
				.append("gatewayId=").append(gatewayId).append(",")
				.append("name=").append(name).append(",")
				.append("location=").append(location).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("groupIds=").append(groupIds)
				.append("]")
				.toString();
	}

	// fluent

	public GatewayCreateVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public GatewayCreateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public GatewayCreateVO location(GatewayLocationDataVO newLocation) {
		this.location = newLocation;
		return this;
	}

	public GatewayCreateVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public GatewayCreateVO groupIds(java.util.Set<java.util.@jakarta.validation.constraints.NotNull UUID> newGroupIds) {
		this.groupIds = newGroupIds;
		return this;
	}
	
	public GatewayCreateVO addGroupIdsItem(java.util.UUID groupIdsItem) {
		if (this.groupIds == null) {
			this.groupIds = new java.util.LinkedHashSet<>();
		}
		this.groupIds.add(groupIdsItem);
		return this;
	}

	public GatewayCreateVO removeGroupIdsItem(java.util.UUID groupIdsItem) {
		if (this.groupIds != null) {
			this.groupIds.remove(groupIdsItem);
		}
		return this;
	}

	// getter/setter

	public java.lang.String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public GatewayLocationDataVO getLocation() {
		return location;
	}

	public void setLocation(GatewayLocationDataVO newLocation) {
		this.location = newLocation;
	}

	public java.lang.Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
	}

	public java.util.Set<java.util.@jakarta.validation.constraints.NotNull UUID> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(java.util.Set<java.util.@jakarta.validation.constraints.NotNull UUID> newGroupIds) {
		this.groupIds = newGroupIds;
	}
}
