package io.inoa.util;

import java.util.Arrays;

/**
 * Utility to calculate Modbus CRC16.
 *
 * @author stephan.schnabel@grayc.de
 */
public class Modbus {

	/**
	 * Calculate Modbus CRC16 for given payload.
	 *
	 * @param data Modbus payload.
	 * @return CRC16 for given payload.
	 */
	public static byte[] getCRC(byte[] data) {

		// CRC registers are all 1
		var crc = 0x0000ffff;

		// Polynomial check value
		var polynomial = 0x0000a001;

		int i, j;
		for (i = 0; i < data.length; i++) {
			crc ^= data[i] & 0x000000ff;
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
		var result = Integer.toHexString(crc).toUpperCase();
		if (result.length() != 4) {
			result = new StringBuilder("0000").replace(4 - result.length(), 4, result).toString();
		}

		// High position in the front position in the back
		// return result.substring(2, 4) + " " + result.substring(0, 2);
		// Exchange high low, low in front, high in back

		var resultByte = new byte[2];
		resultByte[1] = (byte) (crc >> 8 & 0xff);
		resultByte[0] = (byte) (crc & 0xff);
		return resultByte;
	}

	/**
	 * Checks if crc is correct in given Modbus message.
	 *
	 * @param data Modbus repsonse.
	 * @return <code>true</code> if crc matches payload
	 */
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
