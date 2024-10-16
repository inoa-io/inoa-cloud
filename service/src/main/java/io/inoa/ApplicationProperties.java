package io.inoa;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

@Context
@ConfigurationProperties("inoa")
@Getter
@Setter
public class ApplicationProperties {

	/** Definition of the domain this inoa instance is running on. */
	private String domain = "domain";

}
