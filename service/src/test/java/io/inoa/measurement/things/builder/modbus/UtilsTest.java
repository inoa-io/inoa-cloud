package io.inoa.measurement.things.builder.modbus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UtilsTest {
	@Test
	public void testFrameCreation() {
		var frame = Utils.buildFrame((byte) 9, (byte) 3, (short) 16385, (short) 2);
		String base64 = Utils.toBase64(frame);
		assertEquals("CQNAAQACgUM=", base64);
	}
}
