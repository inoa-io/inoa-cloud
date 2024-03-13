package io.inoa.controller.app;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reactivestreams.Publisher;

import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.ConfigurationInterceptUrlMapRule;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.rules.SecurityRuleResult;
import io.micronaut.web.router.resource.StaticResourceConfiguration;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * Allow unauthenticated serving of local resources
 *
 * @author stephan.schnabel@grayc.de
 */
@Singleton
@Slf4j
public class AppSecurityRule implements SecurityRule<HttpRequest<?>> {

	private final Set<String> cache = new HashSet<>(Set.of("/", "/index.html"));
	private final ResourceLoader loader;

	AppSecurityRule(List<StaticResourceConfiguration> configurations) {
		this.loader = configurations.stream().flatMap(c -> c.getResourceLoaders().stream()).findAny().get();
	}

	@Override
	public int getOrder() {
		return ConfigurationInterceptUrlMapRule.ORDER - 1;
	}

	@Override
	public Publisher<SecurityRuleResult> check(HttpRequest<?> request, Authentication authentication) {
		return Publishers.just(isRequestAllowed(request) ? SecurityRuleResult.ALLOWED : SecurityRuleResult.UNKNOWN);
	}

	private boolean isRequestAllowed(HttpRequest<?> request) {

		if (request.getMethod() != HttpMethod.GET) {
			return false;
		}

		var requestPath = request.getPath();
		if (cache.contains(requestPath)) {
			log.trace("Request path {} allowed because of cache", requestPath);
			return true;
		}

		if (loader.getResource(requestPath).isPresent()) {
			cache.add(requestPath);
			log.debug("Request path {} found in loader and added to cache", requestPath);
			return true;
		}

		log.trace("Request path {} not found", requestPath);
		return false;
	}
}
