package io.inoa.fleet.thing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.inoa.fleet.thing.driver.modbus.Utils;

public class UtilsTest {
	@Test
	public void testFrameCreation() {
		var frame = Utils.buildFrame((byte) 9, (byte) 3, (short) 16385, (short) 2);
		String base64 = Utils.toBase64(frame);
		assertEquals("CQNAAQACgUM=", base64);
	}
}
