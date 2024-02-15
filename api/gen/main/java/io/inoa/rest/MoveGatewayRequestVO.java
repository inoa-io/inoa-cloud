package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class MoveGatewayRequestVO {

	public static final java.lang.String JSON_PROPERTY_SOURCE_TENANT_ID = "source_tenant_id";
	public static final java.lang.String JSON_PROPERTY_TARGET_TENANT_ID = "target_tenant_id";
	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";

	/** Id as tenant reference. */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Size(min = 1, max = 30)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_SOURCE_TENANT_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String sourceTenantId;

	/** Id as tenant reference. */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Size(min = 1, max = 30)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TARGET_TENANT_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String targetTenantId;

	/** Id as technical reference (never changes). */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
	@javax.validation.constraints.Size(min = 4, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String gatewayId;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		MoveGatewayRequestVO other = (MoveGatewayRequestVO) object;
		return java.util.Objects.equals(sourceTenantId, other.sourceTenantId)
				&& java.util.Objects.equals(targetTenantId, other.targetTenantId)
				&& java.util.Objects.equals(gatewayId, other.gatewayId);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(sourceTenantId, targetTenantId, gatewayId);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("MoveGatewayRequestVO[")
				.append("sourceTenantId=").append(sourceTenantId).append(",")
				.append("targetTenantId=").append(targetTenantId).append(",")
				.append("gatewayId=").append(gatewayId)
				.append("]")
				.toString();
	}

	// fluent

	public MoveGatewayRequestVO sourceTenantId(java.lang.String newSourceTenantId) {
		this.sourceTenantId = newSourceTenantId;
		return this;
	}

	public MoveGatewayRequestVO targetTenantId(java.lang.String newTargetTenantId) {
		this.targetTenantId = newTargetTenantId;
		return this;
	}

	public MoveGatewayRequestVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	// getter/setter

	public java.lang.String getSourceTenantId() {
		return sourceTenantId;
	}

	public void setSourceTenantId(java.lang.String newSourceTenantId) {
		this.sourceTenantId = newSourceTenantId;
	}

	public java.lang.String getTargetTenantId() {
		return targetTenantId;
	}

	public void setTargetTenantId(java.lang.String newTargetTenantId) {
		this.targetTenantId = newTargetTenantId;
	}

	public java.lang.String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
	}
}
