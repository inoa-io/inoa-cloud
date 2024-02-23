package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class DistributionSetModuleVO {

	/** Technical Id of the software module */
	@com.fasterxml.jackson.annotation.JsonProperty("id")
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer id;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		DistributionSetModuleVO other = (DistributionSetModuleVO) object;
		return java.util.Objects.equals(id, other.id);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("DistributionSetModuleVO[")
				.append("id=").append(id)
				.append("]")
				.toString();
	}

	// fluent

	public DistributionSetModuleVO id(java.lang.Integer newId) {
		this.id = newId;
		return this;
	}

	// getter/setter

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer newId) {
		this.id = newId;
	}
}
