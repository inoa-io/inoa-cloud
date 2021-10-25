package io.inoa.fleet.auth;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@MicronautTest(transactional = false, environments = "h2")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractTest {
}
