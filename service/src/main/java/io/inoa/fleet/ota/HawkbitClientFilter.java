package io.inoa.fleet.ota;

import org.reactivestreams.Publisher;

import io.inoa.fleet.ApplicationProperties;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;
import lombok.RequiredArgsConstructor;

@Filter(serviceId = "hawkbit")
@RequiredArgsConstructor
public class HawkbitClientFilter implements HttpClientFilter {

	private final ApplicationProperties properties;

	@Override
	public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
		return chain
				.proceed(request.basicAuth(properties.getHawkbit().getUser(), properties.getHawkbit().getPassword()));
	}
}
