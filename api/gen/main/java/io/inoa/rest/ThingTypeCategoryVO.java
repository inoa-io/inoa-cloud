package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public enum ThingTypeCategoryVO {

	NONE("NONE"),
	ELECTRIC_METER("ELECTRIC_METER"),
	GAS_METER("GAS_METER"),
	CURRENT_TRANSFORMER("CURRENT_TRANSFORMER"),
	SMART_PLUG("SMART_PLUG");

	public static final java.lang.String NONE_VALUE = "NONE";
	public static final java.lang.String ELECTRIC_METER_VALUE = "ELECTRIC_METER";
	public static final java.lang.String GAS_METER_VALUE = "GAS_METER";
	public static final java.lang.String CURRENT_TRANSFORMER_VALUE = "CURRENT_TRANSFORMER";
	public static final java.lang.String SMART_PLUG_VALUE = "SMART_PLUG";

	private final java.lang.String value;

	private ThingTypeCategoryVO(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static ThingTypeCategoryVO toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<ThingTypeCategoryVO> toOptional(java.lang.String value) {
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
