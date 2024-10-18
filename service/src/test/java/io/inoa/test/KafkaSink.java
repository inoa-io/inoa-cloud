package io.inoa.test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.rest.TelemetryVO;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

@SuppressWarnings({"rawtypes", "unchecked"})
@Factory
@Slf4j
public class KafkaSink {

  private final Map<String, Map<String, List<ProducerRecord<String, ?>>>> records = new HashMap<>();
  private final Producer producer =
      new Producer() {

        @Override
        public Future<RecordMetadata> send(ProducerRecord record) {
          records
              .computeIfAbsent(record.topic(), topic -> new HashMap<>())
              .computeIfAbsent((String) record.key(), key -> new ArrayList<>())
              .add(record);
          log.info(
              "Got record for {} with key {} and value {}={}",
              record.topic(),
              record.key(),
              record.value().getClass().getName(),
              record.value());
          return CompletableFuture.completedFuture(null);
        }

        @Override
        public Future send(ProducerRecord record, Callback callback) {
          throw new UnsupportedOperationException();
        }

        @SuppressWarnings("deprecation")
        @Deprecated
        @Override
        public void sendOffsetsToTransaction(Map offsets, String consumerGroupId) {}

        @Override
        public void sendOffsetsToTransaction(Map offsets, ConsumerGroupMetadata groupMetadata) {}

        @Override
        public void initTransactions() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void beginTransaction() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void commitTransaction() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void abortTransaction() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void flush() {
          throw new UnsupportedOperationException();
        }

        @Override
        public List partitionsFor(String topic) {
          throw new UnsupportedOperationException();
        }

        @Override
        public Map metrics() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
          throw new UnsupportedOperationException();
        }

        @Override
        public void close(Duration timeout) {
          throw new UnsupportedOperationException();
        }
      };

  public ProducerRecord<String, Map<String, String>> awaitHonoEvent(
      String tenantId, String gatewayId) {
    ProducerRecord record = awaitRecord("hono.event." + tenantId, gatewayId);
    if (record.value() instanceof byte[] bytes) {
      return new ProducerRecord(
          record.topic(),
          record.partition(),
          record.key(),
          assertDoesNotThrow(() -> Map.copyOf(new ObjectMapper().readValue(bytes, Map.class))),
          record.headers());
    }
    return record;
  }

  public ProducerRecord<String, byte[]> awaitHonoTelemetry(String tenantId, String gatewayId) {
    return awaitRecord("hono.telemetry." + tenantId, gatewayId);
  }

  public ProducerRecord<String, TelemetryVO> getInoaTelemetry(String tenantId, String gatewayId) {
    return getRecord("inoa.telemetry." + tenantId, gatewayId);
  }

  <T> ProducerRecord<String, T> awaitRecord(String topic, String key) {
    return Await.await(log, "wait for kafka message on topic " + topic + " with key " + key)
        .until(() -> getRecord(topic, key), record -> record != null);
  }

  <T> ProducerRecord<String, T> getRecord(String topic, String key) {
    var topicRecords = records.get(topic);
    assertNotNull(topicRecords, () -> topic + " not found, available: " + records.keySet());
    var keyRecords = topicRecords.get(key);
    assertNotNull(
        keyRecords, () -> topic + "/" + key + " not found, available: " + topicRecords.keySet());
    assertEquals(1, keyRecords.size(), () -> topic + "/" + key + ", got: " + keyRecords);
    var record = keyRecords.get(0);
    assertEquals(topic, record.topic(), "topic");
    assertEquals(key, record.key(), "key");
    keyRecords.remove(record);
    return (ProducerRecord<String, T>) record;
  }

  void reset() {
    records.clear();
  }

  @Singleton
  Producer<String, byte[]> producerByteArray() {
    return producer;
  }

  @Singleton
  Producer<String, Map<String, String>> producerMapStrings() {
    return producer;
  }

  @Singleton
  Producer<String, TelemetryVO> producerTelemetry() {
    return producer;
  }
}
