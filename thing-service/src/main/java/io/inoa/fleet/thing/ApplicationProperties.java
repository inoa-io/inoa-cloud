package io.inoa.fleet.thing;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

@Context
@ConfigurationProperties("inoa")
@Getter
@Setter
public class ApplicationProperties {
	private SecurityProperties security = new SecurityProperties();

	@ConfigurationProperties("security")
	@Getter
	@Setter
	public static class SecurityProperties {

		/** Claim for tenant. */
		private String claimTenant = "tenant";
	}
}
