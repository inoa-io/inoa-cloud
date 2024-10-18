package io.inoa.controller.translator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.inoa.messaging.KafkaHeader;
import io.inoa.messaging.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.validation.validator.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;

@KafkaListener(offsetReset = OffsetReset.EARLIEST)
@Slf4j
@RequiredArgsConstructor
public class TranslateListener {

  private final Validator validator;
  private final ObjectMapper mapper;
  private final TranslateService service;
  private final TranslateMetrics metrics;
  private final Producer<String, TelemetryVO> producer;

  @Topic(patterns = "hono\\.telemetry\\..*")
  void receive(ConsumerRecord<String, String> record) {
    log.trace("Received message:\n" + record.value());
    String tenantId = null;
    if (record.headers().lastHeader(KafkaHeader.TENANT_ID) != null) {
      var deviceId = record.headers().lastHeader(KafkaHeader.TENANT_ID);
      tenantId = new String(deviceId.value());
    } else {
      // fallback but the header should always exist
      tenantId = record.topic().substring(15);
    }
    log.trace("Receiving telemetry data for tenant {}...", tenantId);
    String gatewayId = null;

    if (record.headers().lastHeader("gatewayName") != null) {
      var deviceId = record.headers().lastHeader("gatewayName");
      gatewayId = new String(deviceId.value());
    } else if (record.headers().lastHeader(KafkaHeader.DEVICE_ID) != null) {
      var deviceId = record.headers().lastHeader(KafkaHeader.DEVICE_ID);
      gatewayId = new String(deviceId.value());
    } else {
      // fallback but the header should always exist
      gatewayId = record.key();
    }
    log.trace("Receiving telemetry data for tenant {} and gateway {} ...", tenantId, gatewayId);
    var payload = record.value();

    try {

      MDC.put("tenantId", tenantId);
      MDC.put("gatewayId", gatewayId);

      // parse payload, convert and publish

      var finalTenantId = tenantId;
      var finalGatewayId = gatewayId;
      var telemetryOptional =
          toRaw(tenantId, payload).stream()
              .map(raw -> service.translate(finalTenantId, finalGatewayId, raw))
              .flatMap(List::stream)
              .collect(Collectors.toList());
      if (telemetryOptional.isEmpty()) {
        metrics.counterIgnore(tenantId).increment();
        log.trace(
            "No telemetry data found. Ignoring message from {} in tenant {}.", gatewayId, tenantId);
        return;
      }
      for (var telemetry : telemetryOptional) {
        producer.send(
            new ProducerRecord<>(
                "inoa.telemetry." + tenantId, telemetry.getGatewayId(), telemetry));
        log.trace(
            "Sent new telemetry data into kafka topic inoa.telemetry.{} \n {}.",
            tenantId,
            telemetry);
        metrics
            .counterSuccess(tenantId, telemetry.getDeviceType(), telemetry.getSensor())
            .increment();
      }

    } finally {
      MDC.clear();
    }
  }

  private List<TelemetryRawVO> toRaw(String tenantId, String payload) {

    List<TelemetryRawVO> messages = new ArrayList<>();
    try {
      var node = mapper.readValue(payload, JsonNode.class);
      if (node.isArray()) {
        messages = mapper.readValue(payload, new TypeReference<>() {});
      } else {
        messages = new ArrayList<>();
        messages.add(mapper.readValue(payload, TelemetryRawVO.class));
      }
    } catch (JsonProcessingException e) {
      log.warn("Retrieved invalid payload: {}", payload);
      metrics.counterFailMessageRead(tenantId).increment();
      return messages;
    }

    List<TelemetryRawVO> toRemove = new ArrayList<>();
    for (var message : messages) {
      var violations = validator.validate(message);
      if (!violations.isEmpty()) {
        log.warn("Retrieved invalid payload: {}", payload);
        if (log.isDebugEnabled()) {
          violations.forEach(
              v -> log.debug("Violation: {} {}", v.getPropertyPath(), v.getMessage()));
        }
        metrics.counterFailMessageValidate(tenantId).increment();
        toRemove.add(message);
      }
    }
    for (var remove : toRemove) {
      messages.remove(remove);
    }
    return messages;
  }
}
