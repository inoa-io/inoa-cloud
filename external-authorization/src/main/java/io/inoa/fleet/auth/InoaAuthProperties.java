package io.inoa.fleet.auth;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Data;

@Data
@ConfigurationProperties("inoa.auth")
public class InoaAuthProperties {
	private String issuer;
	private String tenantHeader = "x-inoa-tenant";
	private String privateKey;
	private String keyId = "test";
}
