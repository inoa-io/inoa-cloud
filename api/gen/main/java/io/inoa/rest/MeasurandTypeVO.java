package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class MeasurandTypeVO {

	public static final java.lang.String JSON_PROPERTY_OBIS_ID = "obis_id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";

	/** The OBIS code */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Pattern(regexp = "^((?<a>[0-9]{1,3})-)?((?<b>[0-9]{1,3}):)?(S\\.)?(?<cde>(?<c>[0-9A-F]{1,3}).(?<d>[0-9A-F]{1,3})(.(?<e>[0-9A-F]{1,3}))?)([\\*\\&](?<f>[0-9A-F]{1,3}))?$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_OBIS_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String obisId;

	/** Name of the measurand type */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Size(max = 256)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Description of the measurand type */
	@jakarta.validation.constraints.Size(max = 4096)
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
		return java.util.Objects.equals(obisId, other.obisId)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(obisId, name, description);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("MeasurandTypeVO[")
				.append("obisId=").append(obisId).append(",")
				.append("name=").append(name).append(",")
				.append("description=").append(description)
				.append("]")
				.toString();
	}

	// fluent

	public MeasurandTypeVO obisId(java.lang.String newObisId) {
		this.obisId = newObisId;
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

	public java.lang.String getObisId() {
		return obisId;
	}

	public void setObisId(java.lang.String newObisId) {
		this.obisId = newObisId;
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
