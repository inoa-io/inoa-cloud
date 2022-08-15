package io.inoa.measurement.translator.service;

import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.MDC;

import io.inoa.fleet.telemetry.TelemetryRawVO;
import io.inoa.measurement.telemetry.TelemetryVO;
import io.inoa.measurement.translator.converter.Converter;
import io.micronaut.core.order.Ordered;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to translate messages from fleet to messages in measurement.
 */
@Singleton
@Slf4j
@RequiredArgsConstructor
public class TranslateService {

	private static Pattern URN_PATTERN = Pattern.compile("urn"
			+ ":(?<deviceType>[a-zA-Z0-9\\-]{2,32})"
			+ ":(?<deviceId>[a-zA-Z0-9\\-]{2,36})"
			+ ":(?<sensor>[a-zA-Z0-9_\\-\\:\\*]{2,64})");

	private final Set<Converter> converters;
	private final TranslateMetrics metrics;

	public List<TelemetryVO> translate(String tenantId, UUID gatewayId, TelemetryRawVO raw) {

		// parse urn

		var matcher = URN_PATTERN.matcher(raw.getUrn());
		if (!matcher.matches()) {
			log.error("Unsupported urn {}.", raw.getUrn());
			return List.of();
		}
		var deviceType = matcher.group("deviceType");
		var deviceId = matcher.group("deviceId");
		var sensor = matcher.group("sensor");
		MDC.put("deviceType", deviceType);
		MDC.put("deviceId", deviceId);
		MDC.put("sensor", sensor);

		// get converter

		var converter = converters.stream()
				.sorted(Comparator.comparing(Ordered::getOrder))
				.filter(c -> c.supports(deviceType, sensor))
				.findFirst();
		if (converter.isEmpty()) {
			log.warn("Device type {} with sensor {} is unsupported.", deviceType, sensor);
			metrics.counterFailConverter(tenantId, deviceType, sensor).increment();
			return List.of();
		}

		// map message value

		var messages = converter.get().convert(raw, deviceType, sensor)
				.peek(target -> fillMessage(raw, target, tenantId, gatewayId, deviceType, deviceId, sensor))
				.toList();
		if (messages.isEmpty()) {
			log.warn("Failed on value: {}", Base64.getEncoder().encode(raw.getValue()));
			metrics.counterFailValue(tenantId, deviceType, sensor).increment();
			return List.of();
		}

		return messages;
	}

	private void fillMessage(
			TelemetryRawVO raw,
			TelemetryVO telemetry,
			String tenantId,
			UUID gatewayId,
			String deviceType,
			String deviceId,
			String sensor) {

		telemetry.setTenantId(tenantId);
		telemetry.setGatewayId(gatewayId);

		if (telemetry.getUrn() == null) {
			telemetry.setUrn(raw.getUrn());
		}
		if (telemetry.getDeviceId() == null) {
			telemetry.setDeviceId(deviceId);
		}
		if (telemetry.getDeviceType() == null) {
			telemetry.setDeviceType(deviceType);
		}
		if (telemetry.getSensor() == null) {
			telemetry.setSensor(sensor);
		}
		if (telemetry.getTimestamp() == null) {
			telemetry.setTimestamp(Instant.ofEpochMilli(raw.getTimestamp()));
		}

		if (raw.getExt() != null) {
			if (raw.getExt() != null) {
				telemetry.setExt(new HashMap<>());
			}
			raw.getExt().forEach(telemetry.getExt()::putIfAbsent);
		}
	}
}
