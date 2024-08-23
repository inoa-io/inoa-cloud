package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
public enum MeasurandProtocolVO {

	JSON_REST_HTTP("JSON_REST_HTTP"),
	MODBUS_RS485("MODBUS_RS485"),
	MODBUS_TCP("MODBUS_TCP"),
	S0("S0"),
	MBUS("MBUS"),
	WMBUS("WMBUS");

	public static final java.lang.String JSON_REST_HTTP_VALUE = "JSON_REST_HTTP";
	public static final java.lang.String MODBUS_RS485_VALUE = "MODBUS_RS485";
	public static final java.lang.String MODBUS_TCP_VALUE = "MODBUS_TCP";
	public static final java.lang.String S0_VALUE = "S0";
	public static final java.lang.String MBUS_VALUE = "MBUS";
	public static final java.lang.String WMBUS_VALUE = "WMBUS";

	private final java.lang.String value;

	private MeasurandProtocolVO(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static MeasurandProtocolVO toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<MeasurandProtocolVO> toOptional(java.lang.String value) {
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
