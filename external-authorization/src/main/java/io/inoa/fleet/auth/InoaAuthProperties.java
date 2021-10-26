package io.inoa.fleet.auth;

import javax.validation.constraints.NotNull;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import lombok.Data;

@Context
@ConfigurationProperties("inoa.auth")
@Data
public class InoaAuthProperties {

	@NotNull
	private String issuer;
	@NotNull
	private String tenantHeader = "x-inoa-tenant";
	private String privateKey;
	@NotNull
	private String keyId;
}
