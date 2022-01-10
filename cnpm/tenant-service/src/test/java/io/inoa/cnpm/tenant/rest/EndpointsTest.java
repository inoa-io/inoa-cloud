package io.inoa.cnpm.tenant.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.inoa.cnpm.tenant.AbstractTest;
import io.micronaut.http.HttpRequest;

/**
 * Test for custom endpoints.
 */
@DisplayName("endpoints")
public class EndpointsTest extends AbstractTest {

	@DisplayName("openapi")
	@Test
	void openapi() {
		assert200(() -> rawClient.toBlocking().exchange(HttpRequest.GET("/openapi/spec.yaml"), String.class));
	}
}
