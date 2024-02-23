package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class GatewayMqttStatusVO {

	public static final java.lang.String JSON_PROPERTY_CONNECTED = "connected";
	public static final java.lang.String JSON_PROPERTY_TIMESTAMP = "timestamp";

	/** connection status */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CONNECTED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean connected;

	/** Common timestamp for created/updated timestamps. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TIMESTAMP)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.time.Instant timestamp;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GatewayMqttStatusVO other = (GatewayMqttStatusVO) object;
		return java.util.Objects.equals(connected, other.connected)
				&& java.util.Objects.equals(timestamp, other.timestamp);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(connected, timestamp);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GatewayMqttStatusVO[")
				.append("connected=").append(connected).append(",")
				.append("timestamp=").append(timestamp)
				.append("]")
				.toString();
	}

	// fluent

	public GatewayMqttStatusVO connected(java.lang.Boolean newConnected) {
		this.connected = newConnected;
		return this;
	}

	public GatewayMqttStatusVO timestamp(java.time.Instant newTimestamp) {
		this.timestamp = newTimestamp;
		return this;
	}

	// getter/setter

	public java.lang.Boolean getConnected() {
		return connected;
	}

	public void setConnected(java.lang.Boolean newConnected) {
		this.connected = newConnected;
	}

	public java.time.Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.time.Instant newTimestamp) {
		this.timestamp = newTimestamp;
	}
}
