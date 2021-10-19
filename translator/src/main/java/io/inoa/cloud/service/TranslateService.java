package io.inoa.cloud.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.slf4j.MDC;

import io.inoa.cloud.converter.Converter;
import io.inoa.cloud.messages.InoaTelemetryMessageVO;
import io.inoa.hono.messages.HonoTelemetryMessageVO;
import io.micronaut.core.order.Ordered;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to translate messages from hono to messages in inoa.
 *
 * @author Stephan Schnabel
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

	public List<InoaTelemetryMessageVO> toInoa(
			UUID tenantId,
			UUID gatewayId,
			HonoTelemetryMessageVO hono) {

		// parse urn

		var matcher = URN_PATTERN.matcher(hono.getUrn());
		if (!matcher.matches()) {
			log.error("Unsupported urn {}.", hono.getUrn());
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

		var messages = converter.get().convert(hono, deviceType, sensor)
				.peek(inoa -> fillMessage(hono, inoa, tenantId, gatewayId, deviceType, deviceId, sensor))
				.collect(Collectors.toList());
		if (messages.isEmpty()) {
			log.warn("Failed on value: {}", new String(hono.getValue()));
			metrics.counterFailValue(tenantId, deviceType, sensor).increment();
			return List.of();
		}

		return messages;
	}

	private void fillMessage(
			HonoTelemetryMessageVO hono,
			InoaTelemetryMessageVO inoa,
			UUID tenantId,
			UUID gatewayId,
			String deviceType,
			String deviceId,
			String sensor) {

		inoa.setTenantId(tenantId);
		inoa.setGatewayId(gatewayId);

		if (inoa.getUrn() == null) {
			inoa.setUrn(hono.getUrn());
		}
		if (inoa.getDeviceId() == null) {
			inoa.setDeviceId(deviceId);
		}
		if (inoa.getDeviceType() == null) {
			inoa.setDeviceType(deviceType);
		}
		if (inoa.getSensor() == null) {
			inoa.setSensor(sensor);
		}
		if (inoa.getTimestamp() == null) {
			inoa.setTimestamp(Instant.ofEpochMilli(hono.getTimestamp()));
		}

		if (hono.getExt() != null) {
			if (hono.getExt() != null) {
				inoa.setExt(new HashMap<>());
			}
			hono.getExt().forEach(inoa.getExt()::putIfAbsent);
		}
	}
}
