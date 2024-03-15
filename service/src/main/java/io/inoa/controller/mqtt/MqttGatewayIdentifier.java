package io.inoa.controller.mqtt;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.slf4j.MDC;

/**
 * Parse tenant and gateway from username.
 *
 * @author Stephan Schnabel
 * @param tenantId  Tenant ID, e.g. inoa.
 * @param gatewayId Gateway ID, e.g. GW-0001.
 * @see "https:www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#usernamepassword"
 */
public record MqttGatewayIdentifier(String tenantId, String gatewayId) {

	private static final Pattern PATTERN = Pattern.compile("^"
			+ "(?<gatewayId>[A-Z][A-Z0-9\\-_]{3,19})@"
			+ "(?<tenantId>[a-zA-Z0-9-]{3,20})$");

	public static MqttGatewayIdentifier of(String username) {

		var matcher = PATTERN.matcher(username);
		if (!matcher.matches()) {
			return null;
		}

		var tenantId = matcher.group("tenantId");
		var gatewayId = matcher.group("gatewayId");

		return new MqttGatewayIdentifier(tenantId, gatewayId);
	}

	public void mdc(Consumer<MqttGatewayIdentifier> consumer) {
		try {
			MDC.put("tenantId", tenantId);
			MDC.put("gatewayId", gatewayId);
			consumer.accept(this);
		} finally {
			MDC.remove("tenantId");
			MDC.remove("gatewayId");
		}
	}
}
