package io.inoa.measurement.things.builder.modbus;

import java.util.Base64;

import io.inoa.util.Modbus;

public class Utils {

	public static byte[] buildFrame(byte slaveId, byte functionCode, short registerOffset, short numberOfRegisters) {
		byte[] bytes = new byte[6];
		bytes[0] = slaveId;
		bytes[1] = functionCode;
		bytes[2] = (byte) ((registerOffset >> 8) & 0xff);
		bytes[3] = (byte) (registerOffset & 0xff);
		bytes[4] = (byte) ((numberOfRegisters >> 8) & 0xff);
		bytes[5] = (byte) (numberOfRegisters & 0xff);
		byte[] target = new byte[8];
		target[0] = bytes[0];
		target[1] = bytes[1];
		target[2] = bytes[2];
		target[3] = bytes[3];
		target[4] = bytes[4];
		target[5] = bytes[5];
		byte[] crc = Modbus.getCRC(bytes);
		target[6] = crc[0];
		target[7] = crc[1];
		return target;
	}

	public static String toBase64(byte[] frame) {
		return Base64.getEncoder().encodeToString(frame);
	}

	// urn:dvh4013:12345-01:0x4001
	public static String buildUrn(String serial, String urnPrefix, String urnPostfix) {
		return String.format("urn:%s:%s:%s", urnPrefix, serial, urnPostfix);
	}
}
