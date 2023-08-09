package io.inoa.fleet.mqtt;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaHeader {

	public String TENANT_ID = "tenant_id";
	public String DEVICE_ID = "device_id";
	public String CONTENT_TYPE = "content-type";
	public String CONTENT_TYPE_JSON = "application/json";
	public String CONTENT_TYPE_EVENT_DC = "application/vnd.eclipse-hono-dc-notification+json";
	public String CREATION_TIME = "creation-time";
	public String QOS = "qos";
	public String ORIG_ADDRESS = "orig_address";
}
