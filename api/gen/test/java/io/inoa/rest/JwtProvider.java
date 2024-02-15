package io.inoa.rest;

import io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.context.annotation.Requires(beans = io.micronaut.security.token.jwt.signature.SignatureGeneratorConfiguration.class)
@jakarta.inject.Singleton
public class JwtProvider {

	private final SignatureGeneratorConfiguration signature;

	public JwtProvider(SignatureGeneratorConfiguration signature) {
		this.signature = signature;
	}

	public JwtBuilder builder() {
		return new JwtBuilder(signature);
	}

	public String bearer(String subject, String... roles) {
		return new JwtBuilder(signature).subject(subject).roles(roles).toBearer();
	}
}
