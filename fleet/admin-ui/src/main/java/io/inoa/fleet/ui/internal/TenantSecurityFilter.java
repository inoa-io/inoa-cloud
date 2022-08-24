package io.inoa.fleet.ui.internal;

import org.reactivestreams.Publisher;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;

/**
 * Filter to add tenant if no tenant was requested. TODO Remove when tenancy will be implemented
 *
 * @author Stephan Schnabel
 */
@Filter("/**")
public class TenantSecurityFilter implements HttpClientFilter {

	@Override
	public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
		request.getHeaders().add("x-tenant-id", "inoa");
		return chain.proceed(request);
	}
}
