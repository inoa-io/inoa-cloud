package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
public enum ThingConfigurationTypeVO {

	STRING("STRING"),
	NUMBER("NUMBER"),
	BOOLEAN("BOOLEAN");

	public static final java.lang.String STRING_VALUE = "STRING";
	public static final java.lang.String NUMBER_VALUE = "NUMBER";
	public static final java.lang.String BOOLEAN_VALUE = "BOOLEAN";

	private final java.lang.String value;

	private ThingConfigurationTypeVO(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static ThingConfigurationTypeVO toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<ThingConfigurationTypeVO> toOptional(java.lang.String value) {
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
