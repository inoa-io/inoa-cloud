package io.inoa.measurement.translator;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@MicronautTest
public abstract class AbstractTest {}
