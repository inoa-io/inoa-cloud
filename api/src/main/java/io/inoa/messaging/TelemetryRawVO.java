package io.inoa.messaging;

import java.time.Instant;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Introspected;

/**
 * Value object for telemetry send by gateways.
 *
 * @author stephan.schnabel@grayc.de
 * @param urn       URN with device type and id plus sensor identifier.
 * @param timestamp Timestamp of measurement (epoch milliseconds).
 * @param value     Value of measurement as base64 encoded byte array.
 * @param ext       Additional information related to this telemetry.
 */
@Introspected
public record TelemetryRawVO(
		@NotNull @Pattern(regexp = URN_REGEX) String urn,
		@NotNull @JsonFormat(without = JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS) Instant timestamp,
		@NotNull byte[] value,
		Map<String, String> ext) {

	public static final String URN_REGEX = "urn"
			+ ":(?<deviceType>[a-zA-Z0-9\\-]{2,32})"
			+ ":(?<deviceId>[a-zA-Z0-9\\-]{2,36})"
			+ ":(?<sensor>[a-zA-Z0-9_\\-\\:*]{2,64})";
	public static final java.util.regex.Pattern URN_PATTERN = java.util.regex.Pattern.compile(URN_REGEX);

	public static TelemetryRawVO of(String urn, Object value) {
		return of(urn, Instant.now(), value);
	}

	public static TelemetryRawVO of(String urn, Instant timestamp, Object value) {
		return new TelemetryRawVO(
				urn,
				timestamp,
				value instanceof byte[] ? (byte[]) value : value.toString().getBytes(),
				null);
	}
}
