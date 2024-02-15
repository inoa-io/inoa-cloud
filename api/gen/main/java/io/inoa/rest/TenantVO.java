package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class TenantVO {

	public static final java.lang.String JSON_PROPERTY_TENANT_ID = "tenant_id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID_PATTERN = "gateway_id_pattern";
	public static final java.lang.String JSON_PROPERTY_CREATED = "created";
	public static final java.lang.String JSON_PROPERTY_UPDATED = "updated";
	public static final java.lang.String JSON_PROPERTY_DELETED = "deleted";

	/** Id as tenant reference. */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Size(min = 1, max = 30)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TENANT_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String tenantId;

	/** Name of a tenant. */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\s]*$")
	@javax.validation.constraints.Size(min = 3, max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Is tenant enabled */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Boolean enabled;

	/** Regular expression to force specific gateway IDs for this tenant */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID_PATTERN)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String gatewayIdPattern;

	/** Common timestamp for created/updated timestamps. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant created;

	/** Common timestamp for created/updated timestamps. */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_UPDATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant updated;

	/** Common timestamp for created/updated timestamps. */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DELETED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.time.Instant deleted;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		TenantVO other = (TenantVO) object;
		return java.util.Objects.equals(tenantId, other.tenantId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(gatewayIdPattern, other.gatewayIdPattern)
				&& java.util.Objects.equals(created, other.created)
				&& java.util.Objects.equals(updated, other.updated)
				&& java.util.Objects.equals(deleted, other.deleted);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(tenantId, name, enabled, gatewayIdPattern, created, updated, deleted);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("TenantVO[")
				.append("tenantId=").append(tenantId).append(",")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("gatewayIdPattern=").append(gatewayIdPattern).append(",")
				.append("created=").append(created).append(",")
				.append("updated=").append(updated).append(",")
				.append("deleted=").append(deleted)
				.append("]")
				.toString();
	}

	// fluent

	public TenantVO tenantId(java.lang.String newTenantId) {
		this.tenantId = newTenantId;
		return this;
	}

	public TenantVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public TenantVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public TenantVO gatewayIdPattern(java.lang.String newGatewayIdPattern) {
		this.gatewayIdPattern = newGatewayIdPattern;
		return this;
	}

	public TenantVO created(java.time.Instant newCreated) {
		this.created = newCreated;
		return this;
	}

	public TenantVO updated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
		return this;
	}

	public TenantVO deleted(java.time.Instant newDeleted) {
		this.deleted = newDeleted;
		return this;
	}

	// getter/setter

	public java.lang.String getTenantId() {
		return tenantId;
	}

	public void setTenantId(java.lang.String newTenantId) {
		this.tenantId = newTenantId;
	}

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

	public java.time.Instant getCreated() {
		return created;
	}

	public void setCreated(java.time.Instant newCreated) {
		this.created = newCreated;
	}

	public java.time.Instant getUpdated() {
		return updated;
	}

	public void setUpdated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
	}

	public java.time.Instant getDeleted() {
		return deleted;
	}

	public void setDeleted(java.time.Instant newDeleted) {
		this.deleted = newDeleted;
	}
}
