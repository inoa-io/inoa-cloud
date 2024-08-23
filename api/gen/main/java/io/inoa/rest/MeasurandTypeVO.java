package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class MeasurandTypeVO {

	public static final java.lang.String JSON_PROPERTY_OBIS_CODE = "obis_code";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";

	/** The OBIS code of the measurand */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_OBIS_CODE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String obisCode;

	/** Human readable name of the type. */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9\\-\\_]{1,64}$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Longer description of the type. */
	@jakarta.validation.constraints.Size(max = 1024)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		MeasurandTypeVO other = (MeasurandTypeVO) object;
		return java.util.Objects.equals(obisCode, other.obisCode)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(obisCode, name, description);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("MeasurandTypeVO[")
				.append("obisCode=").append(obisCode).append(",")
				.append("name=").append(name).append(",")
				.append("description=").append(description)
				.append("]")
				.toString();
	}

	// fluent

	public MeasurandTypeVO obisCode(java.lang.String newObisCode) {
		this.obisCode = newObisCode;
		return this;
	}

	public MeasurandTypeVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public MeasurandTypeVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	// getter/setter

	public java.lang.String getObisCode() {
		return obisCode;
	}

	public void setObisCode(java.lang.String newObisCode) {
		this.obisCode = newObisCode;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String newDescription) {
		this.description = newDescription;
	}
}
