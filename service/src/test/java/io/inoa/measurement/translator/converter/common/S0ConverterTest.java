package io.inoa.measurement.translator.converter.common;

import io.inoa.measurement.translator.AbstractTranslatorTest;
import io.inoa.measurement.translator.converter.LogSink;
import io.inoa.messaging.TelemetryRawVO;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link S0Converter}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("translator: converter s0")
public class S0ConverterTest extends AbstractTranslatorTest {

  @Inject S0Converter converter;

  @BeforeEach
  void setup() {
    LogSink.setUp(S0Converter.class);
  }

  @DisplayName("success: with modifier")
  @Test
  void successWithModifier() {
    var telemetry = TelemetryRawVO.of("urn:s0:0:gas", "1234567");
    assertSingleValue(1234.567, converter, telemetry, "s0", "gas");
    LogSink.assertMessage(S0Converter.class, "S0 count has value: 1234567");
  }

  @DisplayName("fail: count empty")
  @Test
  void failByteCountEmpty() {
    assertEmpty(converter, TelemetryRawVO.of("urn:s0:0:gas", ""), "s0", "gas");
    LogSink.assertMessage(S0Converter.class, "Retrieved invalid S0 message (empty)");
  }

  @DisplayName("fail: count NaN")
  @Test
  void failByteCountNaN() {
    assertEmpty(converter, TelemetryRawVO.of("urn:s0:0:gas", "DreiZehnKommaZweiEins"), "s0", "gas");
    LogSink.assertMessage(S0Converter.class, "Retrieved invalid S0 message (not a number)");
  }

  @DisplayName("fail: count negative")
  @Test
  void failByteCountNegative() {
    assertEmpty(converter, TelemetryRawVO.of("urn:s0:0:gas", -42), "s0", "gas");
    LogSink.assertMessage(S0Converter.class, "Retrieved invalid S0 message (negative value)");
  }
}
