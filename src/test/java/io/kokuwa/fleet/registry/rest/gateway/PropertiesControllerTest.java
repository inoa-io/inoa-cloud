package io.kokuwa.fleet.registry.rest.gateway;

import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert204;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert401;
import static io.kokuwa.fleet.registry.rest.HttpResponseAssertions.assert404;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.AbstractTest;
import io.kokuwa.fleet.registry.domain.Gateway;

/**
 * Test for {@link PropertiesController}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("gateway: properties")
public class PropertiesControllerTest extends AbstractTest implements PropertiesApiTestSpec {

	@Inject
	PropertiesApiTestClient client;

	@DisplayName("getProperties(200): with properties")
	@Test
	@Override
	public void getProperties200() {
		var expectedProperties = Map.of("aaa", "a", "ccc", "c");
		var gateway = data.gateway(data.tenant(), expectedProperties);
		var actualProperties = assert200(() -> client.getProperties(bearer(gateway)));
		assertProperties(gateway, expectedProperties, actualProperties);
	}

	@DisplayName("getProperties(200): without properties")
	@Test
	public void getProperties200Without() {
		var expectedProperties = Map.<String, String>of();
		var gateway = data.gateway(data.tenant(), expectedProperties);
		var actualProperties = assert200(() -> client.getProperties(bearer(gateway)));
		assertProperties(gateway, expectedProperties, actualProperties);
	}

	@DisplayName("getProperties(401): no token")
	@Test
	@Override
	public void getProperties401() {
		assert401(() -> client.getProperties(null));
	}

	@DisplayName("setProperties(200): insert/update")
	@Test
	@Override
	public void setProperties200() {
		var gateway = data.gateway(data.tenant(), Map.of("aaa", "a", "ccc", "c"));
		var expectedProperties = Map.of("aaa", "x", "bbb", "b", "ccc", "c");
		var actualProperties = assert200(
				() -> client.setProperties(bearer(gateway), Map.of("aaa", "x", "bbb", "b")));
		assertProperties(gateway, expectedProperties, actualProperties);
	}

	@DisplayName("setProperties(200): empty payload")
	@Test
	public void setProperties200EmptyPayload() {
		var expectedProperties = Map.of("aaa", "a");
		var gateway = data.gateway(data.tenant(), expectedProperties);
		var actualProperties = assert200(() -> client.setProperties(bearer(gateway), Map.of()));
		assertProperties(gateway, expectedProperties, actualProperties);
	}

	@DisplayName("setProperties(200): no change")
	@Test
	public void setProperties200NoChange() {
		var expectedProperties = Map.of("aaa", "a", "ccc", "c");
		var gateway = data.gateway(data.tenant(), expectedProperties);
		var actualProperties = assert200(() -> client.setProperties(bearer(gateway), expectedProperties));
		assertProperties(gateway, expectedProperties, actualProperties);
	}

	@DisplayName("setProperties(401): no token")
	@Test
	@Override
	public void setProperties401() {
		var gateway = data.gateway(data.tenant());
		assert401(() -> client.setProperties(null, Map.of("aaa", "a")));
		assertProperties(gateway, Map.of());
	}

	@DisplayName("setProperty(204): insert key")
	@Test
	@Override
	public void setProperty204() {
		var gateway = data.gateway(data.tenant(), Map.of("aaa", "a", "ccc", "c"));
		var expectedProperties = Map.of("aaa", "a", "bbb", "b", "ccc", "c");
		assert204(() -> client.setProperty(bearer(gateway), "bbb", "b"));
		assertProperties(gateway, expectedProperties);
	}

	@DisplayName("setProperty(204): update key")
	@Test
	public void setProperty204Update() {
		var gateway = data.gateway(data.tenant(), Map.of("aaa", "a", "ccc", "c"));
		var expectedProperties = Map.of("aaa", "x", "ccc", "c");
		assert204(() -> client.setProperty(bearer(gateway), "aaa", "x"));
		assertProperties(gateway, expectedProperties);
	}

	@DisplayName("setProperty(204): no change")
	@Test
	public void setProperty204NoChange() {
		var expectedProperties = Map.of("aaa", "a", "ccc", "c");
		var gateway = data.gateway(data.tenant(), expectedProperties);
		assert204(() -> client.setProperty(bearer(gateway), "aaa", "a"));
		assertProperties(gateway, expectedProperties);
	}

	@DisplayName("setProperty(401): no token")
	@Test
	@Override
	public void setProperty401() {
		var expectedProperties = Map.of("aaa", "a", "ccc", "c");
		var gateway = data.gateway(data.tenant(), expectedProperties);
		assert401(() -> client.setProperty(null, "aaa", "x"));
		assertProperties(gateway, expectedProperties);
	}

	@DisplayName("deleteProperty(204): delete key")
	@Test
	@Override
	public void deleteProperty204() {
		var expectedProperties = Map.of("aaa", "a", "bbb", "b", "ccc", "c");
		var gateway = data.gateway(data.tenant(), expectedProperties);
		assert204(() -> client.deleteProperty(bearer(gateway), "bbb"));
		assertProperties(gateway, Map.of("aaa", "a", "ccc", "c"));
	}

	@DisplayName("deleteProperty(401): no token")
	@Test
	@Override
	public void deleteProperty401() {
		var expectedProperties = Map.of("aaa", "a", "ccc", "c");
		var gateway = data.gateway(data.tenant(), expectedProperties);
		assert401(() -> client.deleteProperty(null, "aaa"));
		assertProperties(gateway, expectedProperties);
	}

	@DisplayName("deleteProperty(404): key not found")
	@Test
	@Override
	public void deleteProperty404() {
		var expectedProperties = Map.of("aaa", "a", "ccc", "c");
		var gateway = data.gateway(data.tenant(), expectedProperties);
		assert404(() -> client.deleteProperty(bearer(gateway), "bbb"));
		assertProperties(gateway, expectedProperties);
	}

	// internal

	private void assertProperties(Gateway gateway, Map<String, String> expected) {
		var actual = data.findProperties(gateway).stream().collect(Collectors.toMap(
				p -> p.getPk().getKey(),
				p -> p.getValue()));
		assertEquals(expected, actual, "database");
	}

	private void assertProperties(Gateway gateway, Map<String, String> expected, Map<String, String> actual) {
		assertEquals(expected, actual, "rest");
		assertProperties(gateway, expected);
	}
}
