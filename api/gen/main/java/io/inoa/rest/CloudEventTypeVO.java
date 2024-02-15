package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public enum CloudEventTypeVO {

	LOG_EMITTED("io.inoa.log.emitted"),
	MEASUREMENT_TELEMETRY("io.inoa.measurement.telemetry");

	public static final java.lang.String LOG_EMITTED_VALUE = "io.inoa.log.emitted";
	public static final java.lang.String MEASUREMENT_TELEMETRY_VALUE = "io.inoa.measurement.telemetry";

	private final java.lang.String value;

	private CloudEventTypeVO(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static CloudEventTypeVO toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<CloudEventTypeVO> toOptional(java.lang.String value) {
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
