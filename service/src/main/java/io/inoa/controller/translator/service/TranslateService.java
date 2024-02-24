package io.inoa.controller.translator.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.MDC;

import io.inoa.controller.translator.converter.Converter;
import io.inoa.rest.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micronaut.cache.CacheManager;
import io.micronaut.cache.SyncCache;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.order.Ordered;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to translate messages from fleet to messages in measurement.
 */
@Singleton
@Slf4j
public class TranslateService {

	private static Pattern URN_PATTERN = Pattern.compile("urn"
			+ ":(?<deviceType>[a-zA-Z0-9\\-]{2,32})"
			+ ":(?<deviceId>[a-zA-Z0-9\\-]{2,36})"
			+ ":(?<sensor>[a-zA-Z0-9_\\-\\:\\*]{2,64})");

	private final Set<Converter> converters;
	private final TranslateMetrics metrics;
	private final SyncCache<?> cache;
	private final int increaseThreshold;

	public TranslateService(
			Set<Converter> converters,
			TranslateMetrics metrics,
			CacheManager<?> cacheManager,
			@Value("${inoa.measurement.translator.increaseThreshold:100000}") int increaseThreshold) {
		this.converters = converters;
		this.metrics = metrics;
		this.cache = cacheManager.getCache("watthour");
		this.increaseThreshold = increaseThreshold;
	}

	public List<TelemetryVO> translate(String tenantId, String gatewayId, TelemetryRawVO raw) {

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
				.collect(Collectors.toList());
		if (messages.isEmpty()) {
			log.warn("Failed on value: {}", Base64.getEncoder().encode(raw.getValue()));
			metrics.counterFailValue(tenantId, deviceType, sensor).increment();
			return List.of();
		}

		messages.removeIf(this::isInvalid);

		return messages;
	}

	private void fillMessage(
			TelemetryRawVO raw,
			TelemetryVO telemetry,
			String tenantId,
			String gatewayId,
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

	private boolean isInvalid(TelemetryVO vo) {

		if (vo.getExt() == null || !"watthour".equals(vo.getExt().get("unit"))) {
			return false;
		}

		var valid = true;
		var urn = vo.getUrn();
		var old = cache.get(urn, TelemetryVO.class).orElse(null);

		if (old == null) {
			log.trace("URN {} had no cached value", urn);
		} else {

			var oldTime = old.getTimestamp();
			var newTime = vo.getTimestamp();
			var diffTime = Duration.between(oldTime, newTime);
			var newValue = vo.getValue();
			var oldValue = old.getValue();
			var diffValue = newValue - oldValue;

			if (diffTime.isNegative()) {
				log.warn("URN {} got out of order value (old {}, new {}), skip validation.", urn, oldTime, newTime);
				return true;
			}

			if (diffValue < 0) {
				log.warn("URN {} had wh decreased from {} to {} Wh (diff {} Wh).",
						urn, oldValue, newValue, diffValue);
				valid = false;
			} else {
				// normalized to 1h
				var increase = diffValue / (diffTime.getSeconds() / 3_600D);
				if (increase > increaseThreshold) {
					log.warn("URN {} increased by {} Wh ({} Wh in {}), skip value bigger than thresold {} Wh.",
							urn, increase, diffValue, diffTime, increaseThreshold);
					valid = false;
				} else {
					log.trace("URN {} increased by {} Wh ({} Wh in {}).", urn, increase, diffValue, diffTime);
				}
			}
		}

		if (valid) {
			cache.put(urn, vo);
		}

		return !valid;
	}
}
