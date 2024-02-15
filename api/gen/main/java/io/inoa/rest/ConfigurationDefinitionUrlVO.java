package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class ConfigurationDefinitionUrlVO extends ConfigurationDefinitionVO {


	@Override
	public ConfigurationTypeVO getType() {
		return ConfigurationTypeVO.URL;
	}

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		return super.equals(object);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash();
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("ConfigurationDefinitionUrlVO[")
				.append("super").append(super.toString())
				.append("]")
				.toString();
	}
}
