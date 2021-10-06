package io.inoa.cloud.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.slf4j.MDC;

import io.inoa.cloud.converter.ValueConverter;
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
			+ ":(?<sensor>[a-zA-Z0-9\\-\\:\\*]{2,64})");

	private final Set<ValueConverter> converters;
	private final TranslateMetrics metrics;

	public Optional<InoaTelemetryMessageVO> toInoa(
			UUID tenantId,
			UUID gatewayId,
			HonoTelemetryMessageVO hono) {

		// parse urn

		var matcher = URN_PATTERN.matcher(hono.getUrn());
		if (!matcher.matches()) {
			log.error("Unsupported urn {}.", hono.getUrn());
			return Optional.empty();
		}
		var deviceType = matcher.group("deviceType");
		var deviceId = matcher.group("deviceId");
		var sensor = matcher.group("sensor");
		MDC.put("deviceType", deviceType);
		MDC.put("deviceId", deviceId);
		MDC.put("sensor", sensor);

		// get converter

		var valueConverter = converters.stream()
				.sorted(Comparator.comparing(Ordered::getOrder))
				.filter(converter -> converter.supports(deviceType, sensor))
				.findFirst();
		if (valueConverter.isEmpty()) {
			log.warn("Device type {} with sensor {} is unsupported.", deviceType, sensor);
			metrics.counterFailConverter(tenantId, deviceType, sensor).increment();
			return Optional.empty();
		}

		// map message value

		var value = valueConverter.flatMap(converter -> converter.convert(hono));
		if (value.isEmpty()) {
			log.warn("Failed on value: {}", new String(hono.getValue()));
			metrics.counterFailValue(tenantId, deviceType, sensor).increment();
			return Optional.empty();
		}

		return Optional.of(new InoaTelemetryMessageVO()
				.setTenantId(tenantId)
				.setGatewayId(gatewayId)
				.setUrn(hono.getUrn())
				.setDeviceType(deviceType)
				.setDeviceId(deviceId)
				.setSensor(sensor)
				.setTimestamp(Instant.ofEpochMilli(hono.getTimestamp()))
				.setValue(value.get())
				.setExt(hono.getExt()));
	}
}
