package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class RpcResponseErrorVO {

	public static final java.lang.String JSON_PROPERTY_CODE = "code";
	public static final java.lang.String JSON_PROPERTY_MESSAGE = "message";

	/** Error code */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CODE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer code;

	/** Error message */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MESSAGE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String message;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		RpcResponseErrorVO other = (RpcResponseErrorVO) object;
		return java.util.Objects.equals(code, other.code)
				&& java.util.Objects.equals(message, other.message);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(code, message);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("RpcResponseErrorVO[")
				.append("code=").append(code).append(",")
				.append("message=").append(message)
				.append("]")
				.toString();
	}

	// fluent

	public RpcResponseErrorVO code(java.lang.Integer newCode) {
		this.code = newCode;
		return this;
	}

	public RpcResponseErrorVO message(java.lang.String newMessage) {
		this.message = newMessage;
		return this;
	}

	// getter/setter

	public java.lang.Integer getCode() {
		return code;
	}

	public void setCode(java.lang.Integer newCode) {
		this.code = newCode;
	}

	public java.lang.String getMessage() {
		return message;
	}

	public void setMessage(java.lang.String newMessage) {
		this.message = newMessage;
	}
}
