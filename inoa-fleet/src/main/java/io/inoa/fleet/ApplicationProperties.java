package io.inoa.fleet;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import io.inoa.fleet.model.ConfigurationTypeVO;
import io.inoa.fleet.registry.domain.ConfigurationDefinition;
import io.inoa.fleet.registry.domain.Tenant;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

@Context
@ConfigurationProperties("inoa.fleet.registry")
@Getter
@Setter
public class ApplicationProperties {

	private SecurityProperties security = new SecurityProperties();
	private TenantProperties tenant = new TenantProperties();
	private GatewayProperties gateway = new GatewayProperties();

	/** Micronaut security related properties. */
	@ConfigurationProperties("security")
	@Getter
	@Setter
	public static class SecurityProperties {

		/** Claim for tenant. */
		private String claimTenant = "tenant";

		/** Audience whitelists. */
		private List<String> tenantAudienceWhitelist = new ArrayList<>();

		/** Header name for tenantId if audience is in whitelist. */
		private String tenantHeaderName = "x-tenant-id";
	}

	@ConfigurationProperties("gateway")
	@Getter
	@Setter
	public static class GatewayProperties {

		private GatewayTokenProperties token = new GatewayTokenProperties();

		/**
		 * Configuration for verifying gateway jwt's.
		 *
		 * @see "https://tools.ietf.org/html/rfc7523#section-3"
		 */
		@ConfigurationProperties("token")
		@Getter
		@Setter
		public static class GatewayTokenProperties {

			/** Expected value for claim <code>aud</code> (audience). */
			@NotNull
			private String audience = "gateway-registry";

			/** Force claim <code>jti</code> to identify jwt. */
			private boolean forceJwtId = true;

			/** Force claim <code>iat</code>. */
			private boolean forceIssuedAt = true;

			/** Force claim <code>nbf</code>. */
			private boolean forceNotBefore = true;

			/** Reject tokens with claim <code>iat</code> older than this threshold (prevents eternal token). */
			@NotNull
			private Optional<Duration> issuedAtThreshold = Optional.of(Duration.ofMinutes(15));
		}
	}

	/** Properties regarding tenants. */
	@ConfigurationProperties("tenant")
	@Getter
	@Setter
	public static class TenantProperties {

		private List<ConfigurationDefinitionDefault> configurations = new ArrayList<>();

		/** Represents a {@link ConfigurationDefinition} with default value. */
		@Getter
		@Setter
		public static class ConfigurationDefinitionDefault {

			@NotNull
			private String key;
			@NotNull
			private ConfigurationTypeVO type;
			@NotNull
			private String description;

			private Integer minimum;
			private Integer maximum;
			private String pattern;
			private String value;

			public ConfigurationDefinition toConfigurationDefinition(Tenant tenant) {
				return new ConfigurationDefinition()
						.setTenant(tenant)
						.setKey(key)
						.setDescription(description)
						.setType(type)
						.setMinimum(minimum)
						.setMaximum(maximum)
						.setPattern(pattern);
			}
		}
	}
}
