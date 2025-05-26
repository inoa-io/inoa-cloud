package io.inoa.messaging;

/**
 * Header used for Kafka. Should be compliant to Hono spec.
 *
 * @author stephan.schnabel@grayc.de
 */
public class KafkaHeader {

	public static final String TENANT_ID = "tenant_id";
	public static final String DEVICE_ID = "device_id";
	public static final String CONTENT_TYPE = "content-type";
	public static final String CONTENT_TTD = "ttd";
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_EVENT_DC = "application/vnd.eclipse-hono-dc-notification+json";
	public static final String CONTENT_TYPE_EMPTY_NOTIFICATION = "application/vnd.eclipse-hono-empty-notification";
	public static final String CREATION_TIME = "creation-time";
	public static final String QOS = "qos";
	public static final String ORIG_ADDRESS = "orig_address";
}
