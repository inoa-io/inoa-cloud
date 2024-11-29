package io.inoa.measurement.translator.converter.common;

import io.inoa.measurement.translator.TranslatorProperties;
import io.inoa.messaging.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Singleton;
import java.util.stream.Stream;

@Singleton
public class S0Converter extends CommonConverter {

  public static final String COUNTER_FAIL_NAN = "translator_s0_fail_nan";
  public static final String COUNTER_FAIL_NEGATIVE = "translator_s0_fail_negative";
  public static final String COUNTER_FAIL_EMPTY = "translator_s0_fail_empty";
  public static final String COUNTER_SUCCESS = "translator_s0_success";

  public S0Converter(TranslatorProperties properties, MeterRegistry meterRegistry) {
    super(properties, meterRegistry, "s0");
  }

  @Override
  public Stream<TelemetryVO> convert(TelemetryRawVO raw, String type, String sensor) {

    long value;

    // Check empty message
    if (raw.value().length == 0 || new String(raw.value()).isEmpty()) {
      increment(type, COUNTER_FAIL_EMPTY);
      log.trace("Retrieved invalid S0 message (empty)");
      return Stream.empty();
    }

    // Check NaN
    try {
      value = Long.parseLong(new String(raw.value()));
    } catch (NumberFormatException e) {
      increment(type, COUNTER_FAIL_NAN);
      log.trace("Retrieved invalid S0 message (not a number)");
      return Stream.empty();
    }

    // Check negative message
    if (value < 0) {
      increment(type, COUNTER_FAIL_NEGATIVE);
      log.trace("Retrieved invalid S0 message (negative value)");
      return Stream.empty();
    }

    log.trace("S0 count has value: {}", value);
    increment(type, COUNTER_SUCCESS);

    return Stream.of(convert(type, sensor, (double) value));
  }
}
