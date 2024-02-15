package io.inoa.rest;

@jakarta.annotation.Generated("org.openapitools.codegen.languages.MicronautCodegen")
@io.micronaut.core.annotation.Introspected
public class LogEventVO {

	public static final java.lang.String JSON_PROPERTY_TAG = "tag";
	public static final java.lang.String JSON_PROPERTY_LEVEL = "level";
	public static final java.lang.String JSON_PROPERTY_TIME = "time";
	public static final java.lang.String JSON_PROPERTY_MSG = "msg";

	/** Log tag */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TAG)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String tag;

	/** Log level */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Min(0)
	@javax.validation.constraints.Max(5)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_LEVEL)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Integer level;

	/** Log time */
	@javax.validation.constraints.NotNull
	@javax.validation.constraints.Min(0)
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_TIME)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.Long time;

	/** Log message */
	@javax.validation.constraints.NotNull
	@com.fasterxml.jackson.annotation.JsonProperty(JSON_PROPERTY_MSG)
	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
	private java.lang.String msg;

	// methods

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		LogEventVO other = (LogEventVO) object;
		return java.util.Objects.equals(tag, other.tag)
				&& java.util.Objects.equals(level, other.level)
				&& java.util.Objects.equals(time, other.time)
				&& java.util.Objects.equals(msg, other.msg);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(tag, level, time, msg);
	}

	@Override
	public java.lang.String toString() {
		return new java.lang.StringBuilder()
				.append("LogEventVO[")
				.append("tag=").append(tag).append(",")
				.append("level=").append(level).append(",")
				.append("time=").append(time).append(",")
				.append("msg=").append(msg)
				.append("]")
				.toString();
	}

	// fluent

	public LogEventVO tag(java.lang.String newTag) {
		this.tag = newTag;
		return this;
	}

	public LogEventVO level(java.lang.Integer newLevel) {
		this.level = newLevel;
		return this;
	}

	public LogEventVO time(java.lang.Long newTime) {
		this.time = newTime;
		return this;
	}

	public LogEventVO msg(java.lang.String newMsg) {
		this.msg = newMsg;
		return this;
	}

	// getter/setter

	public java.lang.String getTag() {
		return tag;
	}

	public void setTag(java.lang.String newTag) {
		this.tag = newTag;
	}

	public java.lang.Integer getLevel() {
		return level;
	}

	public void setLevel(java.lang.Integer newLevel) {
		this.level = newLevel;
	}

	public java.lang.Long getTime() {
		return time;
	}

	public void setTime(java.lang.Long newTime) {
		this.time = newTime;
	}

	public java.lang.String getMsg() {
		return msg;
	}

	public void setMsg(java.lang.String newMsg) {
		this.msg = newMsg;
	}
}
