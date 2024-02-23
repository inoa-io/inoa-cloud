package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class GatewayVO {

	public static final java.lang.String JSON_PROPERTY_GATEWAY_ID = "gateway_id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_CREATED = "created";
	public static final java.lang.String JSON_PROPERTY_UPDATED = "updated";
	public static final java.lang.String JSON_PROPERTY_STATUS = "status";

	/** Id as technical reference (never changes). */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[A-Z][A-Z0-9\\-_]{3,19}$")
	@jakarta.validation.constraints.Size(min = 4, max = 20)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_GATEWAY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String gatewayId;

	/** Human friendly description (can change). */
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** Flag if enabled or not. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Boolean enabled = true;

	/** Common timestamp for created/updated timestamps. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant created;

	/** Common timestamp for created/updated timestamps. */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_UPDATED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant updated;

	@jakarta.validation.Valid
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_STATUS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private GatewayStatusVO status;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		GatewayVO other = (GatewayVO) object;
		return java.util.Objects.equals(gatewayId, other.gatewayId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(created, other.created)
				&& java.util.Objects.equals(updated, other.updated)
				&& java.util.Objects.equals(status, other.status);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(gatewayId, name, enabled, created, updated, status);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("GatewayVO[")
				.append("gatewayId=").append(gatewayId).append(",")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("created=").append(created).append(",")
				.append("updated=").append(updated).append(",")
				.append("status=").append(status)
				.append("]")
				.toString();
	}

	// fluent

	public GatewayVO gatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
		return this;
	}

	public GatewayVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public GatewayVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public GatewayVO created(java.time.Instant newCreated) {
		this.created = newCreated;
		return this;
	}

	public GatewayVO updated(java.time.Instant newUpdated) {
		this.updated = newUpdated;
		return this;
	}

	public GatewayVO status(GatewayStatusVO newStatus) {
		this.status = newStatus;
		return this;
	}

	// getter/setter

	public java.lang.String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(java.lang.String newGatewayId) {
		this.gatewayId = newGatewayId;
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

	public GatewayStatusVO getStatus() {
		return status;
	}

	public void setStatus(GatewayStatusVO newStatus) {
		this.status = newStatus;
	}
}
