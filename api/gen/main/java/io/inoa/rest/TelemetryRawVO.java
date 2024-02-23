package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class TelemetryRawVO {

	public static final java.lang.String JSON_PROPERTY_URN = "urn";
	public static final java.lang.String JSON_PROPERTY_TIMESTAMP = "timestamp";
	public static final java.lang.String JSON_PROPERTY_VALUE = "value";
	public static final java.lang.String JSON_PROPERTY_EXT = "ext";

	/** URN with device type and id plus sensor identifier. Sensor can by anything, e.g. obis code or modbus register. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "urn:(?<deviceType>[a-zA-Z0-9\\-]{2,32}):(?<deviceId>[a-zA-Z0-9\\-]{2,36}):(?<sensor>[a-zA-Z0-9_\\-\\:*]{2,64})")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_URN)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String urn;

	/** Timestamp of measurement (epoch milliseconds). */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TIMESTAMP)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Long timestamp;

	/** Value of measurement, is a base64 encoded byte array. Will be interpreted later. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALUE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private byte[] value;

	/** Additional stuff describing this measurement. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_EXT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.Map<String, java.lang.String> ext;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		TelemetryRawVO other = (TelemetryRawVO) object;
		return java.util.Objects.equals(urn, other.urn)
				&& java.util.Objects.equals(timestamp, other.timestamp)
				&& java.util.Arrays.equals(value, other.value)
				&& java.util.Objects.equals(ext, other.ext);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(urn, timestamp, java.util.Arrays.hashCode(value), ext);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("TelemetryRawVO[")
				.append("urn=").append(urn).append(",")
				.append("timestamp=").append(timestamp).append(",")
				.append("value.length=").append(value == null ? null : value.length).append(",")
				.append("ext=").append(ext)
				.append("]")
				.toString();
	}

	// fluent

	public TelemetryRawVO urn(java.lang.String newUrn) {
		this.urn = newUrn;
		return this;
	}

	public TelemetryRawVO timestamp(java.lang.Long newTimestamp) {
		this.timestamp = newTimestamp;
		return this;
	}

	public TelemetryRawVO value(byte[] newValue) {
		this.value = newValue;
		return this;
	}

	public TelemetryRawVO ext(java.util.Map<String, java.lang.String> newExt) {
		this.ext = newExt;
		return this;
	}
	
	public TelemetryRawVO putExtItem(java.lang.String key, java.lang.String extItem) {
		if (this.ext == null) {
			this.ext = new java.util.HashMap<>();
		}
		this.ext.put(key, extItem);
		return this;
	}

	public TelemetryRawVO removeExtItem(java.lang.String key) {
		if (this.ext != null) {
			this.ext.remove(key);
		}
		return this;
	}

	// getter/setter

	public java.lang.String getUrn() {
		return urn;
	}

	public void setUrn(java.lang.String newUrn) {
		this.urn = newUrn;
	}

	public java.lang.Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.lang.Long newTimestamp) {
		this.timestamp = newTimestamp;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] newValue) {
		this.value = newValue;
	}

	public java.util.Map<String, java.lang.String> getExt() {
		return ext;
	}

	public void setExt(java.util.Map<String, java.lang.String> newExt) {
		this.ext = newExt;
	}
}
