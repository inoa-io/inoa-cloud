package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class RpcCommandVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_METHOD = "method";
	public static final java.lang.String JSON_PROPERTY_PARAMS = "params";

	/** ID of the command */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String id;

	/** TODO */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_METHOD)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String method;

	/** Parameters of the command as JSON object */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_PARAMS)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Object params;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		RpcCommandVO other = (RpcCommandVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(method, other.method)
				&& java.util.Objects.equals(params, other.params);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, method, params);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("RpcCommandVO[")
				.append("id=").append(id).append(",")
				.append("method=").append(method).append(",")
				.append("params=").append(params)
				.append("]")
				.toString();
	}

	// fluent

	public RpcCommandVO id(java.lang.String newId) {
		this.id = newId;
		return this;
	}

	public RpcCommandVO method(java.lang.String newMethod) {
		this.method = newMethod;
		return this;
	}

	public RpcCommandVO params(java.lang.Object newParams) {
		this.params = newParams;
		return this;
	}

	// getter/setter

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String newId) {
		this.id = newId;
	}

	public java.lang.String getMethod() {
		return method;
	}

	public void setMethod(java.lang.String newMethod) {
		this.method = newMethod;
	}

	public java.lang.Object getParams() {
		return params;
	}

	public void setParams(java.lang.Object newParams) {
		this.params = newParams;
	}
}
