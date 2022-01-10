package io.inoa.test.client;

import java.util.Map;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Data;

/**
 * Client for echo-that is secured with envoy.
 */
@Singleton
public class EchoClient {

	@Inject
	@Client("echo")
	HttpClient client;

	public HttpResponse<EchoResponse> call(String tenantId, String token) {
		return call(Map.of("x-tenant-id", tenantId, HttpHeaders.AUTHORIZATION, token));
	}

	private HttpResponse<EchoResponse> call(Map<CharSequence, CharSequence> headers) {
		return client.toBlocking().exchange(HttpRequest.GET("/get").headers(headers), EchoResponse.class);
	}

	@Data
	public static class EchoResponse {

		private Map<String, String> headers;

		public String getTenantId() {
			return headers.get("X-Tenant-Id");
		}

		public String getAuthorization() {
			return headers.get(HttpHeaders.AUTHORIZATION);
		}
	}
}
