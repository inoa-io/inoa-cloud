package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class RpcResponseVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_ERROR = "error";
	public static final java.lang.String JSON_PROPERTY_RESULT = "result";

	/** ID of the command */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String id;

	@jakarta.validation.Valid
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ERROR)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private RpcResponseErrorVO error;

	/** Result of the command as JSON object */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_RESULT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Object result;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		RpcResponseVO other = (RpcResponseVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(error, other.error)
				&& java.util.Objects.equals(result, other.result);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, error, result);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("RpcResponseVO[")
				.append("id=").append(id).append(",")
				.append("error=").append(error).append(",")
				.append("result=").append(result)
				.append("]")
				.toString();
	}

	// fluent

	public RpcResponseVO id(java.lang.String newId) {
		this.id = newId;
		return this;
	}

	public RpcResponseVO error(RpcResponseErrorVO newError) {
		this.error = newError;
		return this;
	}

	public RpcResponseVO result(java.lang.Object newResult) {
		this.result = newResult;
		return this;
	}

	// getter/setter

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String newId) {
		this.id = newId;
	}

	public RpcResponseErrorVO getError() {
		return error;
	}

	public void setError(RpcResponseErrorVO newError) {
		this.error = newError;
	}

	public java.lang.Object getResult() {
		return result;
	}

	public void setResult(java.lang.Object newResult) {
		this.result = newResult;
	}
}
