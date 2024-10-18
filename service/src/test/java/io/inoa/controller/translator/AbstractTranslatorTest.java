package io.inoa.controller.translator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.inoa.controller.translator.converter.Converter;
import io.inoa.messaging.TelemetryRawVO;
import io.inoa.rest.TelemetryVO;
import io.inoa.test.AbstractUnitTest;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.util.List;

@MicronautTest(environments = "translator")
public abstract class AbstractTranslatorTest extends AbstractUnitTest {

  protected static List<TelemetryVO> convert(
      Converter converter, TelemetryRawVO raw, String type, String sensor) {
    assertTrue(converter.supports(type, sensor), "converter supports");
    return converter.convert(raw, type, sensor).toList();
  }

  protected static void assertEmpty(
      Converter converter, TelemetryRawVO raw, String type, String sensor) {
    assertCount(0, convert(converter, raw, type, sensor));
  }

  protected static void assertSingleValue(
      Double expected, Converter converter, TelemetryRawVO raw, String type, String sensor) {
    var messages = convert(converter, raw, type, sensor);
    assertCount(1, messages);
    assertEquals(expected, messages.get(0).getValue(), "inoa message value");
  }

  protected static void assertValue(Double expected, List<TelemetryVO> telemetryList) {
    assertCount(1, telemetryList);
    assertEquals(expected, telemetryList.get(0).getValue(), "telemetry value");
  }

  protected static void assertCount(int expected, List<TelemetryVO> telemetryList) {
    assertEquals(expected, telemetryList.size(), "telemetry count: " + telemetryList);
  }
}
