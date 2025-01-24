package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.serde.annotation.Serdeable
public class DatapointVO {

	public static final java.lang.String JSON_PROPERTY_ID = "id";
	public static final java.lang.String JSON_PROPERTY_NAME = "name";
	public static final java.lang.String JSON_PROPERTY_ENABLED = "enabled";
	public static final java.lang.String JSON_PROPERTY_INTERVAL = "interval";
	public static final java.lang.String JSON_PROPERTY_TYPE = "type";
	public static final java.lang.String JSON_PROPERTY_INTERFACE = "interface";
	public static final java.lang.String JSON_PROPERTY_TIMEOUT = "timeout";
	public static final java.lang.String JSON_PROPERTY_FRAME = "frame";
	public static final java.lang.String JSON_PROPERTY_URI = "uri";

	/** Unique identifier */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ID)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String id;

	/** Readable name */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_NAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String name;

	/** Is datapoint enabled */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_ENABLED)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Boolean enabled;

	/** Poll interval in Seconds */
	@jakarta.validation.constraints.NotNull
	@jakarta.validation.constraints.Min(0)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_INTERVAL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Integer interval;

	/** Poll type */
	@jakarta.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TYPE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private Type type;

	/** Interface to poll */
	@jakarta.validation.constraints.Min(0)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_INTERFACE)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer _interface;

	/** Polling timeout */
	@jakarta.validation.constraints.Min(0)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TIMEOUT)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.Integer timeout;

	/** RS485 frame as hex string */
	@jakarta.validation.constraints.Pattern(regexp = "^[a-fA-F0-9]+$")
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_FRAME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.lang.String frame;

	/** URI to poll */
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_URI)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	private java.net.URI uri;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		DatapointVO other = (DatapointVO) object;
		return java.util.Objects.equals(id, other.id)
				&& java.util.Objects.equals(name, other.name)
				&& java.util.Objects.equals(enabled, other.enabled)
				&& java.util.Objects.equals(interval, other.interval)
				&& java.util.Objects.equals(type, other.type)
				&& java.util.Objects.equals(_interface, other._interface)
				&& java.util.Objects.equals(timeout, other.timeout)
				&& java.util.Objects.equals(frame, other.frame)
				&& java.util.Objects.equals(uri, other.uri);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(id, name, enabled, interval, type, _interface, timeout, frame, uri);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("DatapointVO[")
				.append("id=").append(id).append(",")
				.append("name=").append(name).append(",")
				.append("enabled=").append(enabled).append(",")
				.append("interval=").append(interval).append(",")
				.append("type=").append(type).append(",")
				.append("_interface=").append(_interface).append(",")
				.append("timeout=").append(timeout).append(",")
				.append("frame=").append(frame).append(",")
				.append("uri=").append(uri)
				.append("]")
				.toString();
	}

	// fluent

	public DatapointVO id(java.lang.String newId) {
		this.id = newId;
		return this;
	}

	public DatapointVO name(java.lang.String newName) {
		this.name = newName;
		return this;
	}

	public DatapointVO enabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
		return this;
	}

	public DatapointVO interval(java.lang.Integer newInterval) {
		this.interval = newInterval;
		return this;
	}

	public DatapointVO type(Type newType) {
		this.type = newType;
		return this;
	}

	public DatapointVO _interface(java.lang.Integer newInterface) {
		this._interface = newInterface;
		return this;
	}

	public DatapointVO timeout(java.lang.Integer newTimeout) {
		this.timeout = newTimeout;
		return this;
	}

	public DatapointVO frame(java.lang.String newFrame) {
		this.frame = newFrame;
		return this;
	}

	public DatapointVO uri(java.net.URI newUri) {
		this.uri = newUri;
		return this;
	}

	// getter/setter

	public java.lang.String getId() {
		return id;
	}

	public void setId(java.lang.String newId) {
		this.id = newId;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String newName) {
		this.name = newName;
	}

	public java.lang.Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(java.lang.Boolean newEnabled) {
		this.enabled = newEnabled;
	}

	public java.lang.Integer getInterval() {
		return interval;
	}

	public void setInterval(java.lang.Integer newInterval) {
		this.interval = newInterval;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type newType) {
		this.type = newType;
	}

	public java.lang.Integer getInterface() {
		return _interface;
	}

	public void setInterface(java.lang.Integer newInterface) {
		this._interface = newInterface;
	}

	public java.lang.Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(java.lang.Integer newTimeout) {
		this.timeout = newTimeout;
	}

	public java.lang.String getFrame() {
		return frame;
	}

	public void setFrame(java.lang.String newFrame) {
		this.frame = newFrame;
	}

	public java.net.URI getUri() {
		return uri;
	}

	public void setUri(java.net.URI newUri) {
		this.uri = newUri;
	}

@io.micronaut.serde.annotation.Serdeable
public enum Type {

	RS485("RS485"),
	S0("S0"),
	HTTP_GET("HTTP_GET");

	public static final java.lang.String RS485_VALUE = "RS485";
	public static final java.lang.String S0_VALUE = "S0";
	public static final java.lang.String HTTP_GET_VALUE = "HTTP_GET";

	private final java.lang.String value;

	private Type(java.lang.String value) {
		this.value = value;
	}

	@com.fasterxml.jackson.annotation.JsonCreator
	public static Type toEnum(java.lang.String value) {
		return toOptional(value).orElseThrow(() -> new IllegalArgumentException("Unknown value '" + value + "'."));
	}

	public static java.util.Optional<Type> toOptional(java.lang.String value) {
		return java.util.Arrays
				.stream(values())
				.filter(e -> e.value.equals(value))
				.findAny();
	}

	@com.fasterxml.jackson.annotation.JsonValue
	public java.lang.String getValue() {
		return value;
	}
}
}
