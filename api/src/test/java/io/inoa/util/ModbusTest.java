package io.inoa.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.inoa.test.AbstractTest;
import java.util.HexFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Modbus}.
 *
 * @author stephan.schnabel@grayc.de
 * @see "https://npulse.net/en/online-modbus"
 */
@DisplayName("Modbus")
public class ModbusTest extends AbstractTest {

  @Test
  void isValid() {
    var payload = "01030240C8";
    var crc = "8812";
    assertEquals(crc, HexFormat.of().formatHex(Modbus.getCRC(fromHexString(payload))), "getCrc");
    assertTrue(Modbus.isValid(fromHexString(payload + crc)), "isValid");
  }

  @Test
  void isNotValid() {
    assertFalse(Modbus.isValid(fromHexString("00")));
    assertFalse(Modbus.isValid(fromHexString("FC")));
    assertFalse(Modbus.isValid(fromHexString("01030240C88811")));
    assertFalse(Modbus.isValid(fromHexString("58060002328745")));
  }

  private byte[] fromHexString(String s) {
    return HexFormat.of().parseHex(s);
  }
}
