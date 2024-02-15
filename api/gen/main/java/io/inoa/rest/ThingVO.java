package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ThingVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_THING_TYPE_ID = "thing_type_id";
	public static final java.lang.String JSON_PROPERTY_CONFIG = "config";
	public static final java.lang.String JSON_PROPERTY_CREATED = "created";
	public static final java.lang.String JSON_PROPERTY_UPDATED = "updated";

	/** Id as technical reference (never changes). */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.util.UUID id;

	/** Name. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Id as technical reference (never changes). */
	@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
	@javax.validation.constraints.Size(min = 4, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String gatewayId;

	/** thing_type_id */
	@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_THING_TYPE_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String thingTypeId;

	/** config */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONFIG)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Map<String, java.lang.Object> config;

	/** Common timestamp for created/updated timestamps. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant created;

	/** Common timestamp for created/updated timestamps. */
	@javax.validation.constraints.NotNull
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
		ThingVO other = (ThingVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(thingTypeId, other.thingTypeId)
				&& java.util.Objects.equals(config, other.config)
				&& java.util.Objects.equals(created, other.created)
				&& java.util.Objects.equals(updated, other.updated);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name, gatewayId, thingTypeId, config, created, updated);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingVO[")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("gatewayId=").append(gatewayId).append(",")
				.append("thingTypeId=").append(thingTypeId).append(",")
				.append("config=").append(config).append(",")
				.append("created=").append(created).append(",")
				.append("updated=").append(updated)
				.append("]")
				.toString();
	}

	// fluent

	public ThingVO id(java.util.UUID newId) {
		this.id = newId;
		return this;
	}

	public ThingVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public ThingVO thingTypeId(java.lang.String newThingTypeId) {
		this.thingTypeId = newThingTypeId;
		return this;
	}

	public ThingVO config(java.util.Map<String, java.lang.Object> newConfig) {
		this.config = newConfig;
		return this;
	}
	
	public ThingVO putConfigItem(java.lang.String key, java.lang.Object configItem) {
		if (this.config == null) {
			this.config = new java.util.HashMap<>();
		}
		this.config.put(key, configItem);
		return this;
	}

	public ThingVO removeConfigItem(java.lang.String key) {
		if (this.config != null) {
			this.config.remove(key);
		}
		return this;
	}

	public ThingVO created(java.time.Instant newCreated) {
		this.created = newCreated;
		return this;
	}

	public ThingVO updated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
		return this;
	}

	// getter/setter

	public java.util.UUID getId() {
		return id;
	}

	public void setId(java.util.UUID newId) {
		this.id = newId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
	}

	public java.lang.String getThingTypeId() {
		return thingTypeId;
	}

	public void setThingTypeId(java.lang.String newThingTypeId) {
		this.thingTypeId = newThingTypeId;
	}

	public java.util.Map<String, java.lang.Object> getConfig() {
		return config;
	}

	public void setConfig(java.util.Map<String, java.lang.Object> newConfig) {
		this.config = newConfig;
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
