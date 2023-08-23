package io.inoa.measurement.translator.modbus;

import java.util.Arrays;

public class CRC16 {

	public static byte[] getCRC(byte[] bytes) {

		// CRC registers are all 1
		int crc = 0x0000ffff;

		// Polynomial check value
		int polynomial = 0x0000a001;

		int i, j;
		for (i = 0; i < bytes.length; i++) {
			crc ^= bytes[i] & 0x000000ff;
			for (j = 0; j < 8; j++) {
				if ((crc & 0x00000001) != 0) {
					crc >>= 1;
					crc ^= polynomial;
				} else {
					crc >>= 1;
				}
			}
		}

		// Result converted to hex
		String result = Integer.toHexString(crc).toUpperCase();
		if (result.length() != 4) {
			result = new StringBuffer("0000").replace(4 - result.length(), 4, result).toString();
		}

		// High position in the front position in the back
		// return result.substring(2, 4) + " " + result.substring(0, 2);
		// Exchange high low, low in front, high in back

		byte[] resultByte = new byte[2];
		resultByte[1] = (byte) (crc >> 8 & 0xff);
		resultByte[0] = (byte) (crc & 0xff);
		return resultByte;
	}

	public static boolean isValid(byte[] data) {
		var dataLength = data.length;
		if (dataLength < 2) {
			return false;
		}
		var dataPayload = Arrays.copyOfRange(data, 0, dataLength - 2);
		var dataCrc = Arrays.copyOfRange(data, dataLength - 2, dataLength);
		var payloadCrc = getCRC(dataPayload);
		return Arrays.equals(dataCrc, payloadCrc);
	}
}
