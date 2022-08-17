package io.inoa.fleet.mqtt;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.slf4j.MDC;

/**
 * Parse tenant and gateway from username.
 *
 * @author Stephan Schnabel
 * @param tenantId  Tenant ID, e.g. inoa.
 * @param gatewayId Gateway ID, a uuid.
 * @see "https:www.eclipse.org/hono/docs/user-guide/mqtt-adapter/#usernamepassword"
 */
public record Gateway(String tenantId, UUID gatewayId) {

	private static final Pattern PATTERN = Pattern.compile("^"
			+ "(?<gatewayId>[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})@"
			+ "(?<tenantId>[a-zA-Z0-9-]{3,20})$");

	public static Gateway of(String username) {

		var matcher = PATTERN.matcher(username);
		if (!matcher.matches()) {
			return null;
		}

		var tenantId = matcher.group("tenantId");
		var gatewayId = UUID.fromString(matcher.group("gatewayId"));

		return new Gateway(tenantId, gatewayId);
	}

	public void mdc(Consumer<Gateway> consumer) {
		try {
			MDC.put("tenantId", tenantId);
			MDC.put("gatewayId", gatewayId.toString());
			consumer.accept(this);
		} finally {
			MDC.remove("tenantId");
			MDC.remove("gatewayId");
		}
	}
}
