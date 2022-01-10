package io.inoa.cnpm.tenant;

import java.net.URL;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

/**
 * Properties for applicatinn.
 */
@ConfigurationProperties("inoa.cnpm.tenant")
@Context
@Getter
@Setter
public class ApplicationProperties {

	private IssuerDefault defaultIssuer = new IssuerDefault();
	/** If jwt contains containing this audience request without user context is allowed. */
	private String serviceAudience = "tenant-management";

	/** Configuration for tenant issuer defaults. */
	@ConfigurationProperties("default-issuer")
	@Getter
	@Setter
	public static class IssuerDefault {

		/** Default issuer name for new tenants. */
		@NotNull
		private String name = "default";

		/** Default issuer url for new tenants. */
		@NotNull
		private URL url;

		/** Default cache duration for issuers. */
		@NotNull
		private Duration cacheDuration = Duration.ofMinutes(5);

		/** Default services. */
		@NotNull
		private Set<String> services = new HashSet<>();
	}
}
