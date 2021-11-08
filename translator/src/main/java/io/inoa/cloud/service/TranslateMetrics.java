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
 * Metrics wrapper for translate.
 *
 * @author Stephan Schnabel
 */
@Singleton
@RequiredArgsConstructor
public class TranslateMetrics {

	public static final String COUNTER_NAME_FAIL_TENANT_ID = "translate_messages_fail_tenant_id_total";
	public static final String COUNTER_NAME_FAIL_GATEWAY_ID = "translate_messages_fail_gateway_id_total";
	public static final String COUNTER_NAME_FAIL_MESSAGE_READ = "translate_messages_fail_read_total";
	public static final String COUNTER_NAME_FAIL_MESSAGE_VALIDATE = "translate_messages_fail_validate_total";
	public static final String COUNTER_NAME_FAIL_CONVERTER = "translate_messages_fail_converter_total";
	public static final String COUNTER_NAME_FAIL_VALUE = "translate_messages_fail_value_total";
	public static final String COUNTER_NAME_IGNORE = "translate_messages_ignore_total";
	public static final String COUNTER_NAME_SUCCESS = "translate_messages_success_total";

	public static final String TAG_TENANT_ID = "tenantId";
	public static final String TAG_TYPE = "type";
	public static final String TAG_SENSOR = "sensor";

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

	public Counter counterFailConverter(String tenantId, String deviceType, String sensor) {
		return counter(COUNTER_NAME_FAIL_CONVERTER, Set.of(
				Tag.of(TAG_TENANT_ID, tenantId),
				Tag.of(TAG_TYPE, deviceType),
				Tag.of(TAG_SENSOR, sensor)));
	}

	public Counter counterFailValue(String tenantId, String deviceType, String sensor) {
		return counter(COUNTER_NAME_FAIL_VALUE, Set.of(
				Tag.of(TAG_TENANT_ID, tenantId),
				Tag.of(TAG_TYPE, deviceType),
				Tag.of(TAG_SENSOR, sensor)));
	}

	public Counter counterSuccess(String tenantId, String deviceType, String sensor) {
		return counter(COUNTER_NAME_SUCCESS, Set.of(
				Tag.of(TAG_TENANT_ID, tenantId),
				Tag.of(TAG_TYPE, deviceType),
				Tag.of(TAG_SENSOR, sensor)));
	}

	private Counter counter(String counter, Set<Tag> tags) {
		return counters.computeIfAbsent(counter + tags, string -> meterRegistry.counter(counter, tags));
	}
}
