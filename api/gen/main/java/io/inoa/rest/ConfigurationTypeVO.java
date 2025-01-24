package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public enum ConfigurationTypeVO {

	INTEGER("INTEGER"),
	BOOLEAN("BOOLEAN"),
	STRING("STRING"),
	URL("URL");

	public static final java.lang.String INTEGER_VALUE = "INTEGER";
	public static final java.lang.String BOOLEAN_VALUE = "BOOLEAN";
	public static final java.lang.String STRING_VALUE = "STRING";
	public static final java.lang.String URL_VALUE = "URL";

	private final java.lang.String value;

	private ConfigurationTypeVO(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static ConfigurationTypeVO toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<ConfigurationTypeVO> toOptional(java.lang.String value) {
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
