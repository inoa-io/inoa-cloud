package io.kokuwa.fleet.registry.rest;

import static io.kokuwa.fleet.registry.HttpResponseAssertions.assert204;
import static io.kokuwa.fleet.registry.HttpResponseAssertions.assert401;
import static io.kokuwa.fleet.registry.HttpResponseAssertions.assert404;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.kokuwa.fleet.registry.domain.GatewayPropertyRepository;
import io.kokuwa.fleet.registry.rest.gateway.PropertiesApiTestClient;
import io.kokuwa.fleet.registry.rest.gateway.PropertiesApiTestSpec;
import io.kokuwa.fleet.registry.test.AbstractUnitTest;
import io.kokuwa.fleet.registry.test.Data;

/**
 * Test for {@link PropertiesController}.
 *
 * @author Stephan Schnabel
 */
@DisplayName("gateway: properties")
public class PropertiesControllerTest extends AbstractUnitTest implements PropertiesApiTestSpec {

	@Inject
	PropertiesApiTestClient client;
	@Inject
	GatewayPropertyRepository repository;

	@DisplayName("getProperties(200): with properties")
	@Test
	@Override
	public void getProperties200() {
		var bearer = auth.bearer(Data.GATEWAY_1_UUID);
		var properties = assert200(() -> client.getProperties(bearer));
		assertEquals(Data.GATEWAY_1_PROPERTIES, properties, "properties");
	}

	@DisplayName("getProperties(200): without properties")
	@Test
	public void getProperties200Without() {
		var bearer = auth.bearer(Data.GATEWAY_2_UUID);
		var properties = assert200(() -> client.getProperties(bearer));
		assertTrue(Data.GATEWAY_2_PROPERTIES.isEmpty(), "expected");
		assertEquals(Data.GATEWAY_2_PROPERTIES, properties, "properties");
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
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		setProperties(bearer, Map.of("aaa", "a", "ccc", "c"));
		var expectedProperties = Map.of("aaa", "x", "bbb", "b", "ccc", "c");
		var actualProperties = assert200(() -> client.setProperties(bearer, Map.of("aaa", "x", "bbb", "b")));
		assertEquals(expectedProperties, actualProperties, "properties from response");
		assertProperties(bearer, expectedProperties);
	}

	@DisplayName("setProperties(200): empty payload")
	@Test
	public void setProperties200EmptyPayload() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		var properties = Map.of("aaa", "a");
		setProperties(bearer, properties);
		var actualProperties = assert200(() -> client.setProperties(bearer, Map.of()));
		assertEquals(properties, actualProperties, "properties from response");
		assertProperties(bearer, properties);
	}

	@DisplayName("setProperties(200): no change")
	@Test
	public void setProperties200NoChange() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		var properties = Map.of("aaa", "a", "ccc", "c");
		setProperties(bearer, properties);
		var actualProperties = assert200(() -> client.setProperties(bearer, properties));
		assertEquals(properties, actualProperties, "properties from response");
		assertProperties(bearer, properties);
	}

	@DisplayName("setProperties(401): no token")
	@Test
	@Override
	public void setProperties401() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		setProperties(bearer, Map.of());
		assert401(() -> client.setProperties(null, Map.of("aaa", "a")));
		assertProperties(bearer, Map.of());
	}

	@DisplayName("setProperty(204): insert key")
	@Test
	@Override
	public void setProperty204() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		setProperties(bearer, Map.of("aaa", "a", "ccc", "c"));
		assert204(() -> client.setProperty(bearer, "bbb", "b"));
		assertProperties(bearer, Map.of("aaa", "a", "bbb", "b", "ccc", "c"));
	}

	@DisplayName("setProperty(204): update key")
	@Test
	public void setProperty204Update() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		setProperties(bearer, Map.of("aaa", "a", "ccc", "c"));
		assert204(() -> client.setProperty(bearer, "aaa", "x"));
		assertProperties(bearer, Map.of("aaa", "x", "ccc", "c"));
	}

	@DisplayName("setProperty(204): no change")
	@Test
	public void setProperty204NoChange() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		var properties = Map.of("aaa", "a", "ccc", "c");
		setProperties(bearer, properties);
		assert204(() -> client.setProperty(bearer, "aaa", "a"));
		assertProperties(bearer, properties);
	}

	@DisplayName("setProperty(401): no token")
	@Test
	@Override
	public void setProperty401() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		setProperties(bearer, Map.of());
		assert401(() -> client.setProperty(null, "aaa", "a"));
		assertProperties(bearer, Map.of());
	}

	@DisplayName("deleteProperty(204): delete key")
	@Test
	@Override
	public void deleteProperty204() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		setProperties(bearer, Map.of("aaa", "a", "bbb", "b", "ccc", "c"));
		assert204(() -> client.deleteProperty(bearer, "bbb"));
		assertProperties(bearer, Map.of("aaa", "a", "ccc", "c"));
	}

	@DisplayName("deleteProperty(401): no token")
	@Test
	@Override
	public void deleteProperty401() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		var properties = Map.of("aaa", "a");
		setProperties(bearer, properties);
		assert401(() -> client.deleteProperty(null, "aaa"));
		assertProperties(bearer, properties);
	}

	@DisplayName("deleteProperty(404): key not found")
	@Test
	@Override
	public void deleteProperty404() {
		var bearer = auth.bearer(Data.GATEWAY_3_UUID);
		var properties = Map.of("aaa", "a");
		setProperties(bearer, properties);
		assert404(() -> client.deleteProperty(bearer, "bbb"));
		assertProperties(bearer, properties);
	}

	// internal

	private void setProperties(String bearer, Map<String, String> properties) {
		repository.deleteByGateway(Data.GATEWAY_3).blockingAwait();
		if (!properties.isEmpty()) {
			assert200(() -> client.setProperties(bearer, properties));
		}
	}

	private void assertProperties(String bearer, Map<String, String> expected) {
		assertEquals(expected, assert200(() -> client.getProperties(bearer)), "properties");
	}
}
