package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingTypeCreateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_CATEGORY = "category";
	public static final java.lang.String JSON_PROPERTY_THING_TYPE_ID = "thing_type_id";
	public static final java.lang.String JSON_PROPERTY_JSON_SCHEMA = "json_schema";

	/** Name. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Category. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CATEGORY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String category;

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_THING_TYPE_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String thingTypeId;

	/** json_schema */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_JSON_SCHEMA)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Map<String, java.lang.Object> jsonSchema;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		ThingTypeCreateVO other = (ThingTypeCreateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(category, other.category)
				&& java.util.Objects.equals(thingTypeId, other.thingTypeId)
				&& java.util.Objects.equals(jsonSchema, other.jsonSchema);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, category, thingTypeId, jsonSchema);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingTypeCreateVO[")
				.append("name=").append(name).append(",")
				.append("category=").append(category).append(",")
				.append("thingTypeId=").append(thingTypeId).append(",")
				.append("jsonSchema=").append(jsonSchema)
				.append("]")
				.toString();
	}

	// fluent

	public ThingTypeCreateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public ThingTypeCreateVO category(java.lang.String newCategory) {
		this.category = newCategory;
		return this;
	}

	public ThingTypeCreateVO thingTypeId(java.lang.String newThingTypeId) {
		this.thingTypeId = newThingTypeId;
		return this;
	}

	public ThingTypeCreateVO jsonSchema(java.util.Map<String, java.lang.Object> newJsonSchema) {
		this.jsonSchema = newJsonSchema;
		return this;
	}
	
	public ThingTypeCreateVO putJsonSchemaItem(java.lang.String key, java.lang.Object jsonSchemaItem) {
		if (this.jsonSchema == null) {
			this.jsonSchema = new java.util.HashMap<>();
		}
		this.jsonSchema.put(key, jsonSchemaItem);
		return this;
	}

	public ThingTypeCreateVO removeJsonSchemaItem(java.lang.String key) {
		if (this.jsonSchema != null) {
			this.jsonSchema.remove(key);
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

	public java.lang.String getCategory() {
		return category;
	}

	public void setCategory(java.lang.String newCategory) {
		this.category = newCategory;
	}

	public java.lang.String getThingTypeId() {
		return thingTypeId;
	}

	public void setThingTypeId(java.lang.String newThingTypeId) {
		this.thingTypeId = newThingTypeId;
	}

	public java.util.Map<String, java.lang.Object> getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(java.util.Map<String, java.lang.Object> newJsonSchema) {
		this.jsonSchema = newJsonSchema;
	}
}
