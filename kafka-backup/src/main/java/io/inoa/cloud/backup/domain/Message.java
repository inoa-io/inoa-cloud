package io.inoa.cloud.backup.domain;

import java.time.Instant;
import java.util.Map;

import org.apache.kafka.common.record.TimestampType;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.MappedProperty;
import io.micronaut.data.model.DataType;
import lombok.Data;

/**
 * Message.
 *
 * @author Stephan Schnabel
 */
@MappedEntity
@Data
public class Message {

	@Id
	@GeneratedValue
	private Long id;

	private String topic;
	@MappedProperty("key_")
	private String key;
	private String value;
	private Instant timestamp;
	private TimestampType timestampType;
	@MappedProperty(type = DataType.JSON)
	private Map<String, String> headers;
}
