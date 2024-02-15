package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ThingTypeVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_JSON_SCHEMA = "json_schema";
	public static final java.lang.String JSON_PROPERTY_UI_LAYOUT = "ui_layout";
	public static final java.lang.String JSON_PROPERTY_PROPERTIES = "properties";
	public static final java.lang.String JSON_PROPERTY_CHANNELS = "channels";
	public static final java.lang.String JSON_PROPERTY_CREATED = "created";
	public static final java.lang.String JSON_PROPERTY_UPDATED = "updated";

	/** Id as technical reference (never changes). */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String id;

	/** Name. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** json_schema */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_JSON_SCHEMA)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Map<String, java.lang.Object> jsonSchema;

	/** ui_layout */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_UI_LAYOUT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<java.util.Map<String, java.lang.Object>> uiLayout;

	@javax.validation.Valid
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PROPERTIES)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<PropertyDefinitionVO> properties;

	@javax.validation.Valid
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CHANNELS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<ThingTypeChannelVO> channels;

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
		ThingTypeVO other = (ThingTypeVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(jsonSchema, other.jsonSchema)
				&& java.util.Objects.equals(uiLayout, other.uiLayout)
				&& java.util.Objects.equals(properties, other.properties)
				&& java.util.Objects.equals(channels, other.channels)
				&& java.util.Objects.equals(created, other.created)
				&& java.util.Objects.equals(updated, other.updated);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name, jsonSchema, uiLayout, properties, channels, created, updated);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingTypeVO[")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("jsonSchema=").append(jsonSchema).append(",")
				.append("uiLayout=").append(uiLayout).append(",")
				.append("properties=").append(properties).append(",")
				.append("channels=").append(channels).append(",")
				.append("created=").append(created).append(",")
				.append("updated=").append(updated)
				.append("]")
				.toString();
	}

	// fluent

	public ThingTypeVO id(java.lang.String newId) {
		this.id = newId;
		return this;
	}

	public ThingTypeVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingTypeVO jsonSchema(java.util.Map<String, java.lang.Object> newJsonSchema) {
		this.jsonSchema = newJsonSchema;
		return this;
	}
	
	public ThingTypeVO putJsonSchemaItem(java.lang.String key, java.lang.Object jsonSchemaItem) {
		if (this.jsonSchema == null) {
			this.jsonSchema = new java.util.HashMap<>();
		}
		this.jsonSchema.put(key, jsonSchemaItem);
		return this;
	}

	public ThingTypeVO removeJsonSchemaItem(java.lang.String key) {
		if (this.jsonSchema != null) {
			this.jsonSchema.remove(key);
		}
		return this;
	}

	public ThingTypeVO uiLayout(java.util.List<java.util.Map<String, java.lang.Object>> newUiLayout) {
		this.uiLayout = newUiLayout;
		return this;
	}
	
	public ThingTypeVO addUiLayoutItem(java.util.Map<String, java.lang.Object> uiLayoutItem) {
		if (this.uiLayout == null) {
			this.uiLayout = new java.util.ArrayList<>();
		}
		this.uiLayout.add(uiLayoutItem);
		return this;
	}

	public ThingTypeVO removeUiLayoutItem(java.util.Map<String, java.lang.Object> uiLayoutItem) {
		if (this.uiLayout != null) {
			this.uiLayout.remove(uiLayoutItem);
		}
		return this;
	}

	public ThingTypeVO properties(java.util.List<PropertyDefinitionVO> newProperties) {
		this.properties = newProperties;
		return this;
	}
	
	public ThingTypeVO addPropertiesItem(PropertyDefinitionVO propertiesItem) {
		if (this.properties == null) {
			this.properties = new java.util.ArrayList<>();
		}
		this.properties.add(propertiesItem);
		return this;
	}

	public ThingTypeVO removePropertiesItem(PropertyDefinitionVO propertiesItem) {
		if (this.properties != null) {
			this.properties.remove(propertiesItem);
		}
		return this;
	}

	public ThingTypeVO channels(java.util.List<ThingTypeChannelVO> newChannels) {
		this.channels = newChannels;
		return this;
	}
	
	public ThingTypeVO addChannelsItem(ThingTypeChannelVO channelsItem) {
		if (this.channels == null) {
			this.channels = new java.util.ArrayList<>();
		}
		this.channels.add(channelsItem);
		return this;
	}

	public ThingTypeVO removeChannelsItem(ThingTypeChannelVO channelsItem) {
		if (this.channels != null) {
			this.channels.remove(channelsItem);
		}
		return this;
	}

	public ThingTypeVO created(java.time.Instant newCreated) {
		this.created = newCreated;
		return this;
	}

	public ThingTypeVO updated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
		return this;
	}

	// getter/setter

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String newId) {
		this.id = newId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.util.Map<String, java.lang.Object> getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(java.util.Map<String, java.lang.Object> newJsonSchema) {
		this.jsonSchema = newJsonSchema;
	}

	public java.util.List<java.util.Map<String, java.lang.Object>> getUiLayout() {
		return uiLayout;
	}

	public void setUiLayout(java.util.List<java.util.Map<String, java.lang.Object>> newUiLayout) {
		this.uiLayout = newUiLayout;
	}

	public java.util.List<PropertyDefinitionVO> getProperties() {
		return properties;
	}

	public void setProperties(java.util.List<PropertyDefinitionVO> newProperties) {
		this.properties = newProperties;
	}

	public java.util.List<ThingTypeChannelVO> getChannels() {
		return channels;
	}

	public void setChannels(java.util.List<ThingTypeChannelVO> newChannels) {
		this.channels = newChannels;
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
