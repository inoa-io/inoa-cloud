package io.inoa.measurement.translator.modbus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HexFormat;

import org.junit.jupiter.api.Test;

/**
 * Test for {@link CRC16}.
 *
 * @see "https://npulse.net/en/online-modbus"
 */
public class CRC16Test {

	@Test
	void isValid() {
		var payload = "01030240C8";
		var crc = "8812";
		assertEquals(crc, HexFormat.of().formatHex(CRC16.getCRC(fromHexString(payload))), "getCrc");
		assertTrue(CRC16.isValid(fromHexString(payload + crc)), "isValid");
	}

	@Test
	void isNotValid() {
		assertFalse(CRC16.isValid(fromHexString("00")));
		assertFalse(CRC16.isValid(fromHexString("FC")));
		assertFalse(CRC16.isValid(fromHexString("01030240C88811")));
		assertFalse(CRC16.isValid(fromHexString("58060002328745")));
	}

	private byte[] fromHexString(String s) {
		return HexFormat.of().parseHex(s);
	}
}
