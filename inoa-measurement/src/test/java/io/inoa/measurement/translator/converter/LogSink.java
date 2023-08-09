package io.inoa.measurement.translator.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class LogSink {

	private static Map<Class<?>, List<ILoggingEvent>> LOGS = new HashMap<>();

	public static void setUp(Class<?> clazz) {
		var log = (Logger) LoggerFactory.getLogger(clazz);
		if (log.getAppender("test") == null) {
			var appender = new ListAppender<ILoggingEvent>();
			appender.setName("test");
			appender.start();
			appender.list = LOGS.computeIfAbsent(clazz, type -> new ArrayList<>());
			log.addAppender(appender);
		} else {
			LOGS.getOrDefault(clazz, List.of()).clear();
		}
	}

	public static void assertMessage(Class<?> clazz, String expectedMessage) {
		var logs = LOGS.getOrDefault(clazz, List.of());
		assertEquals(1, logs.size(), "log count");
		var log = logs.get(0);
		assertEquals(expectedMessage, log.getFormattedMessage(), "log message");
	}
}
