package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class TelemetryVO {

	public static final java.lang.String JSON_PROPERTY_TENANT_ID = "tenant_id";
	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_URN = "urn";
	public static final java.lang.String JSON_PROPERTY_DEVICE_TYPE = "device_type";
	public static final java.lang.String JSON_PROPERTY_DEVICE_ID = "device_id";
	public static final java.lang.String JSON_PROPERTY_SENSOR = "sensor";
	public static final java.lang.String JSON_PROPERTY_TIMESTAMP = "timestamp";
	public static final java.lang.String JSON_PROPERTY_VALUE = "value";
	public static final java.lang.String JSON_PROPERTY_EXT = "ext";

	/** Id of tenant. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TENANT_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String tenantId;

	/** Id of gateway @ tenant. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String gatewayId;

	/** TODO */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "urn:(?<deviceType>[a-zA-Z0-9\\-]{2,32}):(?<deviceId>[a-zA-Z0-9\\-]{2,36}):(?<sensor>[a-zA-Z0-9_\\-\\:*]{2,64})")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_URN)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String urn;

	/** TODO */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "[a-zA-Z0-9\\-]{2,32}")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DEVICE_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String deviceType;

	/** TODO */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "[a-zA-Z0-9\\-]{2,36}")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DEVICE_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String deviceId;

	/** TODO */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "[a-zA-Z0-9\\-\\:*]{2,64}")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_SENSOR)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String sensor;

	/** Timestamp of measurement value. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TIMESTAMP)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant timestamp;

	/** Value of measurement. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VALUE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Double value;

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
		TelemetryVO other = (TelemetryVO) object;
		return java.util.Objects.equals(tenantId, other.tenantId)
				&& java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(urn, other.urn)
				&& java.util.Objects.equals(deviceType, other.deviceType)
				&& java.util.Objects.equals(deviceId, other.deviceId)
				&& java.util.Objects.equals(sensor, other.sensor)
				&& java.util.Objects.equals(timestamp, other.timestamp)
				&& java.util.Objects.equals(value, other.value)
				&& java.util.Objects.equals(ext, other.ext);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(tenantId, gatewayId, urn, deviceType, deviceId, sensor, timestamp, value, ext);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("TelemetryVO[")
				.append("tenantId=").append(tenantId).append(",")
				.append("gatewayId=").append(gatewayId).append(",")
				.append("urn=").append(urn).append(",")
				.append("deviceType=").append(deviceType).append(",")
				.append("deviceId=").append(deviceId).append(",")
				.append("sensor=").append(sensor).append(",")
				.append("timestamp=").append(timestamp).append(",")
				.append("value=").append(value).append(",")
				.append("ext=").append(ext)
				.append("]")
				.toString();
	}

	// fluent

	public TelemetryVO tenantId(java.lang.String newTenantId) {
		this.tenantId = newTenantId;
		return this;
	}

	public TelemetryVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public TelemetryVO urn(java.lang.String newUrn) {
		this.urn = newUrn;
		return this;
	}

	public TelemetryVO deviceType(java.lang.String newDeviceType) {
		this.deviceType = newDeviceType;
		return this;
	}

	public TelemetryVO deviceId(java.lang.String newDeviceId) {
		this.deviceId = newDeviceId;
		return this;
	}

	public TelemetryVO sensor(java.lang.String newSensor) {
		this.sensor = newSensor;
		return this;
	}

	public TelemetryVO timestamp(java.time.Instant newTimestamp) {
		this.timestamp = newTimestamp;
		return this;
	}

	public TelemetryVO value(java.lang.Double newValue) {
		this.value = newValue;
		return this;
	}

	public TelemetryVO ext(java.util.Map<String, java.lang.String> newExt) {
		this.ext = newExt;
		return this;
	}
	
	public TelemetryVO putExtItem(java.lang.String key, java.lang.String extItem) {
		if (this.ext == null) {
			this.ext = new java.util.HashMap<>();
		}
		this.ext.put(key, extItem);
		return this;
	}

	public TelemetryVO removeExtItem(java.lang.String key) {
		if (this.ext != null) {
			this.ext.remove(key);
		}
		return this;
	}

	// getter/setter

	public java.lang.String getTenantId() {
		return tenantId;
	}

	public void setTenantId(java.lang.String newTenantId) {
		this.tenantId = newTenantId;
	}

	public java.lang.String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
	}

	public java.lang.String getUrn() {
		return urn;
	}

	public void setUrn(java.lang.String newUrn) {
		this.urn = newUrn;
	}

	public java.lang.String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(java.lang.String newDeviceType) {
		this.deviceType = newDeviceType;
	}

	public java.lang.String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(java.lang.String newDeviceId) {
		this.deviceId = newDeviceId;
	}

	public java.lang.String getSensor() {
		return sensor;
	}

	public void setSensor(java.lang.String newSensor) {
		this.sensor = newSensor;
	}

	public java.time.Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.time.Instant newTimestamp) {
		this.timestamp = newTimestamp;
	}

	public java.lang.Double getValue() {
		return value;
	}

	public void setValue(java.lang.Double newValue) {
		this.value = newValue;
	}

	public java.util.Map<String, java.lang.String> getExt() {
		return ext;
	}

	public void setExt(java.util.Map<String, java.lang.String> newExt) {
		this.ext = newExt;
	}
}
