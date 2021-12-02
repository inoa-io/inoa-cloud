package io.inoa.cnpm.tenant;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Getter;
import lombok.Setter;

/**
 * Properties for applicatinn.
 *
 * @author Rico Pahlisch
 * @author Stephan Schnabel
 */
@ConfigurationProperties("inoa.cnpm.tenant")
@Context
@Getter
@Setter
public class ApplicationProperties {

	private TokenExchangeProperties tokenExchange = new TokenExchangeProperties();

	/** Configuration for token exchange. */
	@ConfigurationProperties("token-exchange")
	@Getter
	@Setter
	public static class TokenExchangeProperties {

		/** HTTP header to get for tenant id from. */
		@NotNull
		private String httpHeader = "x-tenant-id";

		/** Issuer to add to generated tokens. */
		@NotNull
		private String issuer = UUID.randomUUID().toString();

		/** Key id to use for JWK */
		@NotNull
		private String keyId = UUID.randomUUID().toString();

		/** Resource path for RSA key to use. */
		private String keyPath;
	}
}
