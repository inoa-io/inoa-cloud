package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class TenantUpdateVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID_PATTERN = "gateway_id_pattern";

	/** Name of a tenant. */
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\s]*$")
	@jakarta.validation.constraints.Size(min = 3, max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** Is tenant enabled */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean enabled;

	/** Regular expression to force specific gateway IDs for this tenant */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID_PATTERN)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String gatewayIdPattern;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		TenantUpdateVO other = (TenantUpdateVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(gatewayIdPattern, other.gatewayIdPattern);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, enabled, gatewayIdPattern);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("TenantUpdateVO[")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("gatewayIdPattern=").append(gatewayIdPattern)
				.append("]")
				.toString();
	}

	// fluent

	public TenantUpdateVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public TenantUpdateVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public TenantUpdateVO gatewayIdPattern(java.lang.String newGatewayIdPattern) {
		this.gatewayIdPattern = newGatewayIdPattern;
		return this;
	}

	// getter/setter

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
	}

	public java.lang.String getGatewayIdPattern() {
		return gatewayIdPattern;
	}

	public void setGatewayIdPattern(java.lang.String newGatewayIdPattern) {
		this.gatewayIdPattern = newGatewayIdPattern;
	}
}
