package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class ThingTypeCreateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_THING_TYPE_ID = "thing_type_id";
	public static final java.lang.String JSON_PROPERTY_JSON_SCHEMA = "json_schema";
	public static final java.lang.String JSON_PROPERTY_UI_LAYOUT = "ui_layout";

	/** Name. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** External thing type Id */
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_THING_TYPE_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String thingTypeId;

	/** json_schema */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_JSON_SCHEMA)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Map<String, java.lang.Object> jsonSchema;

	/** ui_layout */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_UI_LAYOUT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<java.util.Map<String, java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.Valid Object>> uiLayout;

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
				&& java.util.Objects.equals(thingTypeId, other.thingTypeId)
				&& java.util.Objects.equals(jsonSchema, other.jsonSchema)
				&& java.util.Objects.equals(uiLayout, other.uiLayout);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, thingTypeId, jsonSchema, uiLayout);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ThingTypeCreateVO[")
				.append("name=").append(name).append(",")
				.append("thingTypeId=").append(thingTypeId).append(",")
				.append("jsonSchema=").append(jsonSchema).append(",")
				.append("uiLayout=").append(uiLayout)
				.append("]")
				.toString();
	}

	// fluent

	public ThingTypeCreateVO name(java.lang.String newName) {
		this.name = newName;
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

	public ThingTypeCreateVO uiLayout(java.util.List<java.util.Map<String, java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.Valid Object>> newUiLayout) {
		this.uiLayout = newUiLayout;
		return this;
	}
	
	public ThingTypeCreateVO addUiLayoutItem(java.util.Map<String, java.lang.Object> uiLayoutItem) {
		if (this.uiLayout == null) {
			this.uiLayout = new java.util.ArrayList<>();
		}
		this.uiLayout.add(uiLayoutItem);
		return this;
	}

	public ThingTypeCreateVO removeUiLayoutItem(java.util.Map<String, java.lang.Object> uiLayoutItem) {
		if (this.uiLayout != null) {
			this.uiLayout.remove(uiLayoutItem);
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

	public java.util.List<java.util.Map<String, java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.Valid Object>> getUiLayout() {
		return uiLayout;
	}

	public void setUiLayout(java.util.List<java.util.Map<String, java.lang.@jakarta.validation.constraints.NotNull @jakarta.validation.Valid Object>> newUiLayout) {
		this.uiLayout = newUiLayout;
	}
}
