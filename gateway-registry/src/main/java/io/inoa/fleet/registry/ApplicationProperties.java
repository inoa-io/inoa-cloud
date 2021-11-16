package io.inoa.fleet.registry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

/**
 * Properties for application.
 *
 * @author Stephan Schnabel
 */
@Context
@ConfigurationProperties("registry")
@Getter
@Setter
public class ApplicationProperties {

	private SecurityProperties security = new SecurityProperties();
	private RegistryAuthProperties auth = new RegistryAuthProperties();
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

	/** Configuration for issuing registry jwt's. */
	@ConfigurationProperties("auth")
	@Getter
	@Setter
	public static class RegistryAuthProperties {

		/** Value for <code>aud</code> claim. */
		@NotNull
		private String audience = "gateway";

		/** Value for <code>iss</code> claim. */
		@NotNull
		private String issuer;

		/** Duration for <code>exp</code> claim. */
		@NotNull
		private Duration expirationDuration = Duration.ofMinutes(5);

		/** List of RSA keys to use for registry token. */
		@NotNull
		private List<RegistryAuthRSA> keys = new ArrayList<>();

		/** Generate key if no key was defined. */
		private boolean generateKey = false;

		@Getter
		@Setter
		public class RegistryAuthRSA {

			@NotNull
			private String id;
			@NotNull
			private byte[] publicKey;
			@NotNull
			private byte[] privateKey;
		}
	}

	@ConfigurationProperties("gateway")
	@Getter
	@Setter
	public static class GatewayProperties {

		private GatewayTokenProperties token = new GatewayTokenProperties();

		/** Credentials authId to use for checking gateway jwt's. */
		@NotNull
		private String credentialAuthId = "registry";

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
}
