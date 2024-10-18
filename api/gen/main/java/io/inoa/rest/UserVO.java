package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class UserVO {

	public static final java.lang.String JSON_PROPERTY_FIRSTNAME = "firstname";
	public static final java.lang.String JSON_PROPERTY_LASTNAME = "lastname";
	public static final java.lang.String JSON_PROPERTY_EMAIL = "email";
	public static final java.lang.String JSON_PROPERTY_SESSION_EXPIRES = "session_expires";

	/** First name. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_FIRSTNAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String firstname;

	/** Last name. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 100)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_LASTNAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String lastname;

	/** Email. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 254)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_EMAIL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String email;

	/** When does the current session end? */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_SESSION_EXPIRES)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.time.Instant sessionExpires;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		UserVO other = (UserVO) object;
		return java.util.Objects.equals(firstname, other.firstname)
				&& java.util.Objects.equals(lastname, other.lastname)
				&& java.util.Objects.equals(email, other.email)
				&& java.util.Objects.equals(sessionExpires, other.sessionExpires);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(firstname, lastname, email, sessionExpires);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("UserVO[")
				.append("firstname=").append(firstname).append(",")
				.append("lastname=").append(lastname).append(",")
				.append("email=").append(email).append(",")
				.append("sessionExpires=").append(sessionExpires)
				.append("]")
				.toString();
	}

	// fluent

	public UserVO firstname(java.lang.String newFirstname) {
		this.firstname = newFirstname;
		return this;
	}

	public UserVO lastname(java.lang.String newLastname) {
		this.lastname = newLastname;
		return this;
	}

	public UserVO email(java.lang.String newEmail) {
		this.email = newEmail;
		return this;
	}

	public UserVO sessionExpires(java.time.Instant newSessionExpires) {
		this.sessionExpires = newSessionExpires;
		return this;
	}

	// getter/setter

	public java.lang.String getFirstname() {
		return firstname;
	}

	public void setFirstname(java.lang.String newFirstname) {
		this.firstname = newFirstname;
	}

	public java.lang.String getLastname() {
		return lastname;
	}

	public void setLastname(java.lang.String newLastname) {
		this.lastname = newLastname;
	}

	public java.lang.String getEmail() {
		return email;
	}

	public void setEmail(java.lang.String newEmail) {
		this.email = newEmail;
	}

	public java.time.Instant getSessionExpires() {
		return sessionExpires;
	}

	public void setSessionExpires(java.time.Instant newSessionExpires) {
		this.sessionExpires = newSessionExpires;
	}
}
