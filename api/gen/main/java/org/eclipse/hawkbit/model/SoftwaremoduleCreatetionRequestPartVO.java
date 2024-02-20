package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class SoftwaremoduleCreatetionRequestPartVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_VERSION = "version";
	public static final java.lang.String JSON_PROPERTY_VENDOR = "vendor";
	public static final java.lang.String JSON_PROPERTY_TYPE = "type";

	/** The name of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** The description of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** Package version */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VERSION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String version;

	/** The software vendor */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VENDOR)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String vendor;

	/** The type of the software module identified by its key */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String type;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		SoftwaremoduleCreatetionRequestPartVO other = (SoftwaremoduleCreatetionRequestPartVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(version, other.version)
				&& java.util.Objects.equals(vendor, other.vendor)
				&& java.util.Objects.equals(type, other.type);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, description, version, vendor, type);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("SoftwaremoduleCreatetionRequestPartVO[")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("version=").append(version).append(",")
				.append("vendor=").append(vendor).append(",")
				.append("type=").append(type)
				.append("]")
				.toString();
	}

	// fluent

	public SoftwaremoduleCreatetionRequestPartVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public SoftwaremoduleCreatetionRequestPartVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public SoftwaremoduleCreatetionRequestPartVO version(java.lang.String newVersion) {
		this.version = newVersion;
		return this;
	}

	public SoftwaremoduleCreatetionRequestPartVO vendor(java.lang.String newVendor) {
		this.vendor = newVendor;
		return this;
	}

	public SoftwaremoduleCreatetionRequestPartVO type(java.lang.String newType) {
		this.type = newType;
		return this;
	}

	// getter/setter

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

	public java.lang.String getVersion() {
		return version;
	}

	public void setVersion(java.lang.String newVersion) {
		this.version = newVersion;
	}

	public java.lang.String getVendor() {
		return vendor;
	}

	public void setVendor(java.lang.String newVendor) {
		this.vendor = newVendor;
	}

	public java.lang.String getType() {
		return type;
	}

	public void setType(java.lang.String newType) {
		this.type = newType;
	}
}
