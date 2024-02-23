package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class GatewayDetailVO {

	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_GROUP_IDS = "group_ids";
	public static final java.lang.String JSON_PROPERTY_PROPERTIES = "properties";
	public static final java.lang.String JSON_PROPERTY_CREATED = "created";
	public static final java.lang.String JSON_PROPERTY_UPDATED = "updated";

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

	/** Flag if enabled or not. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Boolean enabled = true;

	/** Ids of groups where gateway is member. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GROUP_IDS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.util.Set<java.util.@jakarta.validation.constraints.NotNull UUID> groupIds = new java.util.LinkedHashSet<>();

	/** Properties set by gateway. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PROPERTIES)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.util.Map<String, java.lang.String> properties = new java.util.HashMap<>();

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
		GatewayDetailVO other = (GatewayDetailVO) object;
		return java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(groupIds, other.groupIds)
				&& java.util.Objects.equals(properties, other.properties)
				&& java.util.Objects.equals(created, other.created)
				&& java.util.Objects.equals(updated, other.updated);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(gatewayId, name, enabled, groupIds, properties, created, updated);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GatewayDetailVO[")
				.append("gatewayId=").append(gatewayId).append(",")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("groupIds=").append(groupIds).append(",")
				.append("properties=").append(properties).append(",")
				.append("created=").append(created).append(",")
				.append("updated=").append(updated)
				.append("]")
				.toString();
	}

	// fluent

	public GatewayDetailVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public GatewayDetailVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public GatewayDetailVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public GatewayDetailVO groupIds(java.util.Set<java.util.@jakarta.validation.constraints.NotNull UUID> newGroupIds) {
		this.groupIds = newGroupIds;
		return this;
	}
	
	public GatewayDetailVO addGroupIdsItem(java.util.UUID groupIdsItem) {
		if (this.groupIds == null) {
			this.groupIds = new java.util.LinkedHashSet<>();
		}
		this.groupIds.add(groupIdsItem);
		return this;
	}

	public GatewayDetailVO removeGroupIdsItem(java.util.UUID groupIdsItem) {
		if (this.groupIds != null) {
			this.groupIds.remove(groupIdsItem);
		}
		return this;
	}

	public GatewayDetailVO properties(java.util.Map<String, java.lang.String> newProperties) {
		this.properties = newProperties;
		return this;
	}
	
	public GatewayDetailVO putPropertiesItem(java.lang.String key, java.lang.String propertiesItem) {
		if (this.properties == null) {
			this.properties = new java.util.HashMap<>();
		}
		this.properties.put(key, propertiesItem);
		return this;
	}

	public GatewayDetailVO removePropertiesItem(java.lang.String key) {
		if (this.properties != null) {
			this.properties.remove(key);
		}
		return this;
	}

	public GatewayDetailVO created(java.time.Instant newCreated) {
		this.created = newCreated;
		return this;
	}

	public GatewayDetailVO updated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
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

	public java.util.Map<String, java.lang.String> getProperties() {
		return properties;
	}

	public void setProperties(java.util.Map<String, java.lang.String> newProperties) {
		this.properties = newProperties;
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
