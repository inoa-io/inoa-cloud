package io.inoa.cloud;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Properties for gateway logger.
 *
 * @author Fabian Schlegel
 */
@ConfigurationProperties("logger")
@Getter
@Setter
public class ApplicationProperties {
}
