package io.inoa.cnpm.tenant;

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

	/** HTTP header to use for tenant. */
	@NotNull
	private String httpHeader = "x-tenant-id";

	/** Issuer to add to generated tokens. */
	@NotNull
	private String issuer;

	/** Key id to use for JWK */
	@NotNull
	private String keyId;

	/** Private key path. */
	private String privateKey;
}
