package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingTypeChannelVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_KEY = "key";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_PROPERTIES = "properties";
	public static final java.lang.String JSON_PROPERTY_CREATED = "created";
	public static final java.lang.String JSON_PROPERTY_UPDATED = "updated";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.util.UUID id;

	/** key for the channel type */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String key;

	/** Name. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** description for the channel type */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PROPERTIES)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid PropertyDefinitionVO> properties;

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
		ThingTypeChannelVO other = (ThingTypeChannelVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(properties, other.properties)
				&& java.util.Objects.equals(created, other.created)
				&& java.util.Objects.equals(updated, other.updated);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, key, name, description, properties, created, updated);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingTypeChannelVO[")
				.append("id=").append(id).append(",")
				.append("key=").append(key).append(",")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("properties=").append(properties).append(",")
				.append("created=").append(created).append(",")
				.append("updated=").append(updated)
				.append("]")
				.toString();
	}

	// fluent

	public ThingTypeChannelVO id(java.util.UUID newId) {
		this.id = newId;
		return this;
	}

	public ThingTypeChannelVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public ThingTypeChannelVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingTypeChannelVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public ThingTypeChannelVO properties(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid PropertyDefinitionVO> newProperties) {
		this.properties = newProperties;
		return this;
	}
	
	public ThingTypeChannelVO addPropertiesItem(PropertyDefinitionVO propertiesItem) {
		if (this.properties == null) {
			this.properties = new java.util.ArrayList<>();
		}
		this.properties.add(propertiesItem);
		return this;
	}

	public ThingTypeChannelVO removePropertiesItem(PropertyDefinitionVO propertiesItem) {
		if (this.properties != null) {
			this.properties.remove(propertiesItem);
		}
		return this;
	}

	public ThingTypeChannelVO created(java.time.Instant newCreated) {
		this.created = newCreated;
		return this;
	}

	public ThingTypeChannelVO updated(java.time.Instant newUpdated) {
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

	public java.lang.String getKey() {
		return key;
	}

	public void setKey(java.lang.String newKey) {
		this.key = newKey;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String newDescription) {
		this.description = newDescription;
	}

	public java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid PropertyDefinitionVO> getProperties() {
		return properties;
	}

	public void setProperties(java.util.List<@jakarta.validation.constraints.NotNull @jakarta.validation.Valid PropertyDefinitionVO> newProperties) {
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
