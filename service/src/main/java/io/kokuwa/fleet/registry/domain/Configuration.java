package io.kokuwa.fleet.registry.domain;

/**
 * Configuration value for {@link ConfigurationDefinition}.
 *
 * @author Stephan Schnabel
 */
public interface Configuration {

	ConfigurationDefinition getDefinition();

	String getValue();
}
