package io.inoa.controller.app;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.config.SecurityConfiguration;
import io.micronaut.security.oauth2.client.OpenIdProviderMetadata;
import io.micronaut.security.oauth2.configuration.OauthClientConfiguration;
import io.micronaut.security.oauth2.endpoint.endsession.request.EndSessionEndpoint;
import io.micronaut.security.oauth2.endpoint.endsession.request.EndSessionEndpointResolver;
import io.micronaut.security.oauth2.endpoint.endsession.request.OktaEndSessionEndpoint;
import io.micronaut.security.oauth2.endpoint.endsession.response.EndSessionCallbackUrlBuilder;
import io.micronaut.security.token.reader.TokenResolver;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.function.Supplier;

@Replaces(EndSessionEndpointResolver.class)
@Singleton
public class KeycloakEndSessionEndpointResolver extends EndSessionEndpointResolver {

  private final SecurityConfiguration securityConfiguration;
  private final TokenResolver<HttpRequest<?>> tokenResolver;

  KeycloakEndSessionEndpointResolver(
      BeanContext beanContext,
      SecurityConfiguration securityConfiguration,
      TokenResolver<HttpRequest<?>> tokenResolver) {
    super(beanContext);
    this.securityConfiguration = securityConfiguration;
    this.tokenResolver = tokenResolver;
  }

  @Override
  public Optional<EndSessionEndpoint> resolve(
      OauthClientConfiguration oauthClientConfiguration,
      Supplier<OpenIdProviderMetadata> openIdProviderMetadata,
      @SuppressWarnings("rawtypes") EndSessionCallbackUrlBuilder endSessionCallbackUrlBuilder) {
    // Okta is misleading here, Keycloak is used, but Okta implements correct OIDC like newer
    // Keycloak versions
    return Optional.of(
        new OktaEndSessionEndpoint(
            endSessionCallbackUrlBuilder,
            oauthClientConfiguration,
            openIdProviderMetadata,
            securityConfiguration,
            tokenResolver));
  }
}
