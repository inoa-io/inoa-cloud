package io.inoa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;

/**
 * Base for all micronaut tests.
 *
 * @author stephan.schnabel@grayc.de
 */
@Execution(ExecutionMode.CONCURRENT)
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.DisplayName.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractMicronautTest implements TestPropertyProvider {

	public final Logger log = LoggerFactory.getLogger(getClass());
	public @Inject ObjectMapper mapper;
	public @Inject Validator validator;

	@BeforeEach
	void log(TestInfo test) {
		log.info("Test: " + test.getTestMethod().orElse(null));
	}

	// lifecycle

	private static Map<String, String> properties;

	@Override
	public final Map<String, String> getProperties() {
		return properties == null ? properties = getSuiteProperties() : properties;
	}

	public Map<String, String> getSuiteProperties() {
		return Map.of();
	}

	// asserts

	public <T> T assertValid(T object) {
		if (object instanceof Iterable<?> iterable) {
			iterable.forEach(this::assertValid);
		} else if (object != null) {
			var violations = validator.validate(object);
			assertTrue(violations.isEmpty(),
					() -> "validation of object <" + object + "> failed with:" + violations.stream()
							.map(v -> "\n\t" + v.getPropertyPath() + ": " + v.getMessage())
							.collect(Collectors.joining()));
		}
		return object;
	}

	public static <T> void assertSorted(List<T> content, Function<T, Comparable<?>> toKey, Comparator<T> comparator) {
		try {
			var actual = content.stream().map(toKey).toList();
			var expected = content.stream()
					.sorted((o1, o2) -> {
						if (o1 == null || toKey.apply(o1) == null || o2 == null || toKey.apply(o2) == null) {
							return 0;
						}
						return comparator.compare(o1, o2);
					})
					.map(toKey).toList();
			assertEquals(expected, actual, "unsorted");
		} catch (RuntimeException e) {
			fail("Failed to assertSorted with content:\n "
					+ content.stream().map(Object::toString).collect(Collectors.joining("\n ")), e);
		}
	}
}
