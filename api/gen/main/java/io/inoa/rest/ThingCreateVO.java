package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ThingCreateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_THING_TYPE_ID = "thing_type_id";
	public static final java.lang.String JSON_PROPERTY_CONFIG = "config";

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

	/** External thing type id */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_THING_TYPE_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String thingTypeId;

	/** config */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONFIG)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Map<String, java.lang.Object> config;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingCreateVO other = (ThingCreateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(thingTypeId, other.thingTypeId)
				&& java.util.Objects.equals(config, other.config);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, gatewayId, thingTypeId, config);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingCreateVO[")
				.append("name=").append(name).append(",")
				.append("gatewayId=").append(gatewayId).append(",")
				.append("thingTypeId=").append(thingTypeId).append(",")
				.append("config=").append(config)
				.append("]")
				.toString();
	}

	// fluent

	public ThingCreateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingCreateVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public ThingCreateVO thingTypeId(java.lang.String newThingTypeId) {
		this.thingTypeId = newThingTypeId;
		return this;
	}

	public ThingCreateVO config(java.util.Map<String, java.lang.Object> newConfig) {
		this.config = newConfig;
		return this;
	}
	
	public ThingCreateVO putConfigItem(java.lang.String key, java.lang.Object configItem) {
		if (this.config == null) {
			this.config = new java.util.HashMap<>();
		}
		this.config.put(key, configItem);
		return this;
	}

	public ThingCreateVO removeConfigItem(java.lang.String key) {
		if (this.config != null) {
			this.config.remove(key);
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
}
