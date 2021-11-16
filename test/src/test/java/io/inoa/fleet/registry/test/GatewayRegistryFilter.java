package io.inoa.fleet.registry.test;

import org.reactivestreams.Publisher;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import lombok.Getter;
import lombok.Setter;

@Filter(serviceId = "gateway-registry")
public class GatewayRegistryFilter implements HttpClientFilter {

	@Getter
	@Setter
	private String tenantId;

	@Override
	public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
		return chain.proceed(request.header("x-tenant-id", tenantId));
	}
}
