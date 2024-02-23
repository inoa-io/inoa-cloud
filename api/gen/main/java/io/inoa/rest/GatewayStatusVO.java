package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class GatewayStatusVO {

	public static final java.lang.String JSON_PROPERTY_MQTT = "mqtt";

	@jakarta.validation.Valid
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MQTT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private GatewayMqttStatusVO mqtt;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GatewayStatusVO other = (GatewayStatusVO) object;
		return java.util.Objects.equals(mqtt, other.mqtt);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(mqtt);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GatewayStatusVO[")
				.append("mqtt=").append(mqtt)
				.append("]")
				.toString();
	}

	// fluent

	public GatewayStatusVO mqtt(GatewayMqttStatusVO newMqtt) {
		this.mqtt = newMqtt;
		return this;
	}

	// getter/setter

	public GatewayMqttStatusVO getMqtt() {
		return mqtt;
	}

	public void setMqtt(GatewayMqttStatusVO newMqtt) {
		this.mqtt = newMqtt;
	}
}
