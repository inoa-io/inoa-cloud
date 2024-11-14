package io.inoa.measurement.translator.converter.common;

import static io.inoa.measurement.translator.converter.LogSink.assertMessage;

import io.inoa.measurement.translator.AbstractTranslatorTest;
import io.inoa.measurement.translator.converter.LogSink;
import io.inoa.messaging.TelemetryRawVO;
import jakarta.inject.Inject;
import java.util.HexFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link ModbusConverter}.
 *
 * @author stephan.schnabel@grayc.de
 */
@DisplayName("translator: converter modbus")
public class ModbusConverterTest extends AbstractTranslatorTest {

  @Inject ModbusConverter converter;

  @BeforeEach
  void setup() {
    LogSink.setUp(ModbusConverter.class);
  }

  @DisplayName("success: without modifier")
  @Test
  void successWithoutModifier() {
    var hex = "08030240C85413";
    assertSingleValue(
        16584D, converter, telementry("urn:example:0815:modbus", hex), "example", "modbus");
    assertMessage(
        ModbusConverter.class, "Modbus with slaveId 08, functionCode 03 has value: 16584");
  }

  @DisplayName("success: with modifier")
  @Test
  void successWithModifier() {
    var hex = "08030240C85413";
    var telemetry = telementry("urn:example:0815:modbus-with-modifier", hex);
    assertSingleValue(165840D, converter, telemetry, "example", "modbus-with-modifier");
    assertMessage(
        ModbusConverter.class, "Modbus with slaveId 08, functionCode 03 has value: 16584");
  }

  @DisplayName("fail: payload with invalid crc16")
  @Test
  void failPayloadCrcInvalid() {
    var hex = "010301105044";
    assertEmpty(converter, telementry("urn:example:0815:modbus", hex), "example", "modbus");
    assertMessage(ModbusConverter.class, "Retrieved invalid modbus message (crc16): " + hex);
  }

  @DisplayName("fail: payload too short")
  @Test
  void failPayloadToShort() {
    var hex = "fc";
    assertEmpty(converter, telementry("urn:example:0815:modbus", hex), "example", "modbus");
    assertMessage(ModbusConverter.class, "Retrieved invalid modbus message (too short): " + hex);
  }

  @DisplayName("fail: function code invalid")
  @Test
  void failFunctionCodeInvalid() {
    var hex = "010101105044";
    assertEmpty(converter, telementry("urn:example:0815:modbus", hex), "example", "modbus");
    assertMessage(
        ModbusConverter.class, "Retrieved invalid modbus message (functionCode 01): " + hex);
  }

  @DisplayName("fail: exception code")
  @Test
  void failExceptionCode() {
    var hex = "0A8102B053";
    assertEmpty(converter, telementry("urn:example:0815:modbus", hex), "example", "modbus");
    assertMessage(
        ModbusConverter.class, "Retrieved modbus error message (functionCode 81) with error 2");
  }

  @DisplayName("fail: byte count invalid")
  @Test
  void failByteCountInvalid() {
    var hex = "01030210f174";
    assertEmpty(converter, telementry("urn:example:0815:modbus", hex), "example", "modbus");
    assertMessage(
        ModbusConverter.class,
        "Retrieved invalid modbus message (data.length 1 < byteCount 2): " + hex);
  }

  @DisplayName("fail: byte count empty")
  @Test
  void failByteCountEmpty() {
    var hex = "01030020f0";
    assertEmpty(converter, telementry("urn:example:0815:modbus", hex), "example", "modbus");
    assertMessage(
        ModbusConverter.class, "Retrieved invalid modbus message (byteCount == 0): " + hex);
  }

  private TelemetryRawVO telementry(String urn, String hex) {
    return TelemetryRawVO.of(urn, HexFormat.of().parseHex(hex));
  }
}
