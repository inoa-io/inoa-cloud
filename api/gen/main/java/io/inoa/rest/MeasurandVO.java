package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class MeasurandVO {

	public static final java.lang.String JSON_PROPERTY_URI = "uri";
	public static final java.lang.String JSON_PROPERTY_MEASURAND_TYPE = "measurand_type";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_INTERVAL = "interval";
	public static final java.lang.String JSON_PROPERTY_TIMEOUT = "timeout";

	/** URI for the measurand which is unique per gateway */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_URI)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.net.URI uri;

	@jakarta.validation.Valid
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MEASURAND_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private MeasurandTypeVO measurandType;

	/** Indicates if the measurand is measured */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Boolean enabled;

	/** Duration between two measurements in milliseconds */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_INTERVAL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Integer interval;

	/** Maximum duration a measurement may take in milliseconds */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TIMEOUT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Integer timeout;

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
		return java.util.Objects.equals(uri, other.uri)
				&& java.util.Objects.equals(measurandType, other.measurandType)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(interval, other.interval)
				&& java.util.Objects.equals(timeout, other.timeout);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(uri, measurandType, enabled, interval, timeout);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("MeasurandVO[")
				.append("uri=").append(uri).append(",")
				.append("measurandType=").append(measurandType).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("interval=").append(interval).append(",")
				.append("timeout=").append(timeout)
				.append("]")
				.toString();
	}

	// fluent

	public MeasurandVO uri(java.net.URI newUri) {
		this.uri = newUri;
		return this;
	}

	public MeasurandVO measurandType(MeasurandTypeVO newMeasurandType) {
		this.measurandType = newMeasurandType;
		return this;
	}

	public MeasurandVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public MeasurandVO interval(java.lang.Integer newInterval) {
		this.interval = newInterval;
		return this;
	}

	public MeasurandVO timeout(java.lang.Integer newTimeout) {
		this.timeout = newTimeout;
		return this;
	}

	// getter/setter

	public java.net.URI getUri() {
		return uri;
	}

	public void setUri(java.net.URI newUri) {
		this.uri = newUri;
	}

	public MeasurandTypeVO getMeasurandType() {
		return measurandType;
	}

	public void setMeasurandType(MeasurandTypeVO newMeasurandType) {
		this.measurandType = newMeasurandType;
	}

	public java.lang.Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
	}

	public java.lang.Integer getInterval() {
		return interval;
	}

	public void setInterval(java.lang.Integer newInterval) {
		this.interval = newInterval;
	}

	public java.lang.Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(java.lang.Integer newTimeout) {
		this.timeout = newTimeout;
	}
}
