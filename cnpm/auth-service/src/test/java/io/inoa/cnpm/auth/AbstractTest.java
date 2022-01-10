package io.inoa.cnpm.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.inoa.cnpm.auth.data.Data;
import io.inoa.cnpm.auth.data.Security;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

/**
 * Abstract test.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
@MicronautTest
public abstract class AbstractTest {

	@Inject
	public ApplicationProperties properties;
	@Inject
	public Security security;
	@Inject
	public Data data;

	@BeforeEach
	void reset() {
		data.clear();
	}
}
