package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class DistributionSetCreationRequestPartVO {

	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_VERSION = "version";
	public static final java.lang.String JSON_PROPERTY_REQUIRED_MIGRATION_STEP = "requiredMigrationStep";
	public static final java.lang.String JSON_PROPERTY_TYPE = "type";
	public static final java.lang.String JSON_PROPERTY_MODULES = "modules";

	/** The name of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** The description of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String description;

	/** Package version */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_VERSION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String version;

	/** True if DS is a required migration step for another DS. As a result the DS’s assignment will not be cancelled when another DS is assigned (updatable only if DS is not yet assigned to a target)  */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_REQUIRED_MIGRATION_STEP)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean requiredMigrationStep;

	/** The type of the distribution set */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String type;

	/** Software Modules of this distribution set */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MODULES)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.util.List<DistributionSetModuleVO> modules;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		DistributionSetCreationRequestPartVO other = (DistributionSetCreationRequestPartVO) object;
		return java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(version, other.version)
				&& java.util.Objects.equals(requiredMigrationStep, other.requiredMigrationStep)
				&& java.util.Objects.equals(type, other.type)
				&& java.util.Objects.equals(modules, other.modules);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(name, description, version, requiredMigrationStep, type, modules);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("DistributionSetCreationRequestPartVO[")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("version=").append(version).append(",")
				.append("requiredMigrationStep=").append(requiredMigrationStep).append(",")
				.append("type=").append(type).append(",")
				.append("modules=").append(modules)
				.append("]")
				.toString();
	}

	// fluent

	public DistributionSetCreationRequestPartVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public DistributionSetCreationRequestPartVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public DistributionSetCreationRequestPartVO version(java.lang.String newVersion) {
		this.version = newVersion;
		return this;
	}

	public DistributionSetCreationRequestPartVO requiredMigrationStep(java.lang.Boolean newRequiredMigrationStep) {
		this.requiredMigrationStep = newRequiredMigrationStep;
		return this;
	}

	public DistributionSetCreationRequestPartVO type(java.lang.String newType) {
		this.type = newType;
		return this;
	}

	public DistributionSetCreationRequestPartVO modules(java.util.List<DistributionSetModuleVO> newModules) {
		this.modules = newModules;
		return this;
	}
	
	public DistributionSetCreationRequestPartVO addModulesItem(DistributionSetModuleVO modulesItem) {
		if (this.modules == null) {
			this.modules = new java.util.ArrayList<>();
		}
		this.modules.add(modulesItem);
		return this;
	}

	public DistributionSetCreationRequestPartVO removeModulesItem(DistributionSetModuleVO modulesItem) {
		if (this.modules != null) {
			this.modules.remove(modulesItem);
		}
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

	public java.lang.Boolean getRequiredMigrationStep() {
		return requiredMigrationStep;
	}

	public void setRequiredMigrationStep(java.lang.Boolean newRequiredMigrationStep) {
		this.requiredMigrationStep = newRequiredMigrationStep;
	}

	public java.lang.String getType() {
		return type;
	}

	public void setType(java.lang.String newType) {
		this.type = newType;
	}

	public java.util.List<DistributionSetModuleVO> getModules() {
		return modules;
	}

	public void setModules(java.util.List<DistributionSetModuleVO> newModules) {
		this.modules = newModules;
	}
}
