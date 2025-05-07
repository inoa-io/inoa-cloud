package io.inoa.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base for all INOA junit tests.
 *
 * @author stephan.schnabel@grayc.de
 */
@Execution(ExecutionMode.CONCURRENT)
@TestClassOrder(ClassOrderer.DisplayName.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public abstract class AbstractTest {

	public final Logger log = LoggerFactory.getLogger(getClass());

	@BeforeEach
	void log(TestInfo test) {
		log.info("Test: " + test.getTestMethod().orElse(null));
	}

	public static <T> void assertSorted(
			List<T> content, Function<T, Comparable<?>> toKey, Comparator<T> comparator) {
		assertDoesNotThrow(
				() -> {
					var actual = content.stream().map(toKey).toList();
					var expected = content.stream()
							.sorted(
									(o1, o2) -> (o1 == null
											|| toKey.apply(o1) == null
											|| o2 == null
											|| toKey.apply(o2) == null)
													? 0
													: comparator.compare(o1, o2))
							.map(toKey)
							.toList();
					assertEquals(expected, actual, "unsorted");
				},
				() -> "Content:\n "
						+ content.stream().map(Object::toString).collect(Collectors.joining("\n ")));
	}
}
