package io.inoa.fleet.mqtt;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.inject.Singleton;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Metrics wrapper for MQTT events.
 *
 * @author Fabian Schlegel
 */
@Singleton
@RequiredArgsConstructor
public class MqttMetrics {
	public static final String COUNTER_NAME_LOGIN_ERROR = "mqtt_login_fail_total";

	public static final String TAG_TENANT_ID = "tenantId";

	private final Map<String, Counter> counters = new HashMap<>();
	private final MeterRegistry meterRegistry;

	/**
	 * Increment metric for failed login attempts.
	 *
	 * @param tenantId affected tenant
	 * @return corresponding counter
	 */
	public Counter counterLoginError(String tenantId) {
		return counter(COUNTER_NAME_LOGIN_ERROR, Set.of(Tag.of(TAG_TENANT_ID, tenantId)));
	}

	private Counter counter(String counter, Set<Tag> tags) {
		return counters.computeIfAbsent(counter + tags, string -> meterRegistry.counter(counter, tags));
	}
}
