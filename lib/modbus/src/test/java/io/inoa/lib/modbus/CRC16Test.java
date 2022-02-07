package io.inoa.lib.modbus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertEquals(crc, toHexString(CRC16.getCRC(fromHexString(payload))), "getCrc");
		assertTrue(CRC16.isValid(fromHexString(payload + crc)), "isValid");
	}

	@Test
	void isNotValid() {
		assertFalse(CRC16.isValid(fromHexString("00")));
		assertFalse(CRC16.isValid(fromHexString("FC")));
		assertFalse(CRC16.isValid(fromHexString("01030240C88811")));
		assertFalse(CRC16.isValid(fromHexString("58060002328745")));
	}

	// FIXME Replace with jdk17: HexFormat.of().formatHex(bytes);
	private String toHexString(byte[] data) {
		var digits = "0123456789ABCDEF".toCharArray();
		var buf = new StringBuilder();
		for (byte element : data) {
			buf.append(digits[element >> 4 & 0x0f]);
			buf.append(digits[element & 0x0f]);
		}
		return buf.toString();
	}

	private byte[] fromHexString(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
