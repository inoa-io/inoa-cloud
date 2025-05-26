package io.inoa.fleet;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import io.inoa.fleet.registry.domain.Configuration;
import io.inoa.fleet.registry.domain.ConfigurationDefinition;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

@Context
@ConfigurationProperties("inoa.fleet")
@Getter
@Setter
public class FleetProperties {

	private SecurityProperties security = new SecurityProperties();
	private TenantProperties tenant = new TenantProperties();
	private GatewayProperties gateway = new GatewayProperties();
	private HawkbitProperties hawkbit = new HawkbitProperties();

	/** Micronaut security related properties. */
	@ConfigurationProperties("security")
	@Getter
	@Setter
	public static class SecurityProperties {

		/** Claim for tenant. */
		private String claimTenants = "tenants";

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

			/**
			 * Reject tokens with claim <code>iat</code> older than this threshold (prevents eternal
			 * token).
			 */
			@NotNull
			private Optional<Duration> issuedAtThreshold = Optional.of(Duration.ofMinutes(15));
		}
	}

	/** Properties regarding tenants. */
	@ConfigurationProperties("tenant")
	@Getter
	@Setter
	public static class TenantProperties {

		private List<DefaultConfiguration> configurations = new ArrayList<>();

		/** Represents a {@link Configuration} with default value. */
		@Getter
		@Setter
		public static class DefaultConfiguration implements Configuration {

			@NotNull
			private ConfigurationDefinition definition;
			private String value;
		}
	}

	@ConfigurationProperties("hawkbit")
	@Getter
	@Setter
	public static class HawkbitProperties {

		/** User for Hawkbit Management UI */
		@NotNull
		private String user = "admin";

		/** Password for Hawkbit Management UI */
		@NotNull
		private String password = "admin";
	}
}
