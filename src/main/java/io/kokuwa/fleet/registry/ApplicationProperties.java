package io.kokuwa.fleet.registry;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Properties for application.
 *
 * @author Stephan Schnabel
 */
@ConfigurationProperties("registry")
@Getter
@Setter
public class ApplicationProperties {

	private GatewayProperties gateway = new GatewayProperties();
	private RegistryAuthProperties auth = new RegistryAuthProperties();

	/** URI of configuration server. */
	@NotNull
	private URI configUri;

	/** Type of configuration server. */
	@NotNull
	private String configType;

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
