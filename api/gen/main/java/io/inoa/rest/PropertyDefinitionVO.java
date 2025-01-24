package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class PropertyDefinitionVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_KEY = "key";
	public static final java.lang.String JSON_PROPERTY_INPUT_TYPE = "input_type";

	/** Human readable name. */
	@jakarta.validation.constraints.Size(max = 64)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** key for the property */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_KEY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String key;

	/** input type for the frontend */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_INPUT_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private InputType inputType;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		PropertyDefinitionVO other = (PropertyDefinitionVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(key, other.key)
				&& java.util.Objects.equals(inputType, other.inputType);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, key, inputType);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("PropertyDefinitionVO[")
				.append("name=").append(name).append(",")
				.append("key=").append(key).append(",")
				.append("inputType=").append(inputType)
				.append("]")
				.toString();
	}

	// fluent

	public PropertyDefinitionVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public PropertyDefinitionVO key(java.lang.String newKey) {
		this.key = newKey;
		return this;
	}

	public PropertyDefinitionVO inputType(InputType newInputType) {
		this.inputType = newInputType;
		return this;
	}

	// getter/setter

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getKey() {
		return key;
	}

	public void setKey(java.lang.String newKey) {
		this.key = newKey;
	}

	public InputType getInputType() {
		return inputType;
	}

	public void setInputType(InputType newInputType) {
		this.inputType = newInputType;
	}

@io.micronaut.serde.annotation.Serdeable
public enum InputType {

	TEXT("text"),
	NUMBER("number");

	public static final java.lang.String TEXT_VALUE = "text";
	public static final java.lang.String NUMBER_VALUE = "number";

	private final java.lang.String value;

	private InputType(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static InputType toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<InputType> toOptional(java.lang.String value) {
		return java.util.Arrays
				.stream(values())
				.filter(e -> e.value.equals(value))
				.findAny();
	}

	@com.fasterxml.jackson.annotation.JsonValue
	public java.lang.String getValue() {
		return value;
	}
}
}
