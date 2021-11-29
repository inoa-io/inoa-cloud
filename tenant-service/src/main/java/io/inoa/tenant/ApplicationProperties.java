package io.inoa.tenant;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Properties for applicatinn.
 *
 * @author Rico Pahlisch
 * @author Stephan Schnabel
 */
@ConfigurationProperties("tenant")
@Getter
@Setter
public class ApplicationProperties {

	private String issuer;

	private String httpHeader = "x-tenant-id";

	private String keyId;

	private String privateKey;
}
