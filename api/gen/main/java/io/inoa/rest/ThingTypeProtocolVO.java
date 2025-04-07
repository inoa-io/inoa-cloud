package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public enum ThingTypeProtocolVO {

	JSON_REST_HTTP("JSON_REST_HTTP"),
	MODBUS_RS458("MODBUS_RS458"),
	MODBUS_TCP("MODBUS_TCP"),
	S0("S0"),
	MBUS("MBUS"),
	WMBUS("WMBUS"),
	ADC("ADC"),
	RMS("RMS");

	public static final java.lang.String JSON_REST_HTTP_VALUE = "JSON_REST_HTTP";
	public static final java.lang.String MODBUS_RS458_VALUE = "MODBUS_RS458";
	public static final java.lang.String MODBUS_TCP_VALUE = "MODBUS_TCP";
	public static final java.lang.String S0_VALUE = "S0";
	public static final java.lang.String MBUS_VALUE = "MBUS";
	public static final java.lang.String WMBUS_VALUE = "WMBUS";
	public static final java.lang.String ADC_VALUE = "ADC";
	public static final java.lang.String RMS_VALUE = "RMS";

	private final java.lang.String value;

	private ThingTypeProtocolVO(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static ThingTypeProtocolVO toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<ThingTypeProtocolVO> toOptional(java.lang.String value) {
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
