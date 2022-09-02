package io.inoa.fleet.registry;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaHeader {

	public final String TENANT_ID = "tenant_id";
	public final String DEVICE_ID = "device_id";
	public final String CONTENT_TYPE = "content-type";
	public final String CONTENT_TYPE_JSON = "application/json";
	public final String CONTENT_TYPE_EVENT_DC = "application/vnd.eclipse-hono-dc-notification+json";
	public final String CREATION_TIME = "creation-time";
	public final String QOS = "qos";
	public final String ORIG_ADDRESS = "orig_address";
}
