package org.eclipse.hawkbit.model;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class DistributionSetCreationResponsePartVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_DESCRIPTION = "description";
	public static final java.lang.String JSON_PROPERTY_CREATED_BY = "createdBy";
	public static final java.lang.String JSON_PROPERTY_CREATED_AT = "createdAt";
	public static final java.lang.String JSON_PROPERTY_LAST_MODIFIED_BY = "lastModifiedBy";
	public static final java.lang.String JSON_PROPERTY_LAST_MODIFIED_AT = "lastModifiedAt";
	public static final java.lang.String JSON_PROPERTY_REQUIRED_MIGRATION_STEP = "requiredMigrationStep";
	public static final java.lang.String JSON_PROPERTY_TYPE = "type";
	public static final java.lang.String JSON_PROPERTY_MODULES = "modules";

	/** The technical identifier of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer id;

	/** The name of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String name;

	/** The description of the entity */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_DESCRIPTION)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String description;

	/** Entity was originally created by User, AMQP-Controller, anonymous etc.) */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREATED_BY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String createdBy;

	/** Entity was originally created at (timestamp UTC in milliseconds) */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_CREATED_AT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer createdAt;

	/** Entity was last modified by User, AMQP-Controller, anonymous etc.) */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_LAST_MODIFIED_BY)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String lastModifiedBy;

	/** Entity was last modified at (timestamp UTC in milliseconds) */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_LAST_MODIFIED_AT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer lastModifiedAt;

	/** True if DS is a required migration step for another DS. As a result the DS’s assignment will not be cancelled when another DS is assigned (updatable only if DS is not yet assigned to a target)  */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_REQUIRED_MIGRATION_STEP)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Boolean requiredMigrationStep;

	/** The type of the distribution set */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
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
		DistributionSetCreationResponsePartVO other = (DistributionSetCreationResponsePartVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(description, other.description)
				&& java.util.Objects.equals(createdBy, other.createdBy)
				&& java.util.Objects.equals(createdAt, other.createdAt)
				&& java.util.Objects.equals(lastModifiedBy, other.lastModifiedBy)
				&& java.util.Objects.equals(lastModifiedAt, other.lastModifiedAt)
				&& java.util.Objects.equals(requiredMigrationStep, other.requiredMigrationStep)
				&& java.util.Objects.equals(type, other.type)
				&& java.util.Objects.equals(modules, other.modules);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name, description, createdBy, createdAt, lastModifiedBy, lastModifiedAt, requiredMigrationStep, type, modules);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("DistributionSetCreationResponsePartVO[")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("description=").append(description).append(",")
				.append("createdBy=").append(createdBy).append(",")
				.append("createdAt=").append(createdAt).append(",")
				.append("lastModifiedBy=").append(lastModifiedBy).append(",")
				.append("lastModifiedAt=").append(lastModifiedAt).append(",")
				.append("requiredMigrationStep=").append(requiredMigrationStep).append(",")
				.append("type=").append(type).append(",")
				.append("modules=").append(modules)
				.append("]")
				.toString();
	}

	// fluent

	public DistributionSetCreationResponsePartVO id(java.lang.Integer newId) {
		this.id = newId;
		return this;
	}

	public DistributionSetCreationResponsePartVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public DistributionSetCreationResponsePartVO description(java.lang.String newDescription) {
		this.description = newDescription;
		return this;
	}

	public DistributionSetCreationResponsePartVO createdBy(java.lang.String newCreatedBy) {
		this.createdBy = newCreatedBy;
		return this;
	}

	public DistributionSetCreationResponsePartVO createdAt(java.lang.Integer newCreatedAt) {
		this.createdAt = newCreatedAt;
		return this;
	}

	public DistributionSetCreationResponsePartVO lastModifiedBy(java.lang.String newLastModifiedBy) {
		this.lastModifiedBy = newLastModifiedBy;
		return this;
	}

	public DistributionSetCreationResponsePartVO lastModifiedAt(java.lang.Integer newLastModifiedAt) {
		this.lastModifiedAt = newLastModifiedAt;
		return this;
	}

	public DistributionSetCreationResponsePartVO requiredMigrationStep(java.lang.Boolean newRequiredMigrationStep) {
		this.requiredMigrationStep = newRequiredMigrationStep;
		return this;
	}

	public DistributionSetCreationResponsePartVO type(java.lang.String newType) {
		this.type = newType;
		return this;
	}

	public DistributionSetCreationResponsePartVO modules(java.util.List<DistributionSetModuleVO> newModules) {
		this.modules = newModules;
		return this;
	}
	
	public DistributionSetCreationResponsePartVO addModulesItem(DistributionSetModuleVO modulesItem) {
		if (this.modules == null) {
			this.modules = new java.util.ArrayList<>();
		}
		this.modules.add(modulesItem);
		return this;
	}

	public DistributionSetCreationResponsePartVO removeModulesItem(DistributionSetModuleVO modulesItem) {
		if (this.modules != null) {
			this.modules.remove(modulesItem);
		}
		return this;
	}

	// getter/setter

	public java.lang.Integer getId() {
		return id;
	}

	public void setId(java.lang.Integer newId) {
		this.id = newId;
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

	public java.lang.String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(java.lang.String newCreatedBy) {
		this.createdBy = newCreatedBy;
	}

	public java.lang.Integer getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.lang.Integer newCreatedAt) {
		this.createdAt = newCreatedAt;
	}

	public java.lang.String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(java.lang.String newLastModifiedBy) {
		this.lastModifiedBy = newLastModifiedBy;
	}

	public java.lang.Integer getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(java.lang.Integer newLastModifiedAt) {
		this.lastModifiedAt = newLastModifiedAt;
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
