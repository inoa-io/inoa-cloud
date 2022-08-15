package io.inoa.test.infrastructure;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.test.client.KeycloakClient;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Base for all integration tests.
 */
@MicronautTest(startApplication = false)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractTest {

	public static final String EMAIL_SUFFIX = "@example.org";
	public static final String INOA_ADMIN = "admin";

	@Inject
	public TestAssertions assertions;
	@Inject
	public KeycloakClient keycloak;
}
