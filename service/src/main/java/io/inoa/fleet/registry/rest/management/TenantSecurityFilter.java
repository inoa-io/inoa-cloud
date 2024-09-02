package io.inoa.fleet.registry.rest.management;

import org.reactivestreams.Publisher;

import io.inoa.fleet.FleetProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpHeaders;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import lombok.RequiredArgsConstructor;

/**
 * Filter to add tenant if no tenant was requested. TODO Remove when tenancy will be implemented
 *
 * @author Stephan Schnabel
 */
@Requires(notEnv = Environment.TEST)
@Filter("/**")
@RequiredArgsConstructor
public class TenantSecurityFilter implements HttpServerFilter {

	private final FleetProperties properties;

	@Override
	public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
		var header = properties.getSecurity().getTenantHeaderName();
		if (request.getHeaders().get(header) == null) {
			((MutableHttpHeaders) request.getHeaders()).add(header, "inoa");
		}
		return chain.proceed(request);
	}
}
