package io.inoa.measurement.translator.messaging;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import io.inoa.measurement.telemetry.TelemetryVO;
import jakarta.inject.Singleton;
import lombok.Getter;

@Singleton
@Getter
public class MessagingSink implements Producer<UUID, TelemetryVO> {

	private final List<ProducerRecord<UUID, TelemetryVO>> records = new ArrayList<>();

	public void deleteAll() {
		records.clear();
	}

	@Override
	public Future<RecordMetadata> send(ProducerRecord<UUID, TelemetryVO> record) {
		records.add(record);
		return null;
	}

	// mock

	@Override
	public void initTransactions() {}

	@Override
	public void beginTransaction() {}

	@Override
	public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, String consumerGroupId) {}

	@Override
	public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, ConsumerGroupMetadata meta) {}

	@Override
	public void commitTransaction() {}

	@Override
	public void abortTransaction() {}

	@Override
	public Future<RecordMetadata> send(ProducerRecord<UUID, TelemetryVO> record, Callback callback) {
		return null;
	}

	@Override
	public List<PartitionInfo> partitionsFor(String topic) {
		return null;
	}

	@Override
	public Map<MetricName, ? extends Metric> metrics() {
		return null;
	}

	@Override
	public void flush() {}

	@Override
	public void close(Duration timeout) {}

	@Override
	public void close() {}
}
