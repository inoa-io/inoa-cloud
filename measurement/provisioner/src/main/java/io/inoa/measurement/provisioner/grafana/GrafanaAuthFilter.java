package io.inoa.measurement.provisioner.grafana;

import org.reactivestreams.Publisher;

import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.ClientFilterChain;
import io.micronaut.http.filter.HttpClientFilter;

@Context
@Filter(serviceId = "grafana")
public class GrafanaAuthFilter implements HttpClientFilter {

	private final String username;
	private final String password;

	GrafanaAuthFilter(@Value("${grafana.username}") String username, @Value("${grafana.password}") String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public Publisher<? extends HttpResponse<?>> doFilter(MutableHttpRequest<?> request, ClientFilterChain chain) {
		return chain.proceed(request.basicAuth(username, password));
	}
}
