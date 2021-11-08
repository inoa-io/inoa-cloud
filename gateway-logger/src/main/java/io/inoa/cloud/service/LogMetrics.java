package io.inoa.cloud.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

/**
 * Metrics wrapper for log events.
 *
 * @author Fabian Schlegel
 */
@Singleton
@RequiredArgsConstructor
public class LogMetrics {

	public static final String COUNTER_NAME_FAIL_TENANT_ID = "log_messages_fail_tenant_id_total";
	public static final String COUNTER_NAME_FAIL_GATEWAY_ID = "log_messages_fail_gateway_id_total";
	public static final String COUNTER_NAME_FAIL_MESSAGE_READ = "log_messages_fail_read_total";
	public static final String COUNTER_NAME_FAIL_MESSAGE_VALIDATE = "log_messages_fail_validate_total";
	public static final String COUNTER_NAME_IGNORE = "log_messages_ignore_total";
	public static final String COUNTER_NAME_SUCCESS = "log_messages_success_total";

	public static final String TAG_TENANT_ID = "tenantId";

	private final Map<String, Counter> counters = new HashMap<>();
	private final MeterRegistry meterRegistry;

	public Counter counterFailTenantId(String tenantId) {
		return counter(COUNTER_NAME_FAIL_TENANT_ID, Set.of(Tag.of(TAG_TENANT_ID, tenantId)));
	}

	public Counter counterFailGatewayId(String tenantId) {
		return counter(COUNTER_NAME_FAIL_GATEWAY_ID, Set.of(Tag.of(TAG_TENANT_ID, tenantId)));
	}

	public Counter counterFailMessageRead(String tenantId) {
		return counter(COUNTER_NAME_FAIL_MESSAGE_READ, Set.of(Tag.of(TAG_TENANT_ID, tenantId)));
	}

	public Counter counterFailMessageValidate(String tenantId) {
		return counter(COUNTER_NAME_FAIL_MESSAGE_VALIDATE, Set.of(Tag.of(TAG_TENANT_ID, tenantId)));
	}

	public Counter counterIgnore(String tenantId) {
		return counter(COUNTER_NAME_IGNORE, Set.of(Tag.of(TAG_TENANT_ID, tenantId)));
	}

	public Counter counterSuccess(String tenantId) {
		return counter(COUNTER_NAME_SUCCESS, Set.of(Tag.of(TAG_TENANT_ID, tenantId)));
	}

	private Counter counter(String counter, Set<Tag> tags) {
		return counters.computeIfAbsent(counter + tags, string -> meterRegistry.counter(counter, tags));
	}
}
