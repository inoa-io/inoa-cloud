package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class MeasurandVO {

	public static final java.lang.String JSON_PROPERTY_MEASURAND_TYPE = "measurand_type";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_INTERVAL = "interval";
	public static final java.lang.String JSON_PROPERTY_TIMEOUT = "timeout";

	/** The OBIS code */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^((?<a>[0-9]{1,3})-)?((?<b>[0-9]{1,3}):)?(S\\.)?(?<cde>(?<c>[0-9A-F]{1,3}).(?<d>[0-9A-F]{1,3})(.(?<e>[0-9A-F]{1,3}))?)([\\*\\&](?<f>[0-9A-F]{1,3}))?$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MEASURAND_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String measurandType;

	/** Indicates if the measurand is measured */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Boolean enabled;

	/** Duration between two measurements in milliseconds */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_INTERVAL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Long interval;

	/** Maximum duration a measurement may take in milliseconds */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TIMEOUT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Long timeout;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		MeasurandVO other = (MeasurandVO) object;
		return java.util.Objects.equals(measurandType, other.measurandType)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(interval, other.interval)
				&& java.util.Objects.equals(timeout, other.timeout);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(measurandType, enabled, interval, timeout);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("MeasurandVO[")
				.append("measurandType=").append(measurandType).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("interval=").append(interval).append(",")
				.append("timeout=").append(timeout)
				.append("]")
				.toString();
	}

	// fluent

	public MeasurandVO measurandType(java.lang.String newMeasurandType) {
		this.measurandType = newMeasurandType;
		return this;
	}

	public MeasurandVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public MeasurandVO interval(java.lang.Long newInterval) {
		this.interval = newInterval;
		return this;
	}

	public MeasurandVO timeout(java.lang.Long newTimeout) {
		this.timeout = newTimeout;
		return this;
	}

	// getter/setter

	public java.lang.String getMeasurandType() {
		return measurandType;
	}

	public void setMeasurandType(java.lang.String newMeasurandType) {
		this.measurandType = newMeasurandType;
	}

	public java.lang.Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
	}

	public java.lang.Long getInterval() {
		return interval;
	}

	public void setInterval(java.lang.Long newInterval) {
		this.interval = newInterval;
	}

	public java.lang.Long getTimeout() {
		return timeout;
	}

	public void setTimeout(java.lang.Long newTimeout) {
		this.timeout = newTimeout;
	}
}
